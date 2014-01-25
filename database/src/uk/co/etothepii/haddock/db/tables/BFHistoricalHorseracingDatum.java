/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables;

import java.util.Date;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.Field;
import uk.co.etothepii.db.FieldChangedEvent;
import uk.co.etothepii.haddock.db.tables.enumerations.InPlayFlag;

/**
 *
 * @author jrrpl
 */
public class BFHistoricalHorseracingDatum extends HaddockAccessObject {

    public static final Field RACE = new Field("race",
            Race.class, BFHistoricalHorseracingDatum.class);
    public static final Field MARKET_SELECTION = new Field("marketSelection",
            MarketSelection.class, BFHistoricalHorseracingDatum.class);
    public static final Field ODDS = new Field("odds", Double.class,
            BFHistoricalHorseracingDatum.class);
    public static final Field NUMBER_OF_BETS = new Field("numberOfBets",
            Integer.class, BFHistoricalHorseracingDatum.class);
    public static final Field VOLUME_MATCHED = new Field("volumeMatched",
            Double.class, BFHistoricalHorseracingDatum.class);
    public static final Field LAST_TAKEN = new Field("lastTaken", Date.class,
            BFHistoricalHorseracingDatum.class);
    public static final Field FIRST_TAKEN = new Field("firstTaken", Date.class,
            BFHistoricalHorseracingDatum.class);
    public static final Field IN_PLAY = new Field("inPlay", InPlayFlag.class,
            BFHistoricalHorseracingDatum.class);

    private static final Logger LOG =
            Logger.getLogger(BFHistoricalHorseracingDatum.class);

    private Race race;
    private MarketSelection marketSelection;
    private double odds;
    private int numberOfBets;
    private double volumeMatched;
    private Date lastTaken;
    private Date firstTaken;
    private InPlayFlag inPlay;

    public BFHistoricalHorseracingDatum(int id, Race race,
            MarketSelection marketSelection, double odds, int numberOfBets,
            double volumeMatched, Date lastTaken, Date firstTaken,
            InPlayFlag inPlay) {
        super(id);
        this.race = race;
        this.marketSelection = marketSelection;
        this.odds = odds;
        this.numberOfBets = numberOfBets;
        this.volumeMatched = volumeMatched;
        this.lastTaken = lastTaken;
        this.firstTaken = firstTaken;
        this.inPlay = inPlay;
    }

    public BFHistoricalHorseracingDatum(Race race,
            MarketSelection marketSelection, double odds, int numberOfBets,
            double volumeMatched, Date lastTaken, Date firstTaken,
            InPlayFlag inPlay) {
        this(0, race, marketSelection, odds, numberOfBets, volumeMatched,
                lastTaken, firstTaken, inPlay);
    }
    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        if (different(race, this.race)) {
            Race old = this.race;
            this.race = race;
            fireFieldChangedListener(new FieldChangedEvent(this, RACE, old, race));
            setChanged(true);
        }
    }

    public MarketSelection getMarketSelection() {
        return marketSelection;
    }

    public void setMarketSelection(MarketSelection marketSelection) {
        if (different(marketSelection, this.marketSelection)) {
            MarketSelection old = this.marketSelection;
            this.marketSelection = marketSelection;
            fireFieldChangedListener(new FieldChangedEvent(this, MARKET_SELECTION, old, marketSelection));
            setChanged(true);
        }
    }

    public double getOdds() {
        return odds;
    }

    public void setOdds(double odds) {
        if (different(odds, this.odds)) {
            double old = this.odds;
            this.odds = odds;
            fireFieldChangedListener(new FieldChangedEvent(this, ODDS, old, odds));
            setChanged(true);
        }
    }

    public int getNumberOfBets() {
        return numberOfBets;
    }

    public void setNumberOfBets(int numberOfBets) {
        if (different(numberOfBets, this.numberOfBets)) {
            int old = this.numberOfBets;
            this.numberOfBets = numberOfBets;
            fireFieldChangedListener(new FieldChangedEvent(this, NUMBER_OF_BETS, old, numberOfBets));
            setChanged(true);
        }
    }

    public double getVolumeMatched() {
        return volumeMatched;
    }

    public void setVolumeMatched(double volumeMatched) {
        if (different(volumeMatched, this.volumeMatched)) {
            double old = this.volumeMatched;
            this.volumeMatched = volumeMatched;
            fireFieldChangedListener(new FieldChangedEvent(this, VOLUME_MATCHED, old, volumeMatched));
            setChanged(true);
        }
    }

    public Date getLastTaken() {
        return lastTaken;
    }

    public void setLastTaken(Date lastTaken) {
        if (different(lastTaken, this.lastTaken)) {
            Date old = this.lastTaken;
            this.lastTaken = lastTaken;
            fireFieldChangedListener(new FieldChangedEvent(this, LAST_TAKEN, old, lastTaken));
            setChanged(true);
        }
    }

    public Date getFirstTaken() {
        return firstTaken;
    }

    public void setFirstTaken(Date firstTaken) {
        if (different(firstTaken, this.firstTaken)) {
            Date old = this.firstTaken;
            this.firstTaken = firstTaken;
            fireFieldChangedListener(new FieldChangedEvent(this, FIRST_TAKEN, old, firstTaken));
            setChanged(true);
        }
    }

    public InPlayFlag getInPlay() {
        return inPlay;
    }

    public void setInPlay(InPlayFlag inPlay) {
        if (different(inPlay, this.inPlay)) {
            InPlayFlag old = this.inPlay;
            this.inPlay = inPlay;
            fireFieldChangedListener(new FieldChangedEvent(this, IN_PLAY, old, inPlay));
            setChanged(true);
        }
    }
}
