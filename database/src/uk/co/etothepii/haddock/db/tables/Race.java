/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.etothepii.haddock.db.tables;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DataAccessObject;
import uk.co.etothepii.db.Field;
import uk.co.etothepii.db.FieldChangedEvent;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class Race extends DataAccessObject {

    public static final Field SCHEDULED = new Field("scheduled", Date.class,
            Race.class);
    public static final Field MARKET = new Field("market", BFMarket.class,
            Race.class);
    public static final Field PLACE_MARKET = new Field("placeMarket",
            BFMarket.class, Race.class);
    public static final Field RACECOURSE = new Field("racecourse",
            Racecourse.class, Race.class);
    public static final Field NAME = new Field("name", String.class,
            Race.class);
    public static final Field DISTANCE = new Field("distance", Integer.class,
            Race.class);
    public static final Field PRIZEMONEY = new Field("prizemoney",
            Integer.class, Race.class);
    public static final Field WIN = new Field("win", Integer.class,
            Race.class);
    public static final Field GOING = new Field("going", String.class,
            Race.class);

    private static final Logger LOG = Logger.getLogger(Race.class);

    private Date scheduled;
    private BFMarket market;
    private BFMarket placeMarket;
    private Racecourse racecourse;
    private String name;
    private Integer distance;
    private Integer prizemoney;
    private Integer win;
    private String going;

    public Race(int id, BFMarket market, BFMarket placeMarket, Date scheduled,
            Racecourse racecourse, String name, Integer distance,
            Integer prizemoney, Integer win, String going) {
        super(id);
        this.scheduled = scheduled;
        this.market = market;
        this.placeMarket = placeMarket;
        this.racecourse = racecourse;
        this.name = name;
        this.distance = distance;
        this.prizemoney = prizemoney;
        this.win = win;
        this.going = going;
    }

    public Race(BFMarket market, BFMarket placeMarket, Date scheduled,
            Racecourse racecourse, String name, Integer distance,
            Integer prizemoney, Integer win, String going) {
        this(0, market, placeMarket, scheduled, racecourse, name, distance,
                prizemoney, win, going);
    }

    public BFMarket getMarket() {
        return market;
    }

    public void setMarket(BFMarket market) {
        if (different(market, this.market)) {
            BFMarket old = this.market;
            this.market = market;
            fireFieldChangedListener(new FieldChangedEvent(this, MARKET, old,
                    market));
            setChanged(true);
        }
    }

    public BFMarket getPlaceMarket() {
        return placeMarket;
    }

    public void setPlaceMarket(BFMarket placeMarket) {
        if (different(placeMarket, this.placeMarket)) {
            BFMarket old = this.placeMarket;
            this.placeMarket = placeMarket;
            fireFieldChangedListener(new FieldChangedEvent(this, PLACE_MARKET,
                    old, placeMarket));
            setChanged(true);
        }
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        if (different(distance, this.distance)) {
            int old = this.distance;
            this.distance = distance;
            fireFieldChangedListener(new FieldChangedEvent(this, DISTANCE, old,
                    distance));
            setChanged(true);
        }
    }

    public String getGoing() {
        return going;
    }

    public void setGoing(String going) {
        if (different(going, this.going)) {
            String old = this.going;
            this.going = going;
            fireFieldChangedListener(new FieldChangedEvent(this, GOING, old,
                    going));
            setChanged(true);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (different(name, this.name)) {
            String old = this.name;
            this.name = name;
            fireFieldChangedListener(new FieldChangedEvent(this, NAME, old,
                    name));
            setChanged(true);
        }
    }

    public Integer getPrizemoney() {
        return prizemoney;
    }

    public void setPrizemoney(Integer prizemoney) {
        if (different(prizemoney, this.prizemoney)) {
            int old = this.prizemoney;
            this.prizemoney = prizemoney;
            fireFieldChangedListener(new FieldChangedEvent(this, PRIZEMONEY,
                    old, prizemoney));
            setChanged(true);
        }
    }

    public Racecourse getRacecourse() {
        return racecourse;
    }

    public void setRacecourse(Racecourse racecourse) {
        if (different(racecourse, this.racecourse)) {
            Racecourse old = this.racecourse;
            this.racecourse = racecourse;
            fireFieldChangedListener(new FieldChangedEvent(this, RACECOURSE,
                    old, racecourse));
            setChanged(true);
        }
    }

    public Date getScheduled() {
        return scheduled;
    }

    public void setScheduled(Date scheduled) {
        if (different(scheduled, this.scheduled)) {
            Date old = this.scheduled;
            this.scheduled = scheduled;
            fireFieldChangedListener(new FieldChangedEvent(this, SCHEDULED, old,
                    scheduled));
            setChanged(true);
        }
    }

    public Integer getWin() {
        return win;
    }

    public void setWin(Integer win) {
        if (different(win, this.win)) {
            int old = this.win;
            this.win = win;
            fireFieldChangedListener(new FieldChangedEvent(this, WIN, old,
                    win));
            setChanged(true);
        }
    }

    private static SimpleDateFormat sdf =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1000);
        sb.append(getId());
        sb.append(":");
        sb.append(sdf.format(scheduled));
        sb.append(":");
        if (market == null)
            sb.append("null");
        else
            sb.append(market.getId());
        sb.append(":");
        if (placeMarket == null)
            sb.append("null");
        else
            sb.append(placeMarket.getId());
        sb.append(":");
        if (racecourse == null)
            sb.append("null");
        else
            sb.append(racecourse.getId());
        sb.append(":");
        sb.append(name);
        sb.append(":");
        if (distance == null)
            sb.append("null");
        else
            sb.append(distance);
        sb.append(":");
        if (prizemoney == null)
            sb.append("null");
        else
            sb.append(prizemoney);
        sb.append(":");
        if (win == null)
            sb.append("null");
        else
            sb.append(win);
        sb.append(":");
        sb.append(going);
        sb.append(":");
        return sb.toString();
    }



}
