/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.allmarkets;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import uk.co.epii.betfairclient.BFExchange;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.MasterController;
import uk.co.etothepii.haddock.db.tables.BFMarket;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class);

    public static void execute(BFExchange bfExchange) {
        MarketProcessor mp = new MarketProcessor(bfExchange);
        MarketSelectionsProcessor msp = new MarketSelectionsProcessor(bfExchange);
        RaceProcessor rp = new RaceProcessor(bfExchange);
        long startedAt = System.currentTimeMillis();
        try {
            ArrayList<MarketProcessedResult> results = mp.update(startedAt);
            ArrayList<BFMarket> rpProcess = new ArrayList<BFMarket>();
            for (MarketProcessedResult mpr : results) {
                if (mpr.isRace)
                    rpProcess.add(mpr.market);
            }
            DatabaseConduit conduit = MasterController.takeConduit();
            try {
                PreparedStatement ps = conduit.prepareStatement(
                        "SELECT betfairId from (SELect m.id, m.betfairId, "
                        + "m.runners, count(*) count from bfMarkets m "
                        + "left join marketSelections s on (s.market = m.id) "
                        + "where m.type = 'O' and lastSeen = ? group by m.id) "
                        + "as x where x.runners > count or x.runners is null");
                ps.setTimestamp(1, new Timestamp(startedAt));
                ResultSet rs = ps.executeQuery();
                int count = 0;
                while (rs.next())
                    count++;
                rs.beforeFirst();
                LOG.debug(ps);
                LOG.info("returned: " + count);
                msp.openWithConduit(conduit);
                while (rs.next()) {
                    int bfId = rs.getInt(1);
                    LOG.debug("Processing Market Selections for " + bfId);
                    BFMarket market = mp.getMarkets().get(bfId);
                    if (market != null)
						try {
							msp.processMarket(market, startedAt);
						}
					    catch (RuntimeException re) {
                            LOG.error(bfId + " NOT LOADED");
					    }
                    else
                        LOG.error(bfId + " NOT LOADED");
                }
                LOG.debug("rpProcess.size(): " + rpProcess.size());
                if (!rpProcess.isEmpty()) {
                    rp.update(rpProcess);
                        ps = conduit.prepareStatement(
                            "UPDATE races r, bfMarkets m SET r.places = m.winners "
                            + "WHERE r.places is NULL AND r.bfPlaceMarket = m.id");
                    ps.execute();
                    ps.close();
                }
            }
            catch (SQLException sqle) {
                LOG.error(sqle.getMessage(), sqle);
            }
            finally {
                msp.closeWithConduit(conduit);
                MasterController.releaseConduit(conduit);
            }
        }
        catch (SQLException sqle) {
            LOG.error(sqle.getMessage(), sqle);
        }
        catch (InterruptedException ie) {
            LOG.error(ie.getMessage(), ie);
        }
    }


}
