/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.timeform;

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
import uk.co.etothepii.haddock.db.factories.HorseFactory;
import uk.co.etothepii.haddock.db.factories.RaceFactory;
import uk.co.etothepii.haddock.db.factories.RacecourseFactory;
import uk.co.etothepii.haddock.db.factories.TimeformResultFactory;
import uk.co.etothepii.haddock.db.tables.Horse;
import uk.co.etothepii.haddock.db.tables.Race;
import uk.co.etothepii.haddock.db.tables.Racecourse;
import uk.co.etothepii.haddock.db.tables.TimeformResult;
import uk.co.etothepii.util.screenscraping.TextHtmlProcessor;

/**
 *
 * @author jrrpl
 */
public class RaceResultProcessor extends TextHtmlProcessor {
    
    private static final Logger LOG = 
            Logger.getLogger(RaceResultProcessor.class);

    public static final String DEAD_HEAT = "dh";
    public static final String NOSE = "ns";
    public static final String SHORT_HEAD = "sh";
    public static final String HEAD = "hd";
    public static final String NECK = "nk";
    public static final String DISQUALIFIED = "ds";
    public static final String FELL = "F";
    public static final String UNSEATED_RIDER = "U";
    public static final String PULLED_UP = "P";
    public static final String BROUGHT_DOWN = "B";
    public static final String REFUSED = "R";
    public static final String SLIPPED_UP = "SU";
    public static final String SLIPPED_UP_SHORT = "S";
    public static final String REFUED_TO_RACE = "RTR";
    public static final String OUT = "O";
    public static final String RAN_OUT = "RO";
    public static final String CARRIED_OUT = "CO";
    public static final String CARRIED_OUT_SHORT = "C";
    public static final String BLANK = "";

    private Map<String, String> attributes;

    private SimpleDateFormat scheduledOffFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public RaceResultProcessor() {
        attributes = new TreeMap<String, String>();
        attributes.put("class", "runner");
    }

    private final SimpleDateFormat sdf =
            new SimpleDateFormat("HH:mm dd MMMM yyyy");

    public ArrayList<TimeformResult> process(ResultsTable resultsTable) {
        ArrayList<TagNode> runnerResults = 
                getTagsEquals(resultsTable.root, "tr", attributes);
        ArrayList<TimeformResult> toRet = new ArrayList<TimeformResult>();
        for (TagNode tn2 : runnerResults) {
            TagNode[] nodes = tn2.getChildTags();
            String positionStr   = getContents(nodes[0]);
            String drawStr       = getContents(nodes[1]);
            String distStr       = getContents(nodes[2]);
            String horseName         = getContents(nodes[3]);
            String ageStr        = getContents(nodes[4]);
            String weightStr     = getContents(nodes[5]);
            String eqp           = getContents(nodes[6]);
            TagNode[] jtNodes    = nodes[7].getChildTags();
            String jockey        = getContents(jtNodes[0]);
            String trainer       = getContents(jtNodes[1]);
            String inPlayHighStr = getContents(nodes[8]);
            String inPlayLowStr  = getContents(nodes[9]);
            String bspStr        = getContents(nodes[10]);
            String ispStr        = getContents(nodes[11]);
            String placeStr      = getContents(nodes[13]);
            Integer position   = getPosition(positionStr);
            Integer draw       = safeParseInt(drawStr);
            Integer dist       = getDist(distStr);
            Integer age        = safeParseInt(ageStr);
            Integer weight     = getWeight(weightStr);
            Double inPlayHigh  = safeParseDouble(inPlayHighStr);
            Double inPlayLow   = safeParseDouble(inPlayLowStr);
            Double bsp         = safeParseDouble(bspStr);
            Double isp         = safeParseDouble(ispStr);
            Double place       = safeParseDouble(placeStr);
            Horse horse = HorseFactory.getFactory().getFromNameAndAgeOnDate(
                    horseName, age, resultsTable.race.getScheduled());
            if (position == null) {
                LOG.error("Not processed due to null position: "
                        + resultsTable.race.getRacecourse()
                        + " " + sdf.format(resultsTable.race.getScheduled()));
                return new ArrayList<TimeformResult>();
            }
            toRet.add(new TimeformResult(resultsTable.race, position, draw,
                    dist, horse, weight, eqp, jockey, trainer, inPlayHigh,
                    inPlayLow, bsp, isp, place));
        }
        int count = TimeformResultFactory.getFactory(
                ).getResultCount(resultsTable.race);
        if (count == 0) {
            try {
                for (TimeformResult tr : toRet)
                    TimeformResultFactory.getFactory().save(tr);
            }
            catch (SQLException sqle) {
                DatabaseConduit.printSQLException(sqle);
            }
        }
        return toRet;
    }

    public static Integer getPosition(String positionStr) {
        Integer toRet = parsePosition(positionStr);
        if (toRet == null) {
            LOG.debug("'" + positionStr + "' ----> " + (toRet == null ?
            "null" : toRet.toString()));
            toRet = parsePosition(positionStr.toUpperCase());
            if (toRet != null) toRet -= 100;
            LOG.debug("'" + positionStr.toUpperCase() + "' ----> " +
                    (toRet == null ? "null" : toRet.toString()));
        }
        else {
            LOG.debug("'" + positionStr + "' ----> " + (toRet == null ?
                "null" : toRet.toString()));
        }
        return toRet;
    }

    public static Integer parsePosition(String positionStr) {
        if (positionStr.equals(FELL))
            return TimeformResult.FELL;
        else if (positionStr.equals(UNSEATED_RIDER))
            return TimeformResult.UNSEATED_RIDER;
        else if (positionStr.equals(PULLED_UP))
            return TimeformResult.PULLED_UP;
        else if (positionStr.equals(BROUGHT_DOWN))
            return TimeformResult.BROUGHT_DOWN;
        else if (positionStr.equals(REFUSED))
            return TimeformResult.REFUSED;
        else if (positionStr.equals(SLIPPED_UP))
            return TimeformResult.SLIPPED_UP;
        else if (positionStr.equals(REFUED_TO_RACE))
            return TimeformResult.REFUED_TO_RACE;
        else if (positionStr.equals(RAN_OUT))
            return TimeformResult.RAN_OUT;
        else if(positionStr.equals(CARRIED_OUT))
            return TimeformResult.CARRIED_OUT;
        else if(positionStr.equals(OUT))
            return TimeformResult.OUT;
        else if(positionStr.equals(SLIPPED_UP_SHORT))
            return TimeformResult.SLIPPED_UP_SHORT;
        else if(positionStr.equals(BLANK))
            return TimeformResult.BLANK;
        else if(positionStr.equals(CARRIED_OUT_SHORT))
            return TimeformResult.CARRIED_OUT_SHORT;
        else return safeParseInt(positionStr);
    }

    public static Integer getDist(String distStr) {
        Integer toRet = parseDist(distStr);
//        LOG.debug(distStr + " ----> " + (toRet == null ?
//            "null" : toRet.toString()));
        return toRet;
    }

    private static Integer parseDist(String distStr) {
        if (distStr.length() == 0) return null;
        else if(distStr.equals(DEAD_HEAT)) return TimeformResult.DEAD_HEAT;
        else if(distStr.equals(NOSE)) return TimeformResult.NOSE;
        else if (distStr.equals(SHORT_HEAD)) return TimeformResult.SHORT_HEAD;
        else if (distStr.equals(HEAD)) return TimeformResult.HEAD;
        else if (distStr.equals(NECK)) return TimeformResult.NECK;
        else if (distStr.equals(DISQUALIFIED))
            return TimeformResult.DISQUALIFIED;
        StringBuilder integerPart = new StringBuilder(distStr.length());
        StringBuilder otherPart = new StringBuilder(distStr.length());
        for (int i = 0; i < distStr.length(); i++) {
            char c = distStr.charAt(i);
            if (Character.isDigit(c))
                integerPart.append(c);
            else
                otherPart.append(c);
        }
        int toRet = integerPart.length() == 0 ? 0 :
            Integer.parseInt(integerPart.toString()) * 4;
        if (otherPart.length() > 0) {
            String otherStr = otherPart.toString();
            if (otherStr.contains("\u00bc"))
                return toRet + 1;
            if (otherStr.contains("\u00bd"))
                return toRet + 2;
            if (otherStr.contains("\u00be"))
                return toRet + 3;
            for (char c : otherPart.toString().toCharArray()) {
                LOG.error((char)c + " ---> " + (int)c);
            }
        }
        return toRet;
    }

    public static Integer getWeight(String weightStr) {
        String[] parts = weightStr.split("-");
        try {
            int stone = Integer.parseInt(parts[0]);
            int lbs = Integer.parseInt(parts[1]);
            return stone * 14 + lbs;
        }
        catch (NumberFormatException nfe) {
            return null;
        }
    }

    private Race getRace(Date date, String raceTimeAndCourse) {
        Calendar raceDay = Calendar.getInstance();
        raceDay.setTimeInMillis(date.getTime());
        String[] parts = raceTimeAndCourse.split(" ", 2);
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
                LOG.error("No such racecourse: " + parts[1]);
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
                return race;
            }
        }
        catch (SQLException sqle) {
            DatabaseConduit.printSQLException(sqle);
        }
        return null;
    }

}
