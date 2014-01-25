/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables;

import org.apache.log4j.Logger;
import uk.co.etothepii.db.DataAccessObject;
import uk.co.etothepii.db.Field;
import uk.co.etothepii.db.FieldChangedEvent;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class TimeformResult extends DataAccessObject {

    public static final int DEAD_HEAT = -1;
    public static final int NECK = -10;
    public static final int HEAD = -20;
    public static final int SHORT_HEAD = -30;
    public static final int NOSE = -40;
    public static final int DISQUALIFIED = -50;
    public static final int FELL = -1;
    public static final int UNSEATED_RIDER = -2;
    public static final int PULLED_UP = -3;
    public static final int BROUGHT_DOWN = -4;
    public static final int REFUSED = -5;
    public static final int SLIPPED_UP = -6;
    public static final int REFUED_TO_RACE = -7;
    public static final int RAN_OUT = -8;
    public static final int CARRIED_OUT = -9;
    public static final int OUT = -10;
    public static final int SLIPPED_UP_SHORT = -11;
    public static final int BLANK = -12;
    public static final int CARRIED_OUT_SHORT = -13;

    private static final Logger LOG = Logger.getLogger(TimeformResult.class);

    public static final Field RACE = new Field("race", Race.class,
            TimeformResult.class);
    public static final Field POSITION = new Field("position", Integer.class,
            TimeformResult.class);
    public static final Field DRAW = new Field("draw", Integer.class,
            TimeformResult.class);
    public static final Field DISTANCE = new Field("distance",
            Integer.class, TimeformResult.class);
    public static final Field HORSE = new Field("horse", String.class,
            TimeformResult.class);
    public static final Field AGE = new Field("age", Integer.class,
            TimeformResult.class);
    public static final Field WEIGHT = new Field("weight", Integer.class,
            TimeformResult.class);
    public static final Field EQP = new Field("eqp", String.class,
            TimeformResult.class);
    public static final Field JOCKEY = new Field("jockey", String.class,
            TimeformResult.class);
    public static final Field TRAINER = new Field("trainer", String.class,
            TimeformResult.class);
    public static final Field IN_PLAY_HIGH = new Field("inPlayHigh",
            Double.class, TimeformResult.class);
    public static final Field IN_PLAY_LOW = new Field("inPlayLow", Double.class,
            TimeformResult.class);
    public static final Field BSP = new Field("bsp", Double.class,
            TimeformResult.class);
    public static final Field ISP = new Field("isp", Double.class,
            TimeformResult.class);
    public static final Field PLACE = new Field("place", Double.class,
            TimeformResult.class);

    private Race race;
    private Integer position;
    private Integer draw;
    private Integer distance;
    private Horse horse;
    private Integer weight;
    private String eqp;
    private String jockey;
    private String trainer;
    private Double inPlayHigh;
    private Double inPlayLow;
    private Double bsp;
    private Double isp;
    private Double place;

    public TimeformResult(int id, Race race, Integer position, Integer draw,
            Integer distance, Horse horse, Integer weight,
            String eqp, String jockey, String trainer, Double inPlayHigh,
            Double inPlayLow, Double bsp, Double isp, Double place) {
        super(id);
        this.race = race;
        this.position = position;
        this.draw = draw;
        this.distance = distance;
        this.horse = horse;
        this.weight = weight;
        this.eqp = eqp;
        this.jockey = jockey;
        this.trainer = trainer;
        this.inPlayHigh = inPlayHigh;
        this.inPlayLow = inPlayLow;
        this.bsp = bsp;
        this.isp = isp;
        this.place = place;
    }

    public TimeformResult(Race race, Integer position, Integer draw,
            Integer distance, Horse horse, Integer weight,
            String eqp, String jockey, String trainer, Double inPlayHigh,
            Double inPlayLow, Double bsp, Double isp, Double place) {
        this(0, race, position, draw, distance, horse,
                weight, eqp, jockey, trainer, inPlayHigh, inPlayLow, bsp, isp,
                place);
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        if (different(race, this.race)) {
            Race old = this.race;
            this.race = race;
            fireFieldChangedListener(new FieldChangedEvent(this, RACE,
                    old, race));
            setChanged(true);
        }
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        if (different(position, this.position)) {
            Integer old = this.position;
            this.position = position;
            fireFieldChangedListener(new FieldChangedEvent(this, POSITION,
                    old, position));
            setChanged(true);
        }
    }

    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        if (different(draw, this.draw)) {
            Integer old = this.draw;
            this.draw = draw;
            fireFieldChangedListener(new FieldChangedEvent(this, DRAW,
                    old, draw));
            setChanged(true);
        }
    }

    /**
     * Returns the distance in quarter lengths
     *
     * @return the distance in quarter lengths
     */
    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        if (different(distance, this.distance)) {
            Integer old = this.distance;
            this.distance = distance;
            fireFieldChangedListener(new FieldChangedEvent(this,
                    DISTANCE, old, distance));
            setChanged(true);
        }
    }

    public Horse getHorse() {
        return horse;
    }

    public void setHorse(Horse horse) {
        if (different(horse, this.horse)) {
            Horse old = this.horse;
            this.horse = horse;
            fireFieldChangedListener(new FieldChangedEvent(this, HORSE, old,
                    horse));
            setChanged(true);
        }
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        if (different(weight, this.weight)) {
            Integer old = this.weight;
            this.weight = weight;
            fireFieldChangedListener(new FieldChangedEvent(this, WEIGHT, old,
                    weight));
            setChanged(true);
        }
    }

    public String getEqp() {
        return eqp;
    }

    public void setEqp(String eqp) {
        if (different(eqp, this.eqp)) {
            String old = this.eqp;
            this.eqp = eqp;
            fireFieldChangedListener(new FieldChangedEvent(this, EQP, old,
                    eqp));
            setChanged(true);
        }
    }

    public String getJockey() {
        return jockey;
    }

    public void setJockey(String jockey) {
        if (different(jockey, this.jockey)) {
            String old = this.jockey;
            this.jockey = jockey;
            fireFieldChangedListener(new FieldChangedEvent(this, JOCKEY, old,
                    jockey));
            setChanged(true);
        }
    }

    public String getTrainer() {
        return trainer;
    }

    public void setTrainer(String trainer) {
        if (different(trainer, this.trainer)) {
            String old = this.trainer;
            this.trainer = trainer;
            fireFieldChangedListener(new FieldChangedEvent(this, TRAINER, old,
                    trainer));
            setChanged(true);
        }
    }

    public Double getInPlayHigh() {
        return inPlayHigh;
    }

    public void setInPlayHigh(Double inPlayHigh) {
        if (different(inPlayHigh, this.inPlayHigh)) {
            Double old = this.inPlayHigh;
            this.inPlayHigh = inPlayHigh;
            fireFieldChangedListener(new FieldChangedEvent(this, IN_PLAY_HIGH,
                    old, inPlayHigh));
            setChanged(true);
        }
    }

    public Double getInPlayLow() {
        return inPlayLow;
    }

    public void setInPlayLow(Double inPlayLow) {
        if (different(inPlayLow, this.inPlayLow)) {
            Double old = this.inPlayLow;
            this.inPlayLow = inPlayLow;
            fireFieldChangedListener(new FieldChangedEvent(this, IN_PLAY_LOW,
                    old, inPlayLow));
            setChanged(true);
        }
    }

    public Double getBsp() {
        return bsp;
    }

    public void setBsp(Double bsp) {
        if (different(bsp, this.bsp)) {
            Double old = this.bsp;
            this.bsp = bsp;
            fireFieldChangedListener(new FieldChangedEvent(this, BSP, old,
                    bsp));
            setChanged(true);
        }
    }

    public Double getIsp() {
        return isp;
    }

    public void setIsp(Double isp) {
        if (different(isp, this.isp)) {
            Double old = this.isp;
            this.isp = isp;
            fireFieldChangedListener(new FieldChangedEvent(this, ISP, old,
                    isp));
            setChanged(true);
        }
    }

    public Double getPlace() {
        return place;
    }

    public void setPlace(Double place) {
        if (different(place, this.place)) {
            Double old = this.place;
            this.place = place;
            fireFieldChangedListener(new FieldChangedEvent(this, PLACE, old,
                    place));
            setChanged(true);
        }
    }

}
