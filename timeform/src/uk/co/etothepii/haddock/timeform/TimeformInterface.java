/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.timeform;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.apache.log4j.Logger;
import org.htmlcleaner.TagNode;
import uk.co.etothepii.util.screenscraping.WebPage;
import uk.co.etothepii.util.screenscraping.WebPageDownloader;

/**
 *
 * @author jrrpl
 */
public class TimeformInterface {

    private static final Logger LOG = Logger.getLogger(TimeformInterface.class);

    public static final String TIMEFORM_RACECARD_STEM =
            "http://form.horseracing.betfair.com/daypage?date=";

    private final SimpleDateFormat timeformRacecardDateFormat;
    private final WebPageDownloader downloader;
    private final RaceCardProcessor raceCardProcessor;
    private final MeetingResultsProcessor meetingResultsProcessor;
    private final RaceResultProcessor raceResultProcessor;

    public TimeformInterface() {
        timeformRacecardDateFormat = new SimpleDateFormat("yyyyMMdd");
        raceCardProcessor = new RaceCardProcessor();
        meetingResultsProcessor = new MeetingResultsProcessor();
        raceResultProcessor = new RaceResultProcessor();
        downloader = new WebPageDownloader();
    }

    public void download (Date date) {
        download(date, null);
    }

    public void download(Date date, File saveDirectory) {
        String formattedDate = timeformRacecardDateFormat.format(date);
        try {
            URL url = new URL(TIMEFORM_RACECARD_STEM.concat(formattedDate));
            LOG.debug("");
            LOG.debug(url);
            WebPage webPage = downloader.download(url);
            ArrayList<TimeformRaceCard> timeformRaceCards =
                    raceCardProcessor.process(webPage);
            for (int i = 0; i < timeformRaceCards.size(); i++) {
                TimeformRaceCard trc = timeformRaceCards.get(i);
                if (!trc.countryCode.equals("GB") &&
                        !trc.countryCode.equals("IRE")
                        && !trc.countryCode.equals("IE")
                        ) {
                    LOG.debug(trc.racecourse);
                    timeformRaceCards.remove(i--);
                }
            }
            for (TimeformRaceCard trc : timeformRaceCards) {
                WebPage meetingWebPage = downloader.download(trc.url);
                TagNode root =
                        meetingResultsProcessor.getRootNode(
                        meetingWebPage);
                if (saveDirectory != null) {
                    File f = new File(saveDirectory.getAbsolutePath(
                            ).concat(trc.url.getPath()));
                    LOG.debug(f.getAbsolutePath());
                    f.getParentFile().mkdirs();
                    try {
                        f.createNewFile();
                        FileWriter fw = new FileWriter(f, false);
                        PrintWriter pw = new PrintWriter(fw, true);
                        pw.println(
                                meetingResultsProcessor.getSourceCode(root, 0));
                        pw.flush();
                        pw.close();
                        fw.close();
                    }
                    catch (IOException ioe) {
                        LOG.error(ioe.getMessage(), ioe);
                    }
                }
                ArrayList<ResultsTable> resultsTables =
                        meetingResultsProcessor.process(root, date);
                if (resultsTables != null) {
                    for (ResultsTable resultsTable : resultsTables) {
                        raceResultProcessor.process(resultsTable);
                    }
                }
            }
        }
        catch (MalformedURLException mue) {
            LOG.error(mue.getMessage(), mue);
        }
    }



}
