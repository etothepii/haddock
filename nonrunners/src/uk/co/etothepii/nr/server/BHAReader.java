/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.nr.server;

import haddocknr.Main;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import org.htmlcleaner.TagNode;
import uk.co.etothepii.comms.Message;
import uk.co.etothepii.comms.TelnetMessageClient;
import uk.co.etothepii.haddock.db.factories.AliasFactory;
import uk.co.etothepii.haddock.db.factories.NonrunnerFactory;
import uk.co.etothepii.haddock.db.factories.RaceFactory;
import uk.co.etothepii.haddock.db.factories.RacecourseFactory;
import uk.co.etothepii.haddock.db.tables.Alias;
import uk.co.etothepii.haddock.db.tables.Nonrunner;
import uk.co.etothepii.haddock.db.tables.Race;
import uk.co.etothepii.haddock.db.tables.Racecourse;
import uk.co.etothepii.haddock.db.tables.enumerations.RacecourseVendor;
import uk.co.etothepii.haddock.db.tables.enumerations.Vendor;
import uk.co.etothepii.util.ThreadController;
import uk.co.etothepii.util.ThreadControllerStopException;
import uk.co.etothepii.util.screenscraping.TextHtmlProcessor;
import uk.co.etothepii.util.screenscraping.WebPage;
import uk.co.etothepii.util.screenscraping.WebPageDownloader;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class BHAReader {

    private static final Logger LOG = Logger.getLogger(BHAReader.class);
    
    public static final long BASE_SLEEP = 15000L;
    public static final URL BASE_URL;
    public static final File BASE_FILE;
    
    static {
        URL temp = null;
        try {
             temp = new URL(
                     "http://www.britishhorseracing.com/"
                     + "goracing/racing/nonrunners/default.asp");
        }
        catch (MalformedURLException mue) {
            LOG.error(mue.getMessage(), mue);
        }
        BASE_URL = temp;
        LOG.debug(BASE_URL);
        BASE_FILE = new File(System.getProperty(
                "user.home").concat("/.haddockNR/"));
        LOG.debug(BASE_FILE);
    }

    private File logDirectory;
    private URL url;
    private WebPage last;
    private WebPageDownloader downloader;
    private TextHtmlProcessor processor;
    private SimpleDateFormat simpleDateFormat =
            new SimpleDateFormat("yyyyMMddHHmmss");
    private ThreadController downloadThread;
    private long start;
    private String lastString;
    private TelnetMessageClient client;
    private UnknownSelectionProcessor uap;

    public BHAReader() throws IOException {
        this(BASE_FILE, BASE_URL, new TelnetMessageClient(Main.BROADCAST_PORTS,
                "localhost"),
                new UnknownSelectionProcessor());
    }

    public BHAReader(File logDirectory, URL url, TelnetMessageClient client, UnknownSelectionProcessor uap) {
        this.uap = uap;
        this.client = client;
        this.logDirectory = logDirectory;
        this.url = url;
        start = System.currentTimeMillis();
        downloader = new WebPageDownloader();
        processor = new TextHtmlProcessor() {};
        last = download();
        lastString = null;
        downloadThread = new ThreadController(new Runnable() {
            public void run() {
                long time = System.currentTimeMillis();
                long sleep = BASE_SLEEP + start - time;
                if (sleep > 0) {
                    try {
                        Thread.sleep(sleep);
                    }
                    catch (InterruptedException ie) {
                        throw new ThreadControllerStopException();
                    }
                }
                start += BASE_SLEEP;
                try {
                    WebPage w = download();
                    TagNode root = processor.getRootNode(w);
                    String source = processor.getSourceCode(root, 0);
                    boolean seenChange = lastString == null ||
                            !source.equals(lastString);
                    LOG.debug("processing change: " + seenChange);
                    if (seenChange) {
                        last = w;
                        lastString = source;
                        output(source);
                        process(root);
                    }
                }
                catch (Throwable t) {
                    LOG.error(t.getMessage(), t);
                }
            }
        });
        downloadThread.start();
    }

    private WebPage download() {
        return downloader.download(url);
    }
    
    private static final TreeMap<String, String> meetingAttributes = 
            new TreeMap<String, String>();
    private static final TreeMap<String, String> raceAttributes = 
            new TreeMap<String, String>();
    private static final TreeMap<String, String> horseAttributes = 
            new TreeMap<String, String>();
    
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
    
    static {
        meetingAttributes.put("class", "goingItem");
        raceAttributes.put("class", "race");
        horseAttributes.put("class", "horse");
    }

    private void processHorse(TagNode horseNode, Race race)
            throws ParseException, SQLException {
        ArrayList<String> horseStrings =
                processor.getContentStrings(horseNode);
        String horseName = horseStrings.get(0);
        Alias a = AliasFactory.getFactory(
                ).getFromAliasAndVendor(horseName, Vendor.BHA);
        if (a == null) {
            a = new Alias(null, horseName, Vendor.BHA);
            AliasFactory.getFactory().save(a);
            unknownSelection(new UnknownSelection(a, race));
        }
        if (a.getSelection() == null) {
            unknownSelection(new UnknownSelection(a, race));
        }
        Date declaredAt =
                sdf.parse(horseStrings.get(1).split(": ")[1]);
        String reason = horseStrings.get(2).split(": ")[1];
        Nonrunner n = NonrunnerFactory.getFactory(
                ).getFromAliasAndRace(a, race);
        if (n == null) {
            n = new Nonrunner(a, race, new Date(), null, declaredAt, null, null);
            NonrunnerFactory.getFactory().save(n);
            newNonrunner(n);
        }
    }
    
    private void processRace(TagNode raceNode, Racecourse racecourse, 
            String dateStr) throws ParseException, SQLException {
        ArrayList<String> raceStrings =
                processor.getContentStrings(raceNode, 1);
        String[] raceParts = raceStrings.get(0).split(" - ");
        Date scheduledOff = sdf.parse(dateStr + " " + raceParts[0]);
        String raceName = raceParts[1];
        Race race = RaceFactory.getFactory(
                ).getFromRacecourseAndScheduled(racecourse, scheduledOff);
        if (race == null) {
            race = new Race(null, null, scheduledOff, racecourse, raceName,
                    null, null, null, null);
        }
        race.setName(raceName);
        RaceFactory.getFactory().save(race);
        for (TagNode horseNode : processor.getTagsEquals(
                raceNode, "div", horseAttributes)) {
            try {
                processHorse(horseNode, race);
            }
            catch (ParseException pe) {
                LOG.error(pe.getMessage(), pe);
            }
            catch (SQLException sqle) {
                LOG.error(sqle.getMessage(), sqle);
            }
        }
    }

    private void processMeeting(TagNode meeting)
            throws ParseException, SQLException {
        ArrayList<String> meetingStrings =
                processor.getContentStrings(meeting, 1);
        String racecourseName = capitalize(meetingStrings.get(0));
        String dateStr = meetingStrings.get(2);
        Racecourse racecourse = RacecourseFactory.getFactory().getFromName(
                racecourseName, RacecourseVendor.BHA);
        if (racecourse == null) {
            unknownRacecourse(racecourseName);
            return;
        }
        for (TagNode raceNode : processor.getTagsEquals(
                meeting, "div", raceAttributes)) {
            try {
                processRace(raceNode, racecourse, dateStr);
            }
            catch (ParseException pe) {
                LOG.error(pe.getMessage(), pe);
            }
            catch (SQLException sqle) {
                LOG.error(sqle.getMessage(), sqle);
            }
        }
    }

    private void process(TagNode t) {
        ArrayList<TagNode> meetings = processor.getTagsEquals(
                t, "div", meetingAttributes);
        LOG.debug("Found " + meetings.size() + " meetings");
        for (TagNode meeting : meetings) {
            try {
                processMeeting(meeting);
            }
            catch (ParseException pe) {
                LOG.error(pe.getMessage(), pe);
            }
            catch (SQLException sqle) {
                LOG.error(sqle.getMessage(), sqle);
            }
        }
    }

    private void output(String source) {
        File to = new File(logDirectory, simpleDateFormat.format(
                new Date(System.currentTimeMillis())));
        try {
            to.getParentFile().mkdirs();
            to.createNewFile();
            FileWriter fw = new FileWriter(to);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(source);
            pw.flush();
            pw.close();
            fw.close();
        }
        catch (IOException ioe) {
            LOG.error(ioe.getMessage(), ioe);
        }
    }
    
    private static String capitalize(String s) {
        String[] words = s.split(" ");
        StringBuilder sb = new StringBuilder(s.length());
        sb.append(capitalizeWord(words[0]));
        for (int i = 1; i < words.length; i++) {
            sb.append(" ");
            sb.append(capitalizeWord(words[i]));
        }
        return sb.toString();
    }

    private static String capitalizeWord(String word) {
        String lower = word.toLowerCase();
        return lower.substring(0, 1).toUpperCase() + lower.substring(1);
    }

    private void unknownRacecourse(String racecourseName) {
        client.sendMessage(new Message("UNKNOWNRACECOURSE", racecourseName));
    }

    private void unknownSelection(UnknownSelection a) {
        LOG.debug("Unknown Alias " + a.getAlais().getAlias());
        boolean found;
        try {
            found = uap.process(a);
        }
        catch (Throwable t) {
            LOG.error(t.getMessage(), t);
            found = false;
        }
        if (!found)
            client.sendMessage(new Message("UNKNOWNALIAS",
                    a.getAlais().getId() + "~" + a.getRace().getId()));
    }

    private void newNonrunner(Nonrunner n) {
        client.sendMessage(new Message("NEWNONRUNNER", n.getId() + ""));
    }

}
