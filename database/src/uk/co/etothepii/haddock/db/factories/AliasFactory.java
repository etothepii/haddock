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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.HaddockStatement;
import uk.co.etothepii.haddock.db.tables.Alias;
import uk.co.etothepii.haddock.db.tables.BFMarket;
import uk.co.etothepii.haddock.db.tables.Selection;
import uk.co.etothepii.haddock.db.tables.enumerations.Vendor;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class AliasFactory extends HaddockFactory<Alias> {

    private static final Logger LOG = Logger.getLogger(AliasFactory.class);
    private static final Logger LOG_SAV =
            Logger.getLogger(AliasFactory.class.getName().concat(".sav"));

    static {
        LOG.debug("AliasFactory debugging");
    }

    @Override
    protected String getTablename() {
        return "aliases";
    }

    private static AliasFactory factory;

    public static synchronized AliasFactory getFactory() {
        if (factory == null)
            factory = new AliasFactory();
        return factory;
    }

    private Map<Vendor, Map<String, Integer>> aliasMap;
    private Map<Vendor, Map<Integer, Integer>> selectionMap;

    private AliasFactory() {
        aliasMap = new EnumMap<Vendor, Map<String, Integer>>(Vendor.class);
        for (Vendor v : Vendor.values()) {
            aliasMap.put(v, new TreeMap<String, Integer>());
        }
        selectionMap = new EnumMap<Vendor, Map<Integer, Integer>>(Vendor.class);
        for (Vendor v : Vendor.values()) {
            selectionMap.put(v, new TreeMap<Integer, Integer>());
        }
    }

    @Override
    protected Alias build(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            int selectionId = rs.getInt("selection");
            String alias = rs.getString("alias");
            Vendor vendor = Vendor.valueOf(rs.getString("vendor"));
            return new Alias(id, SelectionFactory.getFactory().getFromId(
                    selectionId), alias, vendor);
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
    }

    @Override
    protected String getInsertQueryStr() {
        return "INSERT INTO `" + getDatabasename() + "`.`" + getTablename()
                + "` (`id`, `selection`, `alias`, `vendor`) VALUES ("
                + "NULL, ?, ?, ?);";
    }

    @Override
    protected PreparedStatement prepareInsertStatement(Alias t,
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        if (t.getSelection() == null)
            toRet.setNull(1, Types.INTEGER);
        else
            toRet.setInt(1, (int)t.getSelection().getId());
        toRet.setString(2, t.getAlias());
        toRet.setString(3, t.getVendor().name());
        return toRet;
    }

    @Override
    protected String getUpdateQueryStr() {
        return "UPDATE `" + getDatabasename() + "`.`" + getTablename()
                + "` SET `selection` = ?, `alias` = ?, `vendor` = ? "
                + "WHERE `id` = ? LIMIT 1;";
    }

    private HaddockStatement getAliasesAttachedToMarketQuery =
            new HaddockStatement("SELECT `a`.* FROM `marketSelections` as `ms`,"
            + " `selections` as `s`, `aliases` a WHERE `ms`.`selection` = `s`.`id` AND "
            + "`ms`.`market` = ? AND `a`.`vendor` = ? AND a.selection = s.id", false);

    public ArrayList<Alias> getAliasesAttachedToMarket(BFMarket market, Vendor v,
            boolean cache) throws SQLException {
        DatabaseConduit conduit = takeConduit();
		PreparedStatement ps = null;
		ResultSet rs = null;
        try {
            ps =
                    getAliasesAttachedToMarketQuery.getStatement(conduit);
            ps.setLong(1, market.getId());
            ps.setString(2, v.name());
            rs = ps.executeQuery();
            ArrayList<Alias> aliases = new ArrayList<Alias>();
            int size = 0;
            rs.beforeFirst();
            rs.last();
            size = rs.getRow();
            LOG.debug("Aliases returned: " + size);
            rs.beforeFirst();
            while (rs.next()) {
                Alias a = build(rs);
                if (cache)
                    cache(a);
                aliases.add(a);
            }
            return aliases;
        }
        finally {
			if (rs != null) {
				while (rs.next());
				rs.close();	
			}
			if (ps != null) {
				ps.close();
			}
            releaseConduit(conduit);
        }
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(Alias t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledUpdateStatement(conduit);
        if (t.getSelection() == null)
            toRet.setNull(1, Types.INTEGER);
        else
            toRet.setInt(1, (int)t.getSelection().getId());
        toRet.setString(2, t.getAlias());
        toRet.setString(3, t.getVendor().name());
        toRet.setInt(4, (int)t.getId());
        return toRet;
    }

    @Override
    protected Alias cache(Alias t) {
        if (t == null || t.getSelection() == null) return t;
        synchronized (this) {
            LOG.debug("Caching");
            Alias toRet = super.cache(t);
            int id = (int)toRet.getId();
            aliasMap.get(t.getVendor()).put(t.getAlias(), id);
            selectionMap.get(t.getVendor()).put((int)t.getSelection().getId(), id);
            LOG.debug(aliasMap.get(t.getVendor()).get(t.getAlias()));
            return toRet;
        }
    }
    
    private HaddockStatement getAliasesForRacesBetweenQuery = 
            new HaddockStatement("SELECT r.id as r, a.* FROM (SELECT * "
            + "FROM races as r WHERE r.scheduled > ? AND r.scheduled < ?) as r "
            + "inner join bfMarkets as b on r.bfMarket = b.id "
            + "inner join marketSelections as ms on b.id = ms.market "
            + "inner join selections as s on ms.selection = s.id "
            + "inner join aliases as a on a.selection = s.id "
            + "WHERE vendor LIKE 'BETFAIR_ID'", false);

    public TreeMap<Integer, ArrayList<Alias>> getAllAliasesForRacesBetween(
        Date start, Date stop) throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
             return getAllAliasesForRacesBetween(start, stop, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public TreeMap<Integer, ArrayList<Alias>> getAllAliasesForRacesBetween(
        Date start, Date stop, DatabaseConduit conduit) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
        	TreeMap<Integer, ArrayList<Alias>> toRet =
                	new TreeMap<Integer, ArrayList<Alias>>();
        	ps = getAliasesForRacesBetweenQuery.getStatement(conduit);
        	ps.setTimestamp(1, new Timestamp(start.getTime()));
        	ps.setTimestamp(2, new Timestamp(stop.getTime()));
        	rs = ps.executeQuery();
        	while (rs.next()) {
            	Integer raceId = rs.getInt("r");
            	LOG.debug("raceId: " + raceId);
            	ArrayList<Alias> aliases = toRet.get(raceId);
            	if (aliases == null) {
                	aliases = new ArrayList<Alias>();
                	toRet.put(raceId, aliases);
            	}
            	aliases.add(build(rs));
        	}
        	return toRet;
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

    private HaddockStatement getFromAliasAndVendorQuery = new HaddockStatement(
            "SELECT * FROM `" + getDatabasename() + "`.`" + getTablename() +
            "` WHERE `alias` = ? AND `vendor` = ? LIMIT 1;", false);

    public Alias getFromAliasAndVendor(String alias, Vendor vendor) {
        DatabaseConduit conduit = takeConduit();
        try {
            return getFromAliasAndVendor(alias, vendor, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public TreeMap<String, Alias> getFromAliasesAndVendor(List<String> aliases,
            Vendor vendor, boolean cache) throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return getFromAliasesAndVendor(aliases, vendor, conduit, cache);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public TreeMap<String, Alias> getFromAliasesAndVendor(List<String> aliases,
            Vendor vendor, DatabaseConduit conduit, boolean cache)
            throws SQLException {
        StringBuilder qry = new StringBuilder("SELECT * FROM `");
        qry.append(getDatabasename());
        qry.append("`.`");
        qry.append(getTablename());
        qry.append("` WHERE `alias` IN (");
        TreeMap<String, Alias> toRet = new TreeMap<String, Alias>();
        ArrayList<String> seeking = new ArrayList<String>();
        for (String alias : aliases) {
            Integer id = aliasMap.get(vendor).get(alias);
            if (id != null) 
                toRet.put(alias, getFromId(id));
            else
                seeking.add(alias);
        }
        if (seeking.isEmpty())
            return toRet;
        qry.append("'");
        qry.append(seeking.get(0));
        qry.append("'");
        for (int i = 1; i < seeking.size(); i++) {
            qry.append(", '");
            qry.append(seeking.get(i));
            qry.append("'");
        }
        qry.append(") AND `vendor` = '");
        qry.append(vendor.name());
        qry.append("' LIMIT 1;");
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = conduit.prepareStatement(qry.toString());
            rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                Alias a = build(rs);
                if (cache)
                    cache(a);
                toRet.put(a.getAlias(), a);
                seeking.remove(a.getAlias());
                count++;
            }
            LOG.debug("returned " + count + " aliases");
            for (String alias : seeking)
                toRet.put(alias, null);
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

    public Alias getFromAliasAndVendor(String alias, Vendor vendor,
            DatabaseConduit conduit) {
        Integer id = aliasMap.get(vendor).get(alias);
        if (LOG.isDebugEnabled()) {
            LOG.debug("vendor: " + vendor.name());
            LOG.debug("alias: " + alias);
            LOG.debug("id: " + (id == null ? "null" : id.toString()));
        }
        if (id != null)
            return getFromId(id);
        ResultSet rs = null;
		PreparedStatement ps = null;
        Alias a = null;
        try {
            ps = getFromAliasAndVendorQuery.getStatement(conduit);
            LOG.debug("alias: " + alias);
            ps.setString(1, alias);
            ps.setString(2, vendor.name());
            LOG.debug(ps);
             rs = ps.executeQuery();
            if (rs.next())
                a = cache(build(rs));
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
        return a;
    }

    private HaddockStatement getFromSelectionAndVendorQuery = new HaddockStatement(
            "SELECT * FROM `" + getDatabasename() + "`.`" + getTablename() +
            "` WHERE `selection` = ? AND `vendor` = ? LIMIT 1;", false);

    public Alias getFromSelectionAndVendor(Selection selection, Vendor vendor) {
        DatabaseConduit conduit = takeConduit();
        try {
            return getFromSelectionAndVendor(selection, vendor, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public Alias getFromSelectionAndVendor(Selection selection, Vendor vendor,
            DatabaseConduit conduit) {
        if (selection == null) return null;
        Integer id = selectionMap.get(vendor).get((int)selection.getId());
        if (LOG_SAV.isDebugEnabled()) {
            LOG_SAV.debug("vendor: " + vendor.name());
            LOG_SAV.debug("selection: " + selection.getDisplayName().toString());
            LOG_SAV.debug("id: " + (id == null ? "null" : id.toString()));
        }
        if (id != null)
            return getFromId(id);
        ResultSet rs = null;
		PreparedStatement ps = null;
        Alias a = null;
        try {
            ps = getFromSelectionAndVendorQuery.getStatement(conduit);
            ps.setInt(1, (int)selection.getId());
            ps.setString(2, vendor.name());
            rs = ps.executeQuery();
            if (rs.next())
                a = cache(build(rs));
        }
        catch (SQLException sqle) {
            DatabaseConduit.printSQLException(sqle);
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
        return a;
    }
}
