/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.factories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.HaddockStatement;
import uk.co.etothepii.haddock.db.tables.Horse;
import uk.co.etothepii.haddock.db.tables.Race;
import uk.co.etothepii.haddock.db.tables.TimeformResult;

/**
 *
 * @author jrrpl
 */
public class TimeformResultFactory extends 
        HaddockFactory<TimeformResult> {

    @Override
    protected String getTablename() {
        return "timeformResults";
    }

    private static TimeformResultFactory factory;

    public static TimeformResultFactory getFactory() {
        if (factory == null)
            factory = new TimeformResultFactory();
        return factory;
    }

    private TimeformResultFactory() {}

    @Override
    protected TimeformResult build(ResultSet rs) {
        try {
            int id = rs.getInt(TimeformResult.ID.name);
            Integer raceId = rs.getInt(TimeformResult.RACE.name);
            Integer position = rs.getInt(TimeformResult.POSITION.name);
            Integer draw = rs.getInt(TimeformResult.DRAW.name);
            Integer distance = rs.getInt(TimeformResult.DISTANCE.name);
            Integer horseId = rs.getInt(TimeformResult.HORSE.name);
            Integer weight = rs.getInt(TimeformResult.WEIGHT.name);
            String eqp = rs.getString(TimeformResult.EQP.name);
            String jockey = rs.getString(TimeformResult.JOCKEY.name);
            String trainer = rs.getString(TimeformResult.TRAINER.name);
            Double inPlayHigh = rs.getDouble(TimeformResult.IN_PLAY_HIGH.name);
            Double inPlayLow = rs.getDouble(TimeformResult.IN_PLAY_LOW.name);
            Double bsp = rs.getDouble(TimeformResult.BSP.name);
            Double isp = rs.getDouble(TimeformResult.ISP.name);
            Double place = rs.getDouble(TimeformResult.PLACE.name);
            Horse horse = HorseFactory.getFactory().getFromId(horseId);
            TimeformResult toRet = new TimeformResult(id, 
                    RaceFactory.getFactory().getFromId(raceId), position, draw,
                    distance, horse, weight, eqp, jockey, trainer,
                    inPlayHigh, inPlayLow, bsp, isp, place);
            return toRet;
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
    }

    @Override
    protected String getInsertQueryStr() {
        return "INSERT INTO `" + getDatabasename() + "`.`" + getTablename()
                + "` (`" + TimeformResult.ID.name
                + "`, `" + TimeformResult.RACE.name
                + "`, `" + TimeformResult.POSITION.name
                + "`, `" + TimeformResult.DRAW.name
                + "`, `" + TimeformResult.DISTANCE.name
                + "`, `" + TimeformResult.HORSE.name
                + "`, `" + TimeformResult.WEIGHT.name
                + "`, `" + TimeformResult.EQP.name
                + "`, `" + TimeformResult.JOCKEY.name
                + "`, `" + TimeformResult.TRAINER.name
                + "`, `" + TimeformResult.IN_PLAY_HIGH.name
                + "`, `" + TimeformResult.IN_PLAY_LOW.name
                + "`, `" + TimeformResult.BSP.name
                + "`, `" + TimeformResult.ISP.name
                + "`, `" + TimeformResult.PLACE.name
                + "`) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                + "?);";
    }

    @Override
    protected PreparedStatement prepareInsertStatement(TimeformResult t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        if (t.getRace() != null)
            toRet.setInt(1, (int)t.getRace().getId());
        else
            toRet.setNull(1, Types.INTEGER);

        if (t.getPosition() != null)
            toRet.setInt(2, t.getPosition());
        else
            toRet.setNull(2, Types.INTEGER);

        if (t.getDraw() != null)
            toRet.setInt(3, t.getDraw());
        else
            toRet.setNull(3, Types.INTEGER);

        if (t.getDistance() != null)
            toRet.setInt(4, t.getDistance());
        else
            toRet.setNull(4, Types.INTEGER);

        if (t.getHorse() != null)
            toRet.setLong(5, t.getHorse().getId());
        else
            toRet.setNull(5, Types.INTEGER);
        if (t.getWeight() != null)
            toRet.setInt(6, t.getWeight());
        else
            toRet.setNull(6, Types.INTEGER);

        if (t.getEqp() != null)
            toRet.setString(7, t.getEqp());
        else
            toRet.setNull(7, Types.VARCHAR);

        if (t.getJockey() != null)
            toRet.setString(8, t.getJockey());
        else
            toRet.setNull(8, Types.VARCHAR);

        if (t.getTrainer() != null)
            toRet.setString(9, t.getTrainer());
        else
            toRet.setNull(9, Types.VARCHAR);

        if (t.getInPlayHigh() != null)
            toRet.setDouble(10, t.getInPlayHigh());
        else
            toRet.setNull(10, Types.DOUBLE);

        if (t.getInPlayLow() != null)
            toRet.setDouble(11, t.getInPlayLow());
        else
            toRet.setNull(11, Types.DOUBLE);

        if (t.getBsp() != null)
            toRet.setDouble(12, t.getBsp());
        else
            toRet.setNull(12, Types.DOUBLE);

        if (t.getIsp() != null)
            toRet.setDouble(13, t.getIsp());
        else
            toRet.setNull(13, Types.DOUBLE);

        if (t.getPlace() != null)
            toRet.setDouble(14, t.getPlace());
        else
            toRet.setNull(14, Types.DOUBLE);
        return toRet;
    }

    @Override
    protected String getUpdateQueryStr() {
        return "UPDATE `" + getDatabasename() + "`.`" + getTablename()
                + "` SET `" + TimeformResult.RACE.name
                + "` = ?, `" + TimeformResult.POSITION.name
                + "` = ?, `" + TimeformResult.DRAW.name
                + "` = ?, `" + TimeformResult.DISTANCE.name
                + "` = ?, `" + TimeformResult.HORSE.name
                + "` = ?, `" + TimeformResult.WEIGHT.name
                + "` = ?, `" + TimeformResult.EQP.name
                + "` = ?, `" + TimeformResult.JOCKEY.name
                + "` = ?, `" + TimeformResult.TRAINER.name
                + "` = ?, `" + TimeformResult.IN_PLAY_HIGH.name
                + "` = ?, `" + TimeformResult.IN_PLAY_LOW.name
                + "` = ?, `" + TimeformResult.BSP.name
                + "` = ?, `" + TimeformResult.ISP.name
                + "` = ?, `" + TimeformResult.PLACE.name
                + "` = ? WHERE `id` = ? LIMIT 1";
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(TimeformResult t,
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledUpdateStatement(conduit);
        if (t.getRace() != null)
            toRet.setInt(1, (int)t.getRace().getId());
        else
            toRet.setNull(1, Types.INTEGER);

        if (t.getPosition() != null)
            toRet.setInt(2, t.getPosition());
        else
            toRet.setNull(2, Types.INTEGER);

        if (t.getDraw() != null)
            toRet.setInt(3, t.getDraw());
        else
            toRet.setNull(3, Types.INTEGER);

        if (t.getDistance() != null)
            toRet.setInt(4, t.getDistance());
        else
            toRet.setNull(4, Types.INTEGER);

        if (t.getHorse() != null)
            toRet.setLong(5, t.getHorse().getId());
        else
            toRet.setNull(5, Types.INTEGER);

        if (t.getWeight() != null)
            toRet.setInt(6, t.getWeight());
        else
            toRet.setNull(6, Types.INTEGER);

        if (t.getEqp() != null)
            toRet.setString(7, t.getEqp());
        else
            toRet.setNull(7, Types.VARCHAR);

        if (t.getJockey() != null)
            toRet.setString(8, t.getJockey());
        else
            toRet.setNull(8, Types.VARCHAR);

        if (t.getTrainer() != null)
            toRet.setString(9, t.getTrainer());
        else
            toRet.setNull(9, Types.VARCHAR);

        if (t.getInPlayHigh() != null)
            toRet.setDouble(10, t.getInPlayHigh());
        else
            toRet.setNull(10, Types.DOUBLE);

        if (t.getInPlayLow() != null)
            toRet.setDouble(11, t.getInPlayLow());
        else
            toRet.setNull(11, Types.DOUBLE);

        if (t.getBsp() != null)
            toRet.setDouble(12, t.getBsp());
        else
            toRet.setNull(12, Types.DOUBLE);

        if (t.getIsp() != null)
            toRet.setDouble(13, t.getIsp());
        else
            toRet.setNull(13, Types.DOUBLE);

        if (t.getPlace() != null)
            toRet.setDouble(14, t.getPlace());
        else
            toRet.setNull(14, Types.DOUBLE);
        toRet.setInt(15, (int)t.getId());
        return toRet;
    }

    HaddockStatement resultCountQuery = new HaddockStatement(
            "SELECT COUNT(*) FROM `" + getDatabasename() + "`.`"
            + getTablename() + "` WHERE `" + TimeformResult.RACE.name + "` = ?",
            false);

    public int getResultCount(Race race) {
        DatabaseConduit conduit = takeConduit();
        try {
            return getResultCount(race, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public int getResultCount(Race race, DatabaseConduit conduit) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = resultCountQuery.getStatement(conduit);
            ps.setInt(1, (int)race.getId());
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
        finally {
            try {
            	if (rs != null) {
                    while (rs.next()) {}
                    rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			}
			catch (SQLException sqle) {
				throw new RuntimeException(sqle);
			}
        }
        return -1;
    }
}
