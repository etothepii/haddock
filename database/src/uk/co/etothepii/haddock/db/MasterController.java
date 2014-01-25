 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.util.ThreadController;


/**
 *
 * @author jrrpl
 */
public class MasterController {

    private static final Logger LOG = Logger.getLogger(MasterController.class);

    private static ArrayList<DatabaseConduit> conduits =
            new ArrayList<DatabaseConduit>();
    private static int conduitsCreated = 0;
    private static long checkedAt;

    private static final ThreadController threadController =
            new ThreadController(new Runnable() {
        public void run() {
            try {
                checkedAt = System.currentTimeMillis();
                synchronized (MasterController.class) {
                    for (int i = 0; i < conduits.size(); i++) {
                        DatabaseConduit c = conduits.get(i);
                        if (checkedAt - c.getLastUsed() > 3600000) {
                            conduits.remove(i);
                            LOG.debug("Available conduits: " + conduits.size());
                            LOG.debug("Total conduits: " + conduitsCreated);
                            c.dispose();
                            i--;
                        }
                    }
                }
                long sleep = checkedAt + 3600000 - System.currentTimeMillis();
                if (sleep > 0)
                    Thread.sleep(sleep);
            }
            catch (InterruptedException ie) {}
        }
    });

    static {
        threadController.start();
    }

    public static synchronized DatabaseConduit takeConduit() {
        return takeConduit(false);
    }

    public static synchronized DatabaseConduit takeConduit(boolean verbose) {
        LOG.debug("Conduit taken!");
        LOG.debug("Available conduits: " + conduits.size());
        LOG.debug("Total conduits: " + conduitsCreated);
        DatabaseConduit conduit = null;
        while (conduit == null && !conduits.isEmpty()) {
            DatabaseConduit temp = conduits.remove(0);
            if (temp.tinyQry())
                conduit = temp;
        }
        if (conduit == null) {
            conduit = new HaddockConduit("haddockadmin",
                    "a14BGzfkKBoU","haddockdb", new String[] {"autoReconnect"},
                    new String[] {"true"});
            conduitsCreated++;
        }
        return conduit;
    }

    public static synchronized void releaseConduit(
            DatabaseConduit conduit) {
        releaseConduit(conduit, false);
    }

    public static synchronized void releaseConduit(DatabaseConduit conduit,
            boolean verbose) {
        LOG.debug("Conduit released!");
        LOG.debug("Available conduits: " + conduits.size());
        LOG.debug("Total conduits: " + conduitsCreated);
        if (conduit != null) {
            conduits.add(conduit);
        }
    }

    private static class HaddockConduit extends DatabaseConduit {

        public HaddockConduit(String username, String password, String db) {
            super(username, password, db);
        }

        public HaddockConduit(String username, String password, String db,
                String[] propertyNames, String[] propertyValues) {
            super(username, password, db, propertyNames, propertyValues);
        }

        @Override
        public boolean tinyQry() {
			PreparedStatement ps = null;
			ResultSet rs = null;
            try {
				ps = prepareStatement(
                        "SELECT id FROM aliases LIMIT 1");
                rs = ps.executeQuery();
                if (rs.next())
                    return true;
            }
            catch (SQLException sqle) {
			throw new RuntimeException(sqle);}
			finally {
				if (rs != null) {
					try {
					    while (rs.next());
						rs.close();
					}
					catch (SQLException sqle) {
			throw new RuntimeException(sqle);
					}
				}
				if (ps != null) {
					try {
						ps.close();
					}
					catch (SQLException sqle) {
			throw new RuntimeException(sqle);
					}
				}
			}
            return false;
        }

    }
}
