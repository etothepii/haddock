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
import java.util.ArrayList;
import java.util.Date;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.HaddockStatement;
import uk.co.etothepii.haddock.db.tables.BFMarket;
import uk.co.etothepii.haddock.db.tables.Race;
import uk.co.etothepii.haddock.db.tables.Racecourse;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class RaceFactory extends HaddockFactory<Race>{

    private static final Logger LOG = Logger.getLogger(RaceFactory.class);

    static {
        LOG.debug("Debugging " + RaceFactory.class.getCanonicalName());
    }

    @Override
    protected String getTablename() {
        return "races";
    }

    private static RaceFactory factory;

    public static RaceFactory getFactory() {
        if (factory == null)
            factory = new RaceFactory();
        return factory;
    }

    private RaceFactory() {}

    @Override
    protected Race build(ResultSet rs) {
        try {
            LOG.debug("building Race");
            int id = rs.getInt("id");
            int marketId = rs.getInt("bfMarket");
            int marketPlaceId = rs.getInt("bfPlaceMarket");
            Date scheduled = rs.getTimestamp("scheduled");
            int racecourseId = rs.getInt("racecourse");
            String name = rs.getString("name");
            Integer distance = rs.getInt("distance");
            Integer prizemoney = rs.getInt("prizemoney");
            Integer win = rs.getInt("win");
            String going = rs.getString("going");
            LOG.debug("Creating market ...");
            BFMarket market = BFMarketFactory.getFactory().getFromId(
                    marketId);
            LOG.debug("Creating placeMarket");
            BFMarket placeMarket = BFMarketFactory.getFactory().getFromId(
                    marketPlaceId);
            LOG.debug("Creating racecourse");
            Racecourse racecourse = RacecourseFactory.getFactory(
                    ).getFromId(racecourseId);
            LOG.debug("about to create Race");
            Race race = new Race(id, market, placeMarket, scheduled, racecourse,
                    name, distance, prizemoney, win,
                    going);
            LOG.debug("caching Race");
            return cache(race);
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
    }

    private HaddockStatement getAllBetweenTwoDatesStatement =
            new HaddockStatement("SELECT * FROM `races` WHERE `scheduled` > ? "
            + "AND `scheduled` < ? ORDER BY `scheduled`;", false);

    public ArrayList<Race> getAllBetweenTwoDates(Date start, Date stop)
            throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return getAllBetweenTwoDates(start, stop, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public ArrayList<Race> getAllBetweenTwoDates(Date start, Date stop,
            DatabaseConduit conduit) throws SQLException {
        BFMarketFactory.getFactory().loadAllRaceMarkets(start, stop);
        PreparedStatement ps = 
                getAllBetweenTwoDatesStatement.getStatement(conduit);
        ps.setTimestamp(1, new Timestamp(start.getTime()));
        ps.setTimestamp(2, new Timestamp(stop.getTime()));
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            ArrayList<Race> races = new ArrayList<Race>();
            while (rs.next())
                races.add(build(rs));
            return races;
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

    public void reload(Race t) {
        Race replace = getFromDatabaseWithId(t.getId());
        LOG.debug(replace);
        t.setDistance(replace.getDistance());
        t.setGoing(replace.getGoing());
        t.setMarket(replace.getMarket());
        t.setName(replace.getName());
        t.setPlaceMarket(replace.getPlaceMarket());
        t.setPrizemoney(replace.getPrizemoney());
        t.setRacecourse(replace.getRacecourse());
        t.setScheduled(replace.getScheduled());
        t.setWin(replace.getWin());
        t.saved();
    }

    @Override
    protected String getInsertQueryStr() {
        return "INSERT INTO `" + getDatabasename() + "`.`" + getTablename()
                + "` (`id`, `bfMarket`, `bfPlaceMarket`, `scheduled`, "
                + "`racecourse`, `name`, `distance`, `prizemoney`, `win`, "
                + "`going`) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    }

    @Override
    protected PreparedStatement prepareInsertStatement(Race t,
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        if (t.getMarket() == null) {
            toRet.setNull(1, Types.INTEGER);
        }
        else {
            toRet.setInt(1, (int)t.getMarket().getId());
        }
        if (t.getPlaceMarket() == null) {
            toRet.setNull(2, Types.INTEGER);
        }
        else {
            toRet.setInt(2, (int)t.getPlaceMarket().getId());
        }
        toRet.setTimestamp(3, new Timestamp(t.getScheduled().getTime()));
        toRet.setInt(4, (int)t.getRacecourse().getId());
        if (t.getName() != null)
            toRet.setString(5, t.getName());
        else
            toRet.setNull(5, Types.INTEGER);
        if (t.getDistance() != null)
            toRet.setInt(6, t.getDistance());
        else
            toRet.setNull(6, Types.INTEGER);
        if (t.getPrizemoney() != null)
            toRet.setInt(7, t.getPrizemoney());
        else
            toRet.setNull(7, Types.INTEGER);
        if (t.getWin() != null)
            toRet.setInt(8, t.getWin());
        else
            toRet.setNull(8, Types.INTEGER);
        if (t.getGoing() != null)
            toRet.setString(9, t.getGoing());
        else
            toRet.setNull(9, Types.VARCHAR);
        return toRet;
    }

    @Override
    protected String getUpdateQueryStr() {
        return "UPDATE `" + getDatabasename() + "`.`" + getTablename()
                + "` SET `bfMarket` = ?, `bfPlaceMarket` = ?, `scheduled` = ?, "
                + "`racecourse` = ?, `name` = ?, `distance` = ?, "
                + "`prizemoney` = ?, `win` = ?, `going` = ? WHERE `id` = ? "
                + "LIMIT 1;";
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(Race t,
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledUpdateStatement(conduit);
        if (t.getMarket() == null) {
            toRet.setNull(1, Types.INTEGER);
        }
        else {
            toRet.setInt(1, (int)t.getMarket().getId());
        }
        if (t.getPlaceMarket() == null) {
            toRet.setNull(2, Types.INTEGER);
        }
        else {
            toRet.setInt(2, (int)t.getPlaceMarket().getId());
        }
        toRet.setTimestamp(3, new Timestamp(t.getScheduled().getTime()));
        toRet.setInt(4, (int)t.getRacecourse().getId());
        if (t.getName() != null)
            toRet.setString(5, t.getName());
        else
            toRet.setNull(5, Types.INTEGER);
        if (t.getDistance() != null)
            toRet.setInt(6, t.getDistance());
        else
            toRet.setNull(6, Types.INTEGER);
        if (t.getPrizemoney() != null)
            toRet.setInt(7, t.getPrizemoney());
        else
            toRet.setNull(7, Types.INTEGER);
        if (t.getWin() != null)
            toRet.setInt(8, t.getWin());
        else
            toRet.setNull(8, Types.INTEGER);
        if (t.getGoing() != null)
            toRet.setString(9, t.getGoing());
        else
            toRet.setNull(9, Types.VARCHAR);
        toRet.setInt(10, (int)t.getId());
        return toRet;
    }

    private final HaddockStatement getFromRacecourseAndSheduledQuery =
            new HaddockStatement("SELECT * FROM `" + getDatabasename() + "`.`"
                + getTablename() + "` WHERE `racecourse` = ? AND `scheduled` = "
                + "? LIMIT 1", true);

    public Race getFromRacecourseAndScheduled(Racecourse racecourse,
            Date scheduled) throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return getFromRacecourseAndScheduled(
                    racecourse, scheduled, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public Race getFromRacecourseAndScheduled(Racecourse racecourse,
            Date scheduled, DatabaseConduit conduit) throws SQLException {
        LOG.debug("getFromRacecourseAndScheduled");
        ResultSet rs = null;
		PreparedStatement ps = null;
        try {
            synchronized (getFromRacecourseAndSheduledQuery) {
                ps = getFromRacecourseAndSheduledQuery.getStatement(conduit);
                ps.setInt(1, (int)racecourse.getId());
                ps.setTimestamp(2, new Timestamp(scheduled.getTime()));
                LOG.debug(ps);
                rs = ps.executeQuery();
            }
            if (rs.next())
                return (build(rs));
            else
                return null;
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

}
