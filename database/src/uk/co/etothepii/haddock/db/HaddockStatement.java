/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db;

import org.apache.log4j.Logger;
import uk.co.etothepii.db.CachedStatement;
import uk.co.etothepii.db.DatabaseConduit;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class HaddockStatement extends CachedStatement {

    private static final Logger LOG = Logger.getLogger(HaddockStatement.class);

    public HaddockStatement(String query, boolean returnKeys) {
        super(query, returnKeys);
    }

    @Override
    protected DatabaseConduit takeConduit() {
        return takeConduit(false);
    }

    @Override
    protected DatabaseConduit takeConduit(boolean verbose) {
        return MasterController.takeConduit(verbose);
    }

    @Override
    protected void releaseConduit(DatabaseConduit conduit) {
        releaseConduit(conduit, false);
    }

    @Override
    protected void releaseConduit(DatabaseConduit conduit,
            boolean verbose) {
        MasterController.releaseConduit(conduit, verbose);
    }

}
