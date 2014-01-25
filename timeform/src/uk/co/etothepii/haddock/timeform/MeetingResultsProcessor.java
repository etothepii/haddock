/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.timeform;

import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import org.htmlcleaner.TagNode;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.factories.RaceFactory;
import uk.co.etothepii.haddock.db.factories.RacecourseFactory;
import uk.co.etothepii.haddock.db.tables.Race;
import uk.co.etothepii.haddock.db.tables.Racecourse;
import uk.co.etothepii.util.screenscraping.TextHtmlProcessor;
import uk.co.etothepii.util.screenscraping.WebPage;

/**
 *
 * @author jrrpl
 */
public class MeetingResultsProcessor extends TextHtmlProcessor {

    private static final Logger LOG =
            Logger.getLogger(MeetingResultsProcessor.class);

    Map<String, String> attributes;
    Map<String, String> goingAttributes;
    Map<String, String> nameAttributes;

    private SimpleDateFormat scheduledOffFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public MeetingResultsProcessor() {
        attributes = new TreeMap<String, String>();
        attributes.put("class", "results");
        goingAttributes = new TreeMap<String, String>();
        goingAttributes.put("class", "going");
        nameAttributes = new TreeMap<String, String>();
        nameAttributes.put("class", "race-name");
    }

    public ArrayList<ResultsTable> process(WebPage webPage, Date date) {
        if (webPage.contentType.startsWith("text/html") &&
                webPage.content instanceof InputStream) {
            TagNode root = getRootNode(webPage);
            return process(root, date);
        }
        return null;
    }

    public ArrayList<ResultsTable> process(TagNode root, Date date) {
        ArrayList<ResultsTable> toRet = new ArrayList<ResultsTable>();
        ArrayList<TagNode> resultsTableRootNodes =
                getTagsEquals(root, "table", attributes);
        ArrayList<TagNode> h3Tags = getTagsEquals(root, "h3",
                new TreeMap<String, String>());
        ArrayList<String> races = new ArrayList<String>();
        for (TagNode h3Tag : h3Tags) {
            ArrayList<String> contents = getContentStrings(h3Tag);
            if (contents.size() == 2) {
                if (contents.get(1).equals("RESULT")) {
                    if (contents.get(0).matches(
                            "^[0-2][0-9]:[0-5][0-9].*")) {
                        races.add(contents.get(0));
                    }
                }
            }
        }
        if (races.size() != resultsTableRootNodes.size()) {
            LOG.error("The number of races found does not match with the"
                    + "number of results tables found!");
            return null;
        }
        for (int i = 0; i < races.size(); i++) {
            Calendar raceDay = Calendar.getInstance();
            raceDay.setTimeInMillis(date.getTime());
            String[] parts = races.get(i).split(" ", 2);
            String[] time = parts[0].split(":");
            int hours = Integer.parseInt(time[0]);
            int mins = Integer.parseInt(time[1]);
            Calendar scheduledOffCal = Calendar.getInstance();
            scheduledOffCal.clear();
            scheduledOffCal.set(Calendar.MINUTE, mins);
            scheduledOffCal.set(Calendar.HOUR, hours);
            scheduledOffCal.set(Calendar.DAY_OF_MONTH,
                    raceDay.get(Calendar.DAY_OF_MONTH));
            scheduledOffCal.set(Calendar.MONTH,
                    raceDay.get(Calendar.MONTH));
            scheduledOffCal.set(Calendar.YEAR, raceDay.get(Calendar.YEAR));
            Date scheduledOff = new Date(scheduledOffCal.getTimeInMillis());
            try {
                Racecourse racecourse =
                        RacecourseFactory.getFactory(
                        ).getFromName(parts[1]);
                if (racecourse == null) {
                    LOG.error("No such racecourse: '" + parts[1] + "'");
                }
                else {
                    Race race = RaceFactory.getFactory(
                            ).getFromRacecourseAndScheduled(
                            racecourse, scheduledOff);
                    if (race == null) {
                        LOG.debug("No such race at " + parts[1] + " at "
                                + scheduledOffFormat.format(scheduledOff));
                        race = new Race(null, null, scheduledOff,
                                racecourse, null, null, null, null, null);
                        RaceFactory.getFactory().save(race);
                    }
                    else {
                        LOG.debug("Found race at " + parts[1] + " at "
                                + scheduledOffFormat.format(scheduledOff));
                    }
                    TagNode tableStart = resultsTableRootNodes.get(i);
                    TagNode tableParent = tableStart.getParent();
                    ArrayList<TagNode> nameTags =
                            getTagsEquals(tableParent, "div", nameAttributes);
                    ArrayList<TagNode> goingTags =
                            getTagsEquals(tableParent, "div", goingAttributes);
                    if (nameTags.size() == 1) {
                        race.setName(getContents(nameTags.get(0)));
                    }
                    if (goingTags.size() == 1) {
                        race.setGoing(getContents(goingTags.get(0)));
                    }
                    RaceFactory.getFactory().save(race);
                    toRet.add(new ResultsTable(race, tableStart));
                }
            }
            catch (SQLException sqle) {
                DatabaseConduit.printSQLException(sqle);
            }
        }
        return toRet;
    }

}
