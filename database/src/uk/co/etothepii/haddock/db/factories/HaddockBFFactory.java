/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.factories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.HaddockStatement;
import uk.co.etothepii.haddock.db.tables.BFDataAccessObject;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public abstract class HaddockBFFactory<T extends BFDataAccessObject>
        extends HaddockFactory<T> {

    private static final Logger LOG = Logger.getLogger(HaddockBFFactory.class);

    public HaddockBFFactory() {
//        LOG.debug("taking conduit");
//        DatabaseConduit conduit = takeConduit();
//        LOG.debug("conduit taken");
//        PreparedStatement ps = null;
//        try {
//            ps = conduit.prepareStatement(
//                    "SELECT `id`,`betfairId` FROM `" + getDatabasename()
//                    + "`.`" + getTablename() + "` WHERE 1");
//            LOG.debug("statement prepared");
//            ResultSet rs = null;
//            try {
//                rs = ps.executeQuery();
//            LOG.debug("query executed");
//                while (rs.next()) {
//                    int id = rs.getInt("id");
//                    int betfairId = rs.getInt("betfairId");
//                    idMap.put(betfairId, id);
//                }
//            }
//            finally {
//                if (rs != null) {
//                    try {while (rs.next()) {}}catch(SQLException ee){}
//                    try{rs.close();}catch (SQLException sqle) {}
//                }
//            }
//        }
//        catch (SQLException sqle) {
//            LOG.error(sqle);
//        }
//        finally {
//            if (ps != null) try{ps.close();}catch(SQLException e){}
//            releaseConduit(conduit);
//        }
    }

    private TreeMap<Integer, Integer> idMap = new TreeMap<Integer, Integer>();

    @Override
    protected T cache(T t) {
        synchronized (this) {
            LOG.debug("Caching");
            T toRet = super.cache(t);
            if(t==null) return t;
            Integer betfairId = t.getBetfairId();
            Integer id = (int)t.getId();
            idMap.put(betfairId, id);
            LOG.debug(idMap.get(betfairId));
            return toRet;
        }
    }

    private HaddockStatement getFromBetfairIdQuery = new HaddockStatement(
            "SELECT * FROM `" + getDatabasename() + "`.`" + getTablename() +
            "` WHERE `betfairId` = ?;", false);

    public T getFromBetfairId(int betfairId) {
        DatabaseConduit conduit = takeConduit();
        try {
            return getFromBetfairId(betfairId, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public T getFromBetfairId(int betfairId, DatabaseConduit conduit) {
        Integer id = idMap.get(betfairId);
        if (LOG.isDebugEnabled()) {
            LOG.debug("betfairId: " + betfairId);
            LOG.debug("id: " + (id == null ? "null" : id.toString()));
        }
        if (id != null)
            return getFromId(id);
        try {
            PreparedStatement ps = getFromBetfairIdQuery.getStatement(conduit);
            ps.setInt(1, betfairId);
            ResultSet rs = null;
            try {
                rs = ps.executeQuery();
                if (rs.next())
                    return build(rs);
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
        catch  (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
    }

}
