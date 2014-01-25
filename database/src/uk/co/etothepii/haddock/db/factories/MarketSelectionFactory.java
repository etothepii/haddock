/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.factories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.HaddockStatement;
import uk.co.etothepii.haddock.db.tables.BFMarket;
import uk.co.etothepii.haddock.db.tables.MarketSelection;
import uk.co.etothepii.haddock.db.tables.Selection;
import uk.co.etothepii.haddock.db.tables.enumerations.YN;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class MarketSelectionFactory extends HaddockFactory<MarketSelection>{

    private static final Logger LOG = Logger.getLogger(MarketSelectionFactory.class);

    @Override
    protected String getTablename() {
        return "marketSelections";
    }

    private static MarketSelectionFactory factory;

    public static MarketSelectionFactory getFactory() {
        if (factory == null)
            factory = new MarketSelectionFactory();
        return factory;
    }

    public MarketSelectionFactory() {}

    @Override
    protected MarketSelection build(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            int marketId = rs.getInt("market");
            int selectionId = rs.getInt("selection");
            String winFlagStr = rs.getString("winFlag");
            BFMarket market = BFMarketFactory.getFactory().getFromId(marketId);
            Selection selection =
                    SelectionFactory.getFactory().getFromId(selectionId);
            return new MarketSelection(id, selection, market,
                    winFlagStr == null ? null : YN.valueOf(winFlagStr));
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
    }

    @Override
    protected String getInsertQueryStr() {
        return "INSERT INTO `" + getDatabasename() + "`.`" + getTablename()
                + "` (`id`, `market`, `selection`, `winFlag`) VALUES "
                + "(NULL, ?, ?, ?);";
    }

    @Override
    protected PreparedStatement prepareInsertStatement(MarketSelection t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        toRet.setInt(1, (int)t.getMarket().getId());
        toRet.setInt(2, (int)t.getSelection().getId());
        if (t.getWinFlag() == null) {
            toRet.setNull(3, Types.VARCHAR);
        }
        else {
            toRet.setString(3, t.getWinFlag().toString());
        }
        return toRet;
    }

    @Override
    protected String getUpdateQueryStr() {
        return "UPDATE `" + getDatabasename() + "`.`" + getTablename()
                + "` SET `market` = ?, `selection` = ?, `winFlag` = ? "
                + "WHERE `id` = ? LIMIT 1;";
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(MarketSelection t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        toRet.setInt(1, (int)t.getMarket().getId());
        toRet.setInt(2, (int)t.getSelection().getId());
        if (t.getWinFlag() == null) {
            toRet.setNull(3, Types.VARCHAR);
        }
        else {
            toRet.setString(3, t.getWinFlag().toString());
        }
        toRet.setInt(4, (int)t.getId());
        return toRet;
    }

    private HaddockStatement isSelectionMatchedToMarketQuery = new HaddockStatement(
            "SELECT `id` FROM `" + getDatabasename() + "`.`"
            + getTablename() + "` WHERE `market` = ? AND `selection` = ? "
            + "LIMIT 1;", false);

    public boolean isSelectionMatchedToMarket(Selection selection,
            BFMarket market) throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return isSelectionMatchedToMarket(selection, market, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public boolean isSelectionMatchedToMarket(Selection selection,
            BFMarket market, DatabaseConduit conduit) throws SQLException {
        PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = 
                isSelectionMatchedToMarketQuery.getStatement(conduit);
        	ps.setInt(1, (int)market.getId());
        	ps.setInt(2, (int)selection.getId());
        	LOG.debug(ps);
			rs = ps.executeQuery();
        	return rs.next();
		}
		finally {
			if (rs != null) {
			   	while (rs.next());
				rs.close();
			}
			if (ps != null) {
				ps.close();	
			}
		}
    }

}
