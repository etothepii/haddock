/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.bfhistorical;

import java.sql.SQLException;
import uk.co.etothepii.haddock.db.tables.enumerations.InPlayFlag;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import uk.co.etothepii.haddock.db.factories.BFMarketFactory;
import uk.co.etothepii.haddock.db.factories.MarketSelectionFactory;
import uk.co.etothepii.haddock.db.factories.RaceFactory;
import uk.co.etothepii.haddock.db.factories.RacecourseFactory;
import uk.co.etothepii.haddock.db.tables.BFHistoricalHorseracingDatum;
import uk.co.etothepii.haddock.db.tables.BFMarket;
import uk.co.etothepii.haddock.db.tables.MarketSelection;
import uk.co.etothepii.haddock.db.tables.Race;
import uk.co.etothepii.haddock.db.tables.Racecourse;
import uk.co.etothepii.haddock.db.tables.enumerations.MarketStatus;
import uk.co.etothepii.haddock.db.tables.enumerations.MarketType;
import uk.co.etothepii.haddock.db.tables.enumerations.YN;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class NonTimestampedRow {

    private static final Logger LOG = Logger.getLogger(NonTimestampedRow.class);
    private static final Logger LOG_PARSE_EXCEPTION =
            Logger.getLogger(NonTimestampedRow.class.getName().concat(".pe"));

    public final int sportsId;
    public final int eventId;
    public final Date settledDate;
    public final String country;
    public final String fullDescription;
    public final String racecourse;
    public final Date scheduledOff;
    public final String event;
    public final Date actualOff;
    public final int selectionId;
    public final String selection;
    public final double odds;
    public final int numberOfBets;
    public final double volumeMatched;
    public final Date lastTaken;
    public final Date firstTaken;
    public boolean winFlag;
    public InPlayFlag inPlay;

    public NonTimestampedRow(int sportsId, int eventId, Date settledDate,
            String country, String fullDescription, String racecourse,
            Date scheduledOff, String event, Date actualOff, int selectionId,
            String selection, double odds, int numberOfBets,
            double volumeMatched, Date lastTaken, Date firstTaken,
            boolean winFlag, InPlayFlag inPlay) {
        this.sportsId = sportsId;
        this.eventId = eventId;
        this.settledDate = settledDate;
        this.country = country;
        this.fullDescription = fullDescription;
        this.racecourse = racecourse;
        this.scheduledOff = scheduledOff;
        this.event = event;
        this.actualOff = actualOff;
        this.selectionId = selectionId;
        this.selection = selection;
        this.odds = odds;
        this.numberOfBets = numberOfBets;
        this.volumeMatched = volumeMatched;
        this.lastTaken = lastTaken;
        this.firstTaken = firstTaken;
        this.winFlag = winFlag;
        this.inPlay = inPlay;
    }

    private static final SimpleDateFormat settledDateFormat =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private static final SimpleDateFormat offFormat =
            new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private static final SimpleDateFormat takenFormat =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public static NonTimestampedRow getRow(String row) {
        String[] data = row.split("\",\"");
        if (data.length != 18) throw
                new IllegalArgumentException("Not enough columns");
        int sportsId = Integer.parseInt(data[0].substring(1));
        int eventId = Integer.parseInt(data[1]);
        Date settledDate = getDate(data[2], settledDateFormat);
        String country = data[3];
        String fullDescription = data[4];
        String racecourse = data[5];
        Date scheduledOff = getDate(data[6], offFormat);
        String event = data[7];
        Date actualOff = getDate(data[8], offFormat);
        int selectionId = Integer.parseInt(data[9]);
        String selection = data[10];
        double odds = Double.parseDouble(data[11]);
        int numberOfBets = Integer.parseInt(data[12]);
        double volumeMatched = Double.parseDouble(data[13]);
        Date lastTaken = getDate(data[14], takenFormat);
        Date firstTaken = getDate(data[15], takenFormat);
        int winFlag = Integer.parseInt(data[16]);
        InPlayFlag inPlay = InPlayFlag.valueOf(data[17].substring(0,
                data[17].length() - 1));
        return new NonTimestampedRow(sportsId, eventId, settledDate, country,
                fullDescription, racecourse, scheduledOff, event, actualOff,
                selectionId, selection, odds, numberOfBets, volumeMatched,
                lastTaken, firstTaken, winFlag == 1, inPlay);
    }

    private static Date getDate(String raw, SimpleDateFormat sdf) {
        try {
            return sdf.parse(raw);
        }
        catch (ParseException pe) {
            LOG_PARSE_EXCEPTION.debug(pe);
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1000);
        sb.append("\"");
        sb.append(sportsId);
        sb.append("\",\"");
        sb.append(eventId);
        sb.append("\",\"");
        sb.append(settledDate == null ? "" :
            settledDateFormat.format(settledDate));
        sb.append("\",\"");
        sb.append(country);
        sb.append("\",\"");
        sb.append(fullDescription);
        sb.append("\",\"");
        sb.append(racecourse);
        sb.append("\",\"");
        sb.append(scheduledOff == null ? "" :
            offFormat.format(scheduledOff));
        sb.append("\",\"");
        sb.append(event);
        sb.append("\",\"");
        sb.append(actualOff == null ? "" :
            offFormat.format(actualOff));
        sb.append("\",\"");
        sb.append(selectionId);
        sb.append("\",\"");
        sb.append(selection);
        sb.append("\",\"");
        sb.append(odds);
        sb.append("\",\"");
        sb.append(numberOfBets);
        sb.append("\",\"");
        sb.append(volumeMatched);
        sb.append("\",\"");
        sb.append(eventId);
        sb.append("\",\"");
        sb.append(lastTaken == null ? "" :
            takenFormat.format(lastTaken));
        sb.append("\",\"");
        sb.append(firstTaken == null ? "" :
            takenFormat.format(firstTaken));
        sb.append("\",\"");
        sb.append(winFlag ? "1" : "0");
        sb.append("\",\"");
        sb.append(inPlay);
        sb.append("\"");
        return sb.toString();
    }

    public BFHistoricalHorseracingDatum convertToHorseracingDatum()
            throws SQLException {
        Racecourse dbRacecourse =
                RacecourseFactory.getFactory().getFromAbv(this.racecourse);
        Race race = RaceFactory.getFactory().getFromRacecourseAndScheduled(
                dbRacecourse, scheduledOff);

        BFMarket thisMarket;
        if (event.equals("TO BE PLACED")) {
            if (race.getPlaceMarket() == null) {
                BFMarket place = getRepresentedBFMarket();
                race.setPlaceMarket(place);
            }
            thisMarket = race.getPlaceMarket();
        }
        else {
            if (race.getMarket() == null) {
                BFMarket market = getRepresentedBFMarket();
                race.setMarket(market);
            }
            thisMarket = race.getMarket();
        }
        if (thisMarket.getSettledDate() == null)
            thisMarket.setDate(settledDate);
        if (thisMarket.getActualDate() == null)
            thisMarket.setActualDate(actualOff);
        BFMarketFactory.getFactory().save(thisMarket);
        return null;
    }

    private BFMarket getRepresentedBFMarket() throws SQLException {
       BFMarket toRet = BFMarketFactory.getFactory(
               ).getFromBetfairId(eventId);
       if (toRet == null) {
           toRet = new BFMarket(eventId, event, null,
                   MarketStatus.CLOSED, scheduledOff, null, null, null,
                   country, null, null, null, null, null, null, null,
                   settledDate, actualOff);
           BFMarketFactory.getFactory().save(toRet);
       }
       return toRet;
    }

}
