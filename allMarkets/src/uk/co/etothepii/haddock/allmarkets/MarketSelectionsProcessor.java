/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.allmarkets;

import com.betfair.publicapi.types.exchange.v5.ArrayOfProfitAndLoss;
import com.betfair.publicapi.types.exchange.v5.GetMarketProfitAndLossReq;
import com.betfair.publicapi.types.exchange.v5.GetMarketProfitAndLossResp;
import com.betfair.publicapi.types.exchange.v5.ProfitAndLoss;
import com.mysql.jdbc.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import uk.co.epii.betfairclient.BFExchange;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.MasterController;
import uk.co.etothepii.haddock.db.tables.Alias;
import uk.co.etothepii.haddock.db.tables.BFMarket;
import uk.co.etothepii.haddock.db.tables.MarketSelection;
import uk.co.etothepii.haddock.db.tables.Selection;
import uk.co.etothepii.haddock.db.tables.enumerations.Vendor;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class MarketSelectionsProcessor {

    private static final Logger LOG = Logger.getLogger(
            MarketSelectionsProcessor.class);

    private final BFExchange bfExchange;

    private String tmpTbl = null;
    private String tmpTbl2 = null;
    private PreparedStatement insertSelection = null;
    private PreparedStatement insertAliases = null;
    private PreparedStatement insertMarketSelections = null;
    private PreparedStatement insertToTemp = null;
    private PreparedStatement insertToTemp2 = null;
    private PreparedStatement updateSelections = null;
    private PreparedStatement clearTemp = null;
    private PreparedStatement clearTemp2 = null;
    private PreparedStatement selectAliases = null;
    private PreparedStatement selectSelections = null;

    public MarketSelectionsProcessor(BFExchange bfExchange) {
        this.bfExchange = bfExchange;
    }

    private TreeMap<Integer, Alias> getAliases(
            List<ProfitAndLoss> profitAndLosses) throws SQLException{
        for (ProfitAndLoss pl : profitAndLosses) {
            insertToTemp2.setInt(1, pl.getSelectionId());
            insertToTemp2.addBatch();
        }
        insertToTemp2.executeBatch();
        insertToTemp2.clearBatch();
        selectAliases.setString(1, Vendor.BETFAIR_ID.name());
        ResultSet aliasesRS = selectAliases.executeQuery();
        clearTemp2.execute();
        TreeMap<Integer, Alias> toRet = new TreeMap<Integer, Alias>();
        TreeSet<Integer> selectionIds = new TreeSet<Integer>();
        while (aliasesRS.next()) {
            int selection = aliasesRS.getInt("selection");
            if (selectionIds.add(selection)) {
                insertToTemp2.setInt(1, selection);
                insertToTemp2.addBatch();
            }
        }
        insertToTemp2.executeBatch();
        insertToTemp2.clearBatch();
        TreeMap<Integer, Selection> selections =
                new TreeMap<Integer, Selection>();
        ResultSet selectionsRS = selectSelections.executeQuery();
        while (selectionsRS.next()) {
            int id = selectionsRS.getInt("id");
            String displayName = selectionsRS.getString("displayName");
            selections.put(id, new Selection(id, displayName));
        }
        aliasesRS.beforeFirst();

        while (aliasesRS.next()) {
            int id = aliasesRS.getInt("id");
            int selId = aliasesRS.getInt("selection");
            String alias = aliasesRS.getString("alias");
            String vendorStr = aliasesRS.getString("vendor");
            toRet.put(Integer.parseInt(alias),
                    new Alias(id, selections.get(selId), alias,
                    Vendor.valueOf(vendorStr)));
        }
        clearTemp2.execute();
        return toRet;
    }

    void openWithConduit(DatabaseConduit conduit) throws SQLException {
        tmpTbl = DatabaseConduit.getTemporaryTableName();
        PreparedStatement ps =
                conduit.prepareStatement("CREATE TABLE " + tmpTbl +
                " (seek INT, PRIMARY KEY (seek))");
        ps.execute();
        ps.close();
        tmpTbl2 = DatabaseConduit.getTemporaryTableName();
         ps =
                conduit.prepareStatement("CREATE TEMPORARY TABLE " + tmpTbl2 +
                " (seek INT, PRIMARY KEY (seek))");
        ps.execute();
        ps.close();
        insertSelection = conduit.prepareStatement("INSERT INTO `selections` "
                + "(`id`, `displayName`) VALUES (NULL, ?)",
                Statement.RETURN_GENERATED_KEYS);
        insertAliases = conduit.prepareStatement(
                "INSERT INTO `aliases` (`id`, `selection`, `alias`, `vendor`)"
                + " VALUES (NULL, ?, ?, ?)");
        insertMarketSelections = conduit.prepareStatement(
                "INSERT INTO `marketSelections` (`id`, `market`, `selection`)"
                + " VALUES (NULL, ?, ?)");
        insertToTemp = conduit.prepareStatement("INSERT INTO " + tmpTbl +
                " (seek) VALUES (?)");
        insertToTemp2 = conduit.prepareStatement("INSERT INTO " + tmpTbl2 +
                " (seek) VALUES (?)");
        updateSelections = conduit.prepareStatement("INSERT INTO marketSelections "
                + "(`market`, `selection`) SELECT ?, t.seek FROM "
                + tmpTbl + " t LEFT JOIN "
                + "(SELECT * FROM marketSelections WHERE market = ?) as s ON "
                + "(t.seek = s.selection) WHERE s.id IS NULL");
        clearTemp = conduit.prepareStatement("DELETE FROM " + tmpTbl);
        clearTemp2 = conduit.prepareStatement("DELETE FROM " + tmpTbl2);
        selectAliases = conduit.prepareStatement("SELECT DISTINCT a.* FROM " + tmpTbl2
                + " t, aliases a WHERE a.alias = t.seek AND vendor = ?");
        selectSelections = conduit.prepareStatement("SELECT s.* FROM "
                + tmpTbl2 + " t, selections s WHERE s.id = t.seek");
    }

    void closeWithConduit(DatabaseConduit conduit) throws SQLException {
        PreparedStatement ps =
                conduit.prepareStatement("DROP TABLE " + tmpTbl);
        ps.execute();
        ps.close();
        ps = conduit.prepareStatement("DROP TEMPORARY TABLE " + tmpTbl2);
        ps.execute();
        ps.close();
        insertSelection.close();
        insertAliases.close();
        insertMarketSelections.close();
        insertToTemp.close();
        insertToTemp2.close();
        updateSelections.close();
        clearTemp.close();
        clearTemp2.close();
        selectAliases.close();
        selectSelections.close();
    }

    boolean processMarket(BFMarket market, long startTime) 
            throws SQLException, InterruptedException {
        if (market.getBetfairId() == 0) {
            throw new IllegalArgumentException("No market Id");
        }
        List<ProfitAndLoss> profitAndLoss = bfExchange.getProfitAndLoss(market.getBetfairId());
        if (profitAndLoss == null)
            return false;
        TreeMap<Integer, Alias> aliases = getAliases(profitAndLoss);
        ArrayList<Selection> newSelections = new ArrayList<Selection>();
        ArrayList<Alias> newAliases = new ArrayList<Alias>();
        ArrayList<MarketSelection> newMarketSelections =
                new ArrayList<MarketSelection>();
        ArrayList<Selection> potentialSelections =
                new ArrayList<Selection>();
        LOG.debug("profitAndLoss.size(): " + profitAndLoss.size()
                + " runners: " + market.getRunners());
        for (ProfitAndLoss pl : profitAndLoss) {
            Integer selectionId = pl.getSelectionId();
            String selectionName = pl.getSelectionName();
            Alias alias = aliases.get(selectionId);
            if (alias == null) {
                Selection selection = new Selection(selectionName);
                newSelections.add(selection);
                alias = new Alias(selection,
                        selectionId.toString(), Vendor.BETFAIR_ID);
                newAliases.add(alias);
                newAliases.add(new Alias(selection, selectionName,
                        Vendor.BETFAIR_NAME));
                newMarketSelections.add(
                        new MarketSelection(selection, market, null));
            }
            else {
                potentialSelections.add(alias.getSelection());
            }
        }
        for (Selection s : newSelections) {
            insertSelection.setString(1, s.getDisplayName());
            insertSelection.addBatch();
        }
        if (LOG.isDebugEnabled())
            LOG.debug("Inserting " + newSelections.size() + " new selections");
        insertSelection.executeBatch();
        ResultSet rs = insertSelection.getGeneratedKeys();
        for (Selection s : newSelections) {
            rs.next();
            s.setId(rs.getInt(1));
        }
        rs.close();
        insertSelection.clearBatch();
        for (Alias a : newAliases) {
            insertAliases.setLong(1, a.getSelection().getId());
            insertAliases.setString(2, a.getAlias());
            insertAliases.setString(3, a.getVendor().name());
            insertAliases.addBatch();
        }
        if (LOG.isDebugEnabled())
            LOG.debug("Inserting " + newAliases.size() + " new aliases");
        insertAliases.executeBatch();
        insertAliases.clearBatch();
        for (MarketSelection s : newMarketSelections) {
            insertMarketSelections.setLong(1, s.getMarket().getId());
            insertMarketSelections.setLong(2, s.getSelection().getId());
            insertMarketSelections.addBatch();
        }
        if (LOG.isDebugEnabled())
            LOG.debug("Inserting " + newMarketSelections.size()
                    + " new marketSelections");
        insertMarketSelections.executeBatch();
        insertMarketSelections.clearBatch();
        for (Selection s : potentialSelections) {
            insertToTemp.setLong(1, s.getId());
            insertToTemp.addBatch();
        }
        if (LOG.isDebugEnabled())
            LOG.debug("Checking " + potentialSelections.size()
                    + " potential selections");
        insertToTemp.executeBatch();
        insertToTemp.clearBatch();
        updateSelections.setLong(1, market.getId());
        updateSelections.setLong(2, market.getId());
        LOG.debug(updateSelections);
        updateSelections.execute();
        clearTemp.execute();
        return true;
    }

}
