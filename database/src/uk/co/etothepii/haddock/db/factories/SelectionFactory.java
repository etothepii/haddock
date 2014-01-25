/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.factories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.HaddockStatement;
import uk.co.etothepii.haddock.db.tables.BFMarket;
import uk.co.etothepii.haddock.db.tables.Selection;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class SelectionFactory extends HaddockFactory<Selection> {

    private static final Logger LOG = Logger.getLogger(SelectionFactory.class);

    @Override
    protected String getTablename() {
        return "selections";
    }

    private static SelectionFactory factory;

    public static SelectionFactory getFactory() {
        if (factory == null)
            factory = new SelectionFactory();
        return factory;
    }

    public SelectionFactory() {}

    @Override
    protected Selection build(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            String displayName = rs.getString("displayName");
            return new Selection(id, displayName);
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
    }

    @Override
    protected String getInsertQueryStr() {
        return "INSERT INTO `" + getDatabasename() + "`.`" + getTablename()
                + "` (`id`, `displayName`) VALUES (NULL, ?);";
    }

    @Override
    protected PreparedStatement prepareInsertStatement(Selection t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        toRet.setString(1, t.getDisplayName());
        return toRet;
    }

    @Override
    protected String getUpdateQueryStr() {
        return "UPDATE `" + getDatabasename() + "`.`" + getTablename()
                + "` SET `displayName` = ? WHERE `id` = ? LIMIT 1;";
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(Selection t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        toRet.setString(1, t.getDisplayName());
        toRet.setInt(2, (int)t.getId());
        return toRet;
    }

    private HaddockStatement loadAllSelectionForRaces = new HaddockStatement(
            "SELECT s.* FROM (SELECT * FROM races as r "
            + "WHERE r.scheduled > ? AND r.scheduled < ?) as r "
            + "inner join bfMarkets as b on r.bfMarket = b.id "
            + "inner join marketSelections as ms on b.id = ms.market "
            + "inner join selections as s on ms.selection = s.id", false);

    public ArrayList<Selection> loadAllSelectionsForRaces(Date start,
            Date stop) throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return loadAllSelectionsForRaces(start, stop, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public ArrayList<Selection> loadAllSelectionsForRaces(Date start,
            Date stop, DatabaseConduit conduit) throws SQLException {
        PreparedStatement ps = loadAllSelectionForRaces.getStatement(conduit);
        ps.setTimestamp(1, new Timestamp(start.getTime()));
        ps.setTimestamp(2, new Timestamp(stop.getTime()));
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            ArrayList<Selection> selections = new ArrayList<Selection>();
            while (rs.next()) {
                selections.add(build(rs));
            }
            return selections;
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

    private HaddockStatement getSelectionsAttachedToMarketQuery =
            new HaddockStatement("SELECT `s`.* FROM `marketSelections` as `ms`,"
            + " `selections` as `s` WHERE `ms`.`selection` = `s`.`id` AND "
            + "`ms`.`market` = ?", false);

    public ArrayList<Selection> getSelectionsAttachedToMarket(BFMarket market)
            throws SQLException{
        DatabaseConduit conduit = takeConduit();
        try {
            return getSelectionsAttachedToMarket(market, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public ArrayList<Selection> getSelectionsAttachedToMarket(BFMarket market,
            DatabaseConduit conduit) throws SQLException{
        if (market == null)
            return null;
        PreparedStatement ps =
                getSelectionsAttachedToMarketQuery.getStatement(conduit, true);
        ps.setInt(1, (int)market.getId());
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            ArrayList<Selection> selections = new ArrayList<Selection>();
            while (rs.next()) {
                selections.add(build(rs));
            }
            return selections;
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
