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
import uk.co.etothepii.haddock.db.tables.Alias;
import uk.co.etothepii.haddock.db.tables.SportingBetMatch;
import uk.co.etothepii.haddock.db.tables.enumerations.TeamOrder;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class SportingBetMatchFactory extends HaddockFactory<SportingBetMatch> {

    private static final Logger LOG = Logger.getLogger(SportingBetMatchFactory.class);

    @Override
    protected String getTablename() {
        return "sportingBetMatches";
    }

    private static SportingBetMatchFactory factory;

    public static SportingBetMatchFactory getFactory() {
        if (factory == null)
            factory = new SportingBetMatchFactory();
        return factory;
    }

    private SportingBetMatchFactory() {}

    @Override
    protected SportingBetMatch build(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            int teamAId = rs.getInt("teamA");
            int teamBId = rs.getInt("teamB");
            Date start = rs.getTimestamp("start");
            int aWin = rs.getInt("aWin");
            int draw = rs.getInt("draw");
            int bWin = rs.getInt("bWin");
            int marketId = rs.getInt("market");
            TeamOrder teamOrder = TeamOrder.valueOf(rs.getString("teamOrder"));
            SportingBetMatch toRet = new SportingBetMatch(id,
                    AliasFactory.getFactory().getFromId(teamAId),
                    AliasFactory.getFactory().getFromId(teamBId),
                    start, aWin, draw, bWin,
                    BFMarketFactory.getFactory().getFromId(marketId),
                    teamOrder);
            return toRet;
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
    }

    @Override
    protected String getInsertQueryStr() {
        return "INSERT INTO `" + getDatabasename() + "`.`" + getTablename()
                + "` (`id`, `teamA`, `teamB`, `start`, `aWin`, `draw`, `bWin`, "
                + "`market`, `teamOrder`) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, "
                + "?);";
    }

    @Override
    protected PreparedStatement prepareInsertStatement(SportingBetMatch t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        toRet.setInt(1, (int)t.getTeamA().getId());
        toRet.setInt(2, (int)t.getTeamB().getId());
        toRet.setTimestamp(3, new java.sql.Timestamp(
                t.getStartTime().getTime()));
        toRet.setDouble(4, t.getaWin());
        toRet.setDouble(5, t.getDraw());
        toRet.setDouble(6, t.getbWin());
        if (t.getMarket() == null)
            toRet.setNull(7, Types.INTEGER);
        else
            toRet.setInt(7, (int)t.getMarket().getId());
        toRet.setString(8, t.getTeamOrder().toString());
        LOG.debug(toRet);
        return toRet;
    }
    
    @Override
    protected String getUpdateQueryStr() {
        return "UPDATE `" + getDatabasename() + "`.`" + getTablename()
                + "` SET `teamA` = ?, `teamB` = ?, `start` = ?, `aWin` = ?, "
                + "`draw` = ?, `bWin` = ?, `market` = ?, `teamOrder` = ? "
                + "WHERE `id` = ? LIMIT 1";
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(SportingBetMatch t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledUpdateStatement(conduit);
        toRet.setInt(1, (int)t.getTeamA().getId());
        toRet.setInt(2, (int)t.getTeamB().getId());
        toRet.setTimestamp(3, new java.sql.Timestamp(
                t.getStartTime().getTime()));
        toRet.setDouble(4, t.getaWin());
        toRet.setDouble(5, t.getDraw());
        toRet.setDouble(6, t.getbWin());
        if (t.getMarket() == null)
            toRet.setNull(7, Types.INTEGER);
        else
            toRet.setInt(7, (int)t.getMarket().getId());
        toRet.setString(8, t.getTeamOrder().toString());
        toRet.setInt(9, (int)t.getId());
        LOG.debug(toRet);
        return toRet;
    }

    private HaddockStatement getMatchesAfterTimeQuery =
            new HaddockStatement("SELECT * FROM `" + getDatabasename() + "`.`"
            + getTablename() + "` WHERE `start` > ?", false);

    public ArrayList<SportingBetMatch> getMatchesAfterTime(Date date)
            throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return getMatchesAfterTime(date, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public ArrayList<SportingBetMatch> getMatchesAfterTime(Date date,
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement ps = getMatchesAfterTimeQuery.getStatement(conduit);
        ps.setTimestamp(1, new Timestamp(date.getTime()));
        ArrayList<SportingBetMatch> toRet = new ArrayList<SportingBetMatch>();
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            while (rs.next()) {
                toRet.add(build(rs));
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

    private HaddockStatement getFromTeamsAndTimeQuery =
            new HaddockStatement("SELECT * FROM `" + getDatabasename() + "`.`"
            + getTablename() + "` WHERE `teamA` = ? AND `teamB` = ? AND "
            + "`start` = ? LIMIT 1", false);

    public SportingBetMatch getFromTeamsAndTimeQuery(Alias a,
            Alias b, Date start) throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return getFromTeamsAndTimeQuery(a, b, start, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public SportingBetMatch getFromTeamsAndTimeQuery(Alias a,
            Alias b, Date start, DatabaseConduit conduit) throws SQLException {
        PreparedStatement ps = getFromTeamsAndTimeQuery.getStatement(conduit);
        ps.setInt(1, (int)a.getId());
        ps.setInt(2, (int)b.getId());
        ps.setTimestamp(3, new Timestamp(start.getTime()));
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            if (rs.next()) {
                return build(rs);
            }
            return null;
        }
        finally {
            if (rs != null){
                while (rs.next()) {}
                rs.close();
            }
			if (ps != null) {
				ps.close();
			}
        }
    }
}
