/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.racingpostforecast;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import org.htmlcleaner.TagNode;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.MasterController;
import uk.co.etothepii.haddock.db.factories.RaceFactory;
import uk.co.etothepii.haddock.db.factories.RacecourseFactory;
import uk.co.etothepii.haddock.db.tables.Race;
import uk.co.etothepii.haddock.db.tables.Racecourse;
import uk.co.etothepii.haddock.db.tables.enumerations.RacecourseVendor;
import uk.co.etothepii.util.screenscraping.TextHtmlProcessor;
import uk.co.etothepii.util.screenscraping.WebPage;

/**
 *
 *
 * @author jrrpl
 */

public class MainPageProcessor extends TextHtmlProcessor {

    private static final String STEM = "http://www.racingpost.com";

    private static final Logger LOG = Logger.getLogger(MainPageProcessor.class);
    private static final TreeMap <String, String> CR_BLOCK_ATTRIBS = 
            new TreeMap<String, String>();
    private static final TreeMap <String, String> RACE_HEAD_ATTRIBS = 
            new TreeMap<String, String>();
    private static final TreeMap <String, String> CARDS_GRID_ATTRIBS = 
            new TreeMap<String, String>();
    private static final TreeMap <String, String> MEETING_ATTRIBS =
            new TreeMap<String, String>();
    private static final TreeMap <String, String> EMPTY_ATTRIBS =
            new TreeMap<String, String>();
    private static final TreeMap <String, String> TIME_ATTRIBS =
            new TreeMap<String, String>();

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    static {
        CR_BLOCK_ATTRIBS.put("class", "crBlock");
        RACE_HEAD_ATTRIBS.put("class", "raceHead");
        CARDS_GRID_ATTRIBS.put("class", "cardsGrid");
        MEETING_ATTRIBS.put("class", "meeting");
        TIME_ATTRIBS.put("class", "rTime");
    }

    public MainPageProcessor() {
    }

    public ArrayList<Result> process(WebPage webPage) throws SQLException {
        ArrayList<Result> toRet = new ArrayList<Result>();
        TagNode root = getRootNode(webPage);
        LOG.debug("Got root node");
        ArrayList<TagNode> cardsGrids = 
                getTagsEquals(root, "div", CR_BLOCK_ATTRIBS);
        for (TagNode cardsGrid : cardsGrids)
            toRet.addAll(processCardsGrid(cardsGrid));
        return toRet;
    }

    private ArrayList<Result> processCardsGrid(TagNode t) throws SQLException {
        ArrayList<Result> toRet = new ArrayList<Result>();
        TagNode raceHead = getFirstTagEquals(t, "table", RACE_HEAD_ATTRIBS);
        if (raceHead == null) {
            LOG.debug("No race head");
            return toRet;
        }
        LOG.debug("Got race head");
        TagNode meeting = raceHead.findElementByAttValue("class", "meeting",
                true, false);
        LOG.debug("Got meeting");
        TagNode anchor = getFirstTagContains(meeting, "a", EMPTY_ATTRIBS);
        LOG.debug("Got anchor");
        String courseName = getContents(anchor).trim();

        Racecourse racecourse = RacecourseFactory.getFactory().getFromName(
                courseName, RacecourseVendor.RP);
        LOG.debug("First attempt at getting racecourse called " + courseName);
        if (racecourse == null) {
            RPCourse rpCourse = new RPCourse(courseName);
            String country;
            if (rpCourse.modifyer == null || rpCourse.modifyer.equals("AW")) {
                country = "UK";
            }
            else {
                country = rpCourse.modifyer;
            }
            LOG.debug(country);
            racecourse = RacecourseFactory.getFactory(
                    ).getFromNameAndCountry(rpCourse.name, country);
            if (racecourse == null && !country.equals("UK") &&
                    !country.equals("IRE")) {
                racecourse = new Racecourse(rpCourse.name, null, null, null,
                        null, null, null, null, country, null);
                RacecourseFactory.getFactory().save(racecourse);
                LOG.debug("saved racecourse");
            }
            DatabaseConduit conduit = MasterController.takeConduit();
            PreparedStatement ps = conduit.prepareStatement("INSERT INTO "
                    + "`racecourseAliases` (`id`, `alias`, `vendor`, "
                    + "`racecourse`) VALUES (NULL, ?, 'RP', ?)");
            ps.setString(1, courseName);
            if (racecourse != null)
                ps.setLong(2, racecourse.getId());
            else
                ps.setNull(2, Types.INTEGER);
            ps.execute();
            ps.close();
            MasterController.releaseConduit(conduit);
            if (racecourse == null) {
                System.err.println("Failed to find racecourse: " + courseName);
                LOG.debug("Failed to find racecourse: " + courseName);
                return toRet;
            }
        }
        if (racecourse.getCountry().equals("UK") ||
                racecourse.getCountry().equals("IRE")) {
            TagNode cardsGrid = getFirstTagEquals(t, "table", CARDS_GRID_ATTRIBS);
            ArrayList<TagNode> rows = getTagsEquals(cardsGrid, "tr", EMPTY_ATTRIBS);
            for (TagNode row : rows) {
                TagNode timeCell = getFirstTagEquals(row, "th", TIME_ATTRIBS);
                if (timeCell == null) continue;
                TagNode timeAnchor= getFirstTagContains(timeCell, "a", EMPTY_ATTRIBS);
                if (timeAnchor == null) continue;
                String time = getContents(timeAnchor);
                String href = timeAnchor.getAttributeByName("href");
                if (LOG.isDebugEnabled()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(racecourse.getName());
                    sb.append(" ");
                    sb.append(time);
                    sb.append(" ");
                    sb.append(href);
                    LOG.debug(sb);
                }
                try {
                    String[] split = time.split(":");
                    String rawDateStr = href.split("r_date=")[1];
                    LOG.debug("rawDateStr: " + rawDateStr);
                    StringBuilder dateStr = new StringBuilder(rawDateStr);
                    dateStr.append(" ");
                    dateStr.append(Integer.parseInt(split[0]) + 12);
                    dateStr.append(":");
                    dateStr.append(Integer.parseInt(split[1]));
                    Date date = sdf.parse(dateStr.toString());
                    URL url = new URL(STEM.concat(href));
                    Race race = RaceFactory.getFactory(
                            ).getFromRacecourseAndScheduled(racecourse, date);
                    if (race != null) {
                        toRet.add(new Result(race, date, url));
                    }
                    else {
                        LOG.error("Failed to find " +
                                dateStr + " " + racecourse.getName());
                    }

                }
                catch (MalformedURLException mue) {
                    LOG.error(mue.getMessage(), mue);
                }
                catch (ArrayIndexOutOfBoundsException aioobe) {
                    LOG.error(aioobe.getMessage(), aioobe);
                }
                catch (ParseException pe) {
                    LOG.error(pe.getMessage(), pe);
                }
            }
        }
        return toRet;
    }

    private class RPCourse {
        public final String name;
        public final String modifyer;

        public RPCourse(String name) {
            String[] parts = name.split(" \\(");
            if (parts.length == 1) {
                this.name = name;
                modifyer = null;
            }
            else {
                this.name = parts[0];
                parts = parts[1].split("\\)");
                modifyer = parts[0];
            }
        }


    }

    public static class Result {

        public final Race race;
        public final Date date;
        public final URL url;

        public Result(Race race, Date date, URL url) {
            this.race = race;
            this.date = date;
            this.url = url;
        }

    }

}


