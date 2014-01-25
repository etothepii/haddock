/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.Field;
import uk.co.etothepii.haddock.db.tables.enumerations.MarketStatus;
import uk.co.etothepii.haddock.db.tables.enumerations.MarketType;
import uk.co.etothepii.haddock.db.tables.enumerations.YN;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class BFMarket extends BFDataAccessObject {

    public static final Field NAME = new Field("name", String.class, 
            BFMarket.class);
    public static final Field TYPE = new Field("type", MarketType.class, 
            BFMarket.class);
    public static final Field STATUS = new Field("status", MarketStatus.class, 
            BFMarket.class);
    public static final Field DATE = new Field("date", Date.class, 
            BFMarket.class);
    public static final Field PARENT = new Field("parent", BFEvent.class, 
            BFMarket.class);
    public static final Field DELAY = new Field("delay", Integer.class, 
            BFMarket.class);
    public static final Field EXCHANGE = new Field("exchange", Integer.class, 
            BFMarket.class);
    public static final Field COUNTRY = new Field("country", String.class, 
            BFMarket.class);
    public static final Field REFRESH = new Field("refresh", Date.class, 
            BFMarket.class);
    public static final Field LAST_SEEN = new Field("lastSeen", Date.class, 
            BFMarket.class);
    public static final Field RUNNERS = new Field("runners", Integer.class, 
            BFMarket.class);
    public static final Field WINNERS = new Field("winners", Integer.class, 
            BFMarket.class);
    public static final Field MATCHED = new Field("matched", Long.class, 
            BFMarket.class);
    public static final Field BSP_MARKET = new Field("bspMarket", YN.class, 
            BFMarket.class);
    public static final Field TURNING_IN_PLAY = new Field("turningInPlay",
            YN.class, BFMarket.class);
    public static final Field SETTLED_DATE = new Field("settledDate",
            Date.class, BFMarket.class);

    public static final Comparator<BFMarket> ALPHABETICAL =
            new Comparator<BFMarket>() {
        public int compare(BFMarket o1, BFMarket o2) {
            return o1.getDisplayName().compareTo(o2.getDisplayName());
        }
    };

    private static final Logger LOG = Logger.getLogger(BFMarket.class);

    private String name;
    private MarketType type;
    private MarketStatus status;
    private Date date;
    private BFEvent parent;
    private Integer delay;
    private Integer exchange;
    private String country;
    private Date refresh;
    private Date lastSeen;
    private Integer runners;
    private Integer winners;
    private Long matched;
    private YN bspMarket;
    private YN turningInPlay;
    private Date settledDate;
    private Date actualDate;

    public BFMarket(int id, int betfairId, String name, MarketType type,
            MarketStatus status, Date date, BFEvent parent, Integer delay,
            Integer exchange, String country, Date refresh, Date lastSeen,
            Integer runners, Integer winners, Long matched, YN bspMarket,
            YN turningInPlay, Date settledDate, Date actualDate) {
        super(id, betfairId);
        this.name = name;
        this.type = type;
        this.status = status;
        this.date = date;
        this.parent = parent;
        this.delay = delay;
        this.exchange = exchange;
        this.country = country;
        this.refresh = refresh;
        this.runners = runners;
        this.winners = winners;
        this.matched = matched;
        this.bspMarket = bspMarket;
        this.turningInPlay = turningInPlay;
        this.settledDate = settledDate;
        this.actualDate = actualDate;
        setLastSeen(lastSeen);
    }

    public BFMarket(int betfairId, String name, MarketType type,
            MarketStatus status, Date date, BFEvent parent, Integer delay,
            Integer exchange, String country, Date refresh, Date lastSeen,
            Integer runners, Integer winners, Long matched, YN bspMarket,
            YN turningInPlay, Date settledDate, Date actualDate) {
        this(0, betfairId, name, type, status, date, parent, delay, exchange,
                country, null, lastSeen, runners, winners, matched, bspMarket,
                turningInPlay, settledDate, actualDate);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (different(name, this.name)) {
            this.name = name;
            setChanged(true);
        }
    }

    public MarketType getType() {
        return type;
    }

    public void setType(MarketType type) {
        if (different(type, this.type)) {
            this.type = type;
            setChanged(true);
        }
    }

    public MarketStatus getStatus() {
        return status;
    }

    public void setStatus(MarketStatus status) {
        if (different(status, this.status)) {
            this.status = status;
            setChanged(true);
        }
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        if (different(date, this.date)) {
            this.date = date;
            setChanged(true);
        }
    }

    public BFEvent getParent() {
        return parent;
    }

    public void setParent(BFEvent parent) {
        if (different(parent, this.parent)) {
            this.parent = parent;
            setChanged(true);
        }
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        if (different(delay, this.delay)) {
            this.delay = delay;
            setChanged(true);
        }
    }

    public Integer getExchange() {
        return exchange;
    }

    public void setExchange(Integer exchange) {
        if (different(exchange, this.exchange)) {
            this.exchange = exchange;
            setChanged(true);
        }
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        if (different(country, this.country)) {
            this.country = country;
            setChanged(true);
        }
    }

    public Date getRefresh() {
        return refresh;
    }

    public Integer getRunners() {
        return runners;
    }

    public void setRunners(Integer runners) {
        if (different(runners, this.runners)) {
            this.runners = runners;
            setChanged(true);
        }
    }

    public Integer getWinners() {
        return winners;
    }

    public void setWinners(Integer winners) {
        if (different(winners, this.winners)) {
            this.winners = winners;
            setChanged(true);
        }
    }

    public Long getMatched() {
        return matched;
    }

    public void setMatched(Long matched) {
        if (matched > this.matched) {
            this.matched = matched;
            setChanged(true);
        }
    }

    public YN getBspMarket() {
        return bspMarket;
    }

    public void setBspMarket(YN bspMarket) {
        if (different(bspMarket, this.bspMarket)) {
            this.bspMarket = bspMarket;
            setChanged(true);
        }
    }

    public YN getTurningInPlay() {
        return turningInPlay;
    }

    public void setTurningInPlay(YN turningInPlay) {
        if (different(turningInPlay, this.turningInPlay)) {
            this.turningInPlay = turningInPlay;
            setChanged(true);
        }
    }

    public static final SimpleDateFormat timeFormat =
            new SimpleDateFormat("HH:mm");

    public String getDisplayName() {
        if (!isRaceName(name)) return name;
        StringBuilder sb = new StringBuilder(name.length() + 5);
        sb.append(timeFormat.format(date));
        sb.append(" ");
        sb.append(name);
        return sb.toString();
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public final void setLastSeen(Date lastSeen) {
        if (different(lastSeen, this.lastSeen)) {
            this.lastSeen = lastSeen;
            setChanged(true);
        }
        if (parent != null) {
            parent.setLastSeen(lastSeen);
        }
    }

    public Date getSettledDate() {
        return settledDate;
    }

    public final void setSettledDate(Date settledDate) {
        if (different(settledDate, this.settledDate)) {
            this.settledDate = settledDate;
            setChanged(true);
        }
    }

    public Date getActualDate() {
        return actualDate;
    }

    public final void setActualDate(Date actualDate) {
        if (different(actualDate, this.actualDate)) {
            this.actualDate = actualDate;
            setChanged(true);
        }
    }

    private static final NumberFormat nf = NumberFormat.getCurrencyInstance();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1000);
        sb.append(getId());
        sb.append(",");
        sb.append(getBetfairId());
        sb.append(",");
        sb.append(getName());
        sb.append(",");
        sb.append(getType());
        sb.append(",");
        sb.append(getStatus());
        sb.append(",");
        sb.append(getDate());
        sb.append(",");
        sb.append(parent == null ? "" : parent.getId());
        sb.append(",");
        sb.append(getDelay());
        sb.append(",");
        sb.append(getExchange());
        sb.append(",");
        sb.append(getCountry());
        sb.append(",");
        sb.append(getRefresh());
        sb.append(",");
        sb.append(getRunners());
        sb.append(",");
        sb.append(getWinners());
        sb.append(",");
        sb.append(nf.format(getMatched() / 100d));
        sb.append(",");
        sb.append(getBspMarket());
        sb.append(",");
        sb.append(getTurningInPlay());
        sb.append(",");
        sb.append(getSettledDate());
        sb.append(",");
        sb.append(getActualDate());
        return sb.toString();
    }

    @Override
    public void saved() {
        super.saved();
        refresh = new Date(System.currentTimeMillis());
    }

    public static boolean isRaceName(String name) {
        if (name.equals("To Be Placed")) return true;
        if (name.matches("[1-4]m .*")) return true;
        if (name.matches("[1-4]m[1-7]f .*")) return true;
        if (name.matches("[1-7]f .*")) return true;
        if (name.matches(".* [1-4]m .*")) return true;
        if (name.matches(".* [1-4]m[1-7]f .*")) return true;
        if (name.matches(".* [1-7]f .*")) return true;
        else return false;
    }
}
