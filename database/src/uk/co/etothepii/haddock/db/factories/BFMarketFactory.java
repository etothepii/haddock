/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.factories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.HaddockStatement;
import uk.co.etothepii.haddock.db.tables.BFEvent;
import uk.co.etothepii.haddock.db.tables.BFMarket;
import uk.co.etothepii.haddock.db.tables.enumerations.MarketStatus;
import uk.co.etothepii.haddock.db.tables.enumerations.MarketType;
import uk.co.etothepii.haddock.db.tables.enumerations.YN;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class BFMarketFactory extends HaddockBFFactory<BFMarket> {

    private static final SimpleDateFormat sdf =
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private static final Logger LOG = Logger.getLogger(BFMarketFactory.class);

    private static final Logger LOG_QUERY =
            Logger.getLogger(BFMarketFactory.class.getName().concat("query"));

    @Override
    protected String getTablename() {
        return "bfMarkets";
    }

    private static BFMarketFactory factory;

    public static BFMarketFactory getFactory() {
        LOG.debug("Getting Factory");
        if (factory == null)
            factory = new BFMarketFactory();
        LOG.debug("Returning Factory");
        return factory;
    }

    private BFMarketFactory() {
        super();
    }

    @Override
    public BFMarket build(ResultSet rs) {
        try {
            LOG.debug(rs.toString());
            int id = rs.getInt("id");
            int betfairId = rs.getInt("betfairId");
            String name = rs.getString("name");
            MarketType type = MarketType.valueOf(rs.getString("type"));
            MarketStatus status = MarketStatus.valueOf(rs.getString("status"));
            Date date = rs.getTimestamp("date");
            int parentId = rs.getInt("parent");
            LOG.debug("parentId: " + parentId);
            BFEvent parent = BFEventFactory.getFactory().getFromId(parentId);
            int delay = rs.getInt("delay");
            int exchange = rs.getInt("exchange");
            String country = rs.getString("country");
            Date refresh = rs.getTimestamp("refresh");
            Date lastSeen = rs.getTimestamp("refresh");
            int runners = rs.getInt("runners");
            int winners = rs.getInt("winners");
            long matched = rs.getLong("matched");
            YN bspMarket = YN.valueOf(rs.getString("bspMarket"));
            YN turningInPlay = YN.valueOf(rs.getString("turningInPlay"));
            Date settledDate = rs.getTimestamp("settledDate");
            Date actualDate = rs.getTimestamp("actualDate");
            return new BFMarket(id, betfairId, name, type, status, date, parent,
                    delay, exchange, country, refresh, lastSeen, runners,
                    winners, matched, bspMarket, turningInPlay, settledDate,
                    actualDate);
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
    }

    @Override
    protected String getInsertQueryStr() {
        return "INSERT INTO `" + getDatabasename() + "`.`" + getTablename()
                + "` (`id`, `betfairId`, `name`, `type`, `status`, `date`, "
                + "`parent`, `delay`, `exchange`, `country`, "
                + "`runners`, `winners`, `matched`, `bspMarket`, "
                + "`turningInPlay`, `lastSeen`, `settledDate`, `actualDate`) "
                + "VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                + "?, ?);";
    }

    @Override
    protected PreparedStatement prepareInsertStatement(BFMarket t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        toRet.setInt(1, t.getBetfairId());
        toRet.setString(2, t.getName());
        if (t.getType() != null)
            toRet.setString(3, t.getType().name());
        else
            toRet.setNull(3, Types.VARCHAR);
        toRet.setString(4, t.getStatus().name());
        toRet.setTimestamp(5, new Timestamp(t.getDate().getTime()));
        if (t.getParent() != null)
            toRet.setInt(6, (int)t.getParent().getId());
        else
            toRet.setNull(6, Types.INTEGER);
        if (t.getDelay() != null)
            toRet.setInt(7, t.getDelay());
        else
            toRet.setNull(7, Types.INTEGER);
        if (t.getExchange() != null)
            toRet.setInt(8, t.getExchange());
        else
            toRet.setNull(8, Types.INTEGER);
        toRet.setString(9, t.getCountry());
        if (t.getRunners() != null)
            toRet.setInt(10, t.getRunners());
        else
            toRet.setNull(10, Types.INTEGER);
        if (t.getWinners() != null)
            toRet.setInt(11, t.getWinners());
        else
            toRet.setNull(11, Types.INTEGER);
        if (t.getMatched() != null)
            toRet.setLong(12, t.getMatched());
        else
            toRet.setNull(12, Types.INTEGER);
        if (t.getBspMarket() != null)
            toRet.setString(13, t.getBspMarket().name());
        else
            toRet.setNull(13, Types.VARCHAR);
        if (t.getTurningInPlay() != null)
            toRet.setString(14, t.getTurningInPlay().name());
        else
            toRet.setNull(14, Types.VARCHAR);
        if (t.getLastSeen() != null)
            toRet.setTimestamp(15, new Timestamp(t.getLastSeen().getTime()));
        else
            toRet.setNull(15, Types.TIMESTAMP);
        if (t.getSettledDate() == null)
            toRet.setNull(16, Types.TIMESTAMP);
        else
            toRet.setTimestamp(16, new Timestamp(t.getSettledDate().getTime()));
        if (t.getActualDate() == null)
            toRet.setNull(17, Types.TIMESTAMP);
        else
            toRet.setTimestamp(17, new Timestamp(t.getActualDate().getTime()));
        return toRet;
    }

    @Override
    protected String getUpdateQueryStr() {
        return "UPDATE `" + getDatabasename() + "`.`" + getTablename()
                + "` SET `betfairId` = ?, `name` = ?, `type` = ?, `status` = ?,"
                + " `date` = ?, `parent` = ?, `delay` = ?, `exchange` = ?, "
                + "`country` = ?, `runners` = ?, `winners` = ?, "
                + "`matched` = ?, `bspMarket` = ?, `turningInPlay` = ?, "
                + "`lastSeen` = ?, `settledDate` = ?, `actualDate` = ? "
                + "WHERE `id` = ? LIMIT 1";
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(BFMarket t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet =getUnfilledUpdateStatement(conduit);
        toRet.setInt(1, t.getBetfairId());
        toRet.setString(2, t.getName());
        if (t.getType() != null)
            toRet.setString(3, t.getType().name());
        else
            toRet.setNull(3, Types.VARCHAR);
        toRet.setString(4, t.getStatus().name());
        toRet.setTimestamp(5, new Timestamp(t.getDate().getTime()));
        if (t.getParent() != null)
            toRet.setInt(6, (int)t.getParent().getId());
        else
            toRet.setNull(6, Types.INTEGER);
        if (t.getDelay() != null)
            toRet.setInt(7, t.getDelay());
        else
            toRet.setNull(7, Types.INTEGER);
        if (t.getExchange() != null)
            toRet.setInt(8, t.getExchange());
        else
            toRet.setNull(8, Types.INTEGER);
        toRet.setString(9, t.getCountry());
        if (t.getRunners() != null)
            toRet.setInt(10, t.getRunners());
        else
            toRet.setNull(10, Types.INTEGER);
        if (t.getWinners() != null)
            toRet.setInt(11, t.getWinners());
        else
            toRet.setNull(11, Types.INTEGER);
        if (t.getMatched() != null)
            toRet.setLong(12, t.getMatched());
        else
            toRet.setNull(12, Types.INTEGER);
        if (t.getBspMarket() != null)
            toRet.setString(13, t.getBspMarket().name());
        else
            toRet.setNull(13, Types.VARCHAR);
        if (t.getTurningInPlay() != null)
            toRet.setString(14, t.getTurningInPlay().name());
        else
            toRet.setNull(14, Types.VARCHAR);
        if (t.getLastSeen() != null)
            toRet.setTimestamp(15, new Timestamp(t.getLastSeen().getTime()));
        else
            toRet.setNull(15, Types.TIMESTAMP);
        if (t.getSettledDate() == null)
            toRet.setNull(16, Types.TIMESTAMP);
        else
            toRet.setTimestamp(16, new Timestamp(t.getSettledDate().getTime()));
        if (t.getActualDate() == null)
            toRet.setNull(17, Types.TIMESTAMP);
        else
            toRet.setTimestamp(17, new Timestamp(t.getActualDate().getTime()));
        toRet.setInt(18, (int)t.getId());
        return toRet;
    }

    private HaddockStatement smallUpdateQuery = new HaddockStatement("UPDATE `"
            + getDatabasename() + "`.`" + getTablename() + "` SET "
            + "`matched` = ?, `lastSeen` = ? WHERE `id` = ? LIMIT 1", false);

    public void smallUpdate(BFMarket t) throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            smallUpdate(t, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public void smallUpdate(BFMarket t, DatabaseConduit conduit)
            throws SQLException {
        PreparedStatement ps = smallUpdateQuery.getStatement(conduit);
        ps.setLong(1, t.getMatched());
        ps.setTimestamp(2, new Timestamp(t.getLastSeen().getTime()));
        ps.setInt(3, (int)t.getId());
        LOG.debug(ps);
        ps.execute();
        saved(t);
        if (t.getParent() != null) {
            BFEventFactory.getFactory().save(t.getParent());
        }
    }

    private HaddockStatement loadAllRaceMarketsQuery = new HaddockStatement(
            "SELECT b.* FROM (SELECT bfMarket as m, bfPlaceMarket as p "
            + "FROM `races` WHERE `scheduled` > ? AND `scheduled` < ?) as t, "
            + "bfMarkets as b WHERE b.id = m OR b.id = p", false);

    public ArrayList<BFMarket> loadAllRaceMarkets(Date start, Date stop)
            throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return loadAllRaceMarkets(start, stop, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public ArrayList<BFMarket> loadAllRaceMarkets(Date start, Date stop,
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement ps = loadAllRaceMarketsQuery.getStatement(conduit);
        ps.setTimestamp(1, new Timestamp(start.getTime()));
        ps.setTimestamp(2, new Timestamp(stop.getTime()));
        ArrayList<BFMarket> markets = new ArrayList<BFMarket>();
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            while (rs.next())
                markets.add(build(rs));
        }
        finally {
            if (rs != null) {
                while (rs.next()) {}
                rs.close();
            }
			if (ps != null) {
				ps.close();
			}
        }
        return markets;
    }

    private HaddockStatement getMarketsByStartTimeQuery =
            new HaddockStatement("SELECT * FROM `" + getDatabasename() + "`.`"
            + getTablename() + "` WHERE `date` = ? AND `name` = ?", false);

    public ArrayList<BFMarket> getMarketsByStartTime(Date date, String name)
            throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return getMarketsByStartTime(date, name, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public ArrayList<BFMarket> getMarketsByStartTime(Date date, String name,
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement ps = 
                getMarketsByStartTimeQuery.getStatement(conduit);
        ps.setTimestamp(1, new Timestamp(date.getTime()));
        ps.setString(2, name);
        LOG.debug(ps);
        ArrayList<BFMarket> markets = new ArrayList<BFMarket>();
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            while (rs.next()) {
                markets.add(build(rs));
            }
        }
        finally {
            if (rs != null) {
                while (rs.next()) {}
                rs.close();
            }
			if (ps != null) {
				ps.close();
			}
        }
        return markets;
    }

    private final HaddockStatement getMarketFromParentNameAndDateQuery =
            new HaddockStatement("SELECT `m`.* FROM `" + getDatabasename()
            + "`.`" + getTablename() + "` as `m`, `"
            + "bfEvents` as `e` WHERE `m`.`parent` = `e`.`id` AND "
            + "`m`.`date` = ? AND `e`.`name` = ?", true);

    public BFMarket getMarketFromParentNameAndDate(String parentName,
            Date date) throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return getMarketFromParentNameAndDate(parentName, date, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public BFMarket getMarketFromParentNameAndDate(String parentName,
            Date date, DatabaseConduit conduit) throws SQLException {
        PreparedStatement ps =
                getMarketFromParentNameAndDateQuery.getStatement(conduit);
        ps.setTimestamp(1, new Timestamp(date.getTime()));
        ps.setString(2, parentName);
        ResultSet rs = null;
        BFMarket toRet = null;
        try {
            rs = ps.executeQuery();
            if (rs.next()) {
                toRet = cache(build(rs));
            }
            if (rs.next()) {
                toRet = null;
            }
        }
        finally {
            if (rs != null) {
                while (rs.next()) {}
                rs.close();
            }
			if (ps != null) {
				ps.close();
			}
        }
        return toRet;
    }

    public ArrayList<BFMarket> getMarketsFromParentNameAndDate(String parentName,
            Date date) throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return getMarketsFromParentNameAndDate(parentName, date, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public ArrayList<BFMarket> getMarketsFromParentNameAndDate(String parentName,
            Date date, DatabaseConduit conduit) throws SQLException {
        PreparedStatement ps =
                getMarketFromParentNameAndDateQuery.getStatement(conduit);
        ps.setTimestamp(1, new Timestamp(date.getTime()));
        ps.setString(2, parentName);
        LOG_QUERY.debug(ps);
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            ArrayList<BFMarket> toRet = new ArrayList<BFMarket>();
            if (LOG.isDebugEnabled()){
                LOG.debug(rs);
            }
            while (rs.next()) {
                BFMarket market = build(rs);
                if (market != null)
                    toRet.add(cache(market));
                else
                    throw new NullPointerException();
            }
            if(LOG.isDebugEnabled()){
                LOG.debug(toRet.size() + " markets found from " + ps.toString());
            }
            return toRet;
        }
        finally {
            if (rs != null) {
                while (rs.next()) {}
                rs.close();
            }
			if (ps != null) {
				ps.close();
			}
        }
    }

    private HaddockStatement getBFEventUnclosedChildrenQuery =
            new HaddockStatement("SELECT * FROM `" + getDatabasename() + "`.`"
                + getTablename() + "` WHERE `parent` = ? "
                + "AND `status` != 'CLOSED'", false);

    private HaddockStatement getBFEventUnclosedNullChildrenQuery =
            new HaddockStatement("SELECT * FROM `" + getDatabasename() + "`.`"
                + getTablename() + "` WHERE `parent` IS NULL "
                + "AND `status` != 'CLOSED'", false);

    private HaddockStatement getBFEventChildrenQuery =
            new HaddockStatement("SELECT * FROM `" + getDatabasename() + "`.`"
                + getTablename() + "` WHERE `parent` = ?", false);

    private HaddockStatement getBFEventNullChildrenQuery =
            new HaddockStatement("SELECT * FROM `" + getDatabasename() + "`.`"
                + getTablename() + "` WHERE `parent` IS NULL", false);

    public ArrayList<BFMarket> getBFEventUnclosedChildren(BFEvent bfEvent)
            throws SQLException {
        return getBFEventChildren(bfEvent, true);
    }

    public ArrayList<BFMarket> getBFEventChildren(BFEvent bfEvent,
            boolean unclosedOnly) throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return getBFEventChildren(bfEvent, unclosedOnly, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public ArrayList<BFMarket> getBFEventChildren(BFEvent bfEvent,
            boolean unclosedOnly, DatabaseConduit conduit) throws SQLException {
        PreparedStatement ps;
        if (bfEvent == null) {
            if (unclosedOnly)
                ps = getBFEventUnclosedNullChildrenQuery.getStatement(conduit);
            else
                ps = getBFEventNullChildrenQuery.getStatement(conduit);
        }
        else {
            if (unclosedOnly)
                ps = getBFEventUnclosedChildrenQuery.getStatement(conduit);
            else
                ps = getBFEventChildrenQuery.getStatement(conduit);
            ps.setInt(1, (int)bfEvent.getId());
        }
        LOG_QUERY.debug(ps);
        ArrayList<BFMarket> toRet = new ArrayList<BFMarket>();
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            while (rs.next())
                toRet.add(build(rs));
        }
        finally {
            if (rs != null) {
                while (rs.next()) {}
                rs.close();
            }
			if (ps != null) {
				ps.close();
			}
        }
        return toRet;
    }

    private HaddockStatement closeUnseenQuery = new HaddockStatement(
            "UPDATE `" + getDatabasename() + "`.`" + getTablename() + "` "
            + " SET `status` = 'CLOSED' WHERE `lastSeen` IS NULL OR "
            + "`lastSeen`  < ?", false);
    /**
     * Removes anything which hasn't been seen since a specified time.
     * More precisely removes anything that hasn't been seen since the
     * second before since MySQL Timestamp does not have resolution to the
     * millisecond.
     *
     * @param startedAt the time at which one last started looking for new
     *                  markets
     * @throws SQLException
     */
    public void closeUnseen(Date startedAt) throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            closeUnseen(startedAt, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public void closeUnseen(Date startedAt, DatabaseConduit conduit)
            throws SQLException {
        PreparedStatement ps = closeUnseenQuery.getStatement(conduit);
        ps.setTimestamp(1, new Timestamp(startedAt.getTime() - 1500L));
        ps.execute();
    }

    @Override
    public void save(BFMarket t, boolean cache) throws SQLException {
        super.save(t, cache);
        if (t.getParent() != null) {
            BFEventFactory.getFactory().save(t.getParent(), cache);
        }
    }

}
