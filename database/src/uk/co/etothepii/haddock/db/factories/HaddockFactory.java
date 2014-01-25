/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.factories;

import uk.co.etothepii.db.CachedStatement;
import uk.co.etothepii.db.DataAccessObject;
import uk.co.etothepii.db.DataAccessObjectFactory;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.HaddockStatement;
import uk.co.etothepii.haddock.db.MasterController;

/**
 *
 * @author jrrpl
 */
public abstract class HaddockFactory<T extends DataAccessObject>
        extends DataAccessObjectFactory<T> {

    @Override
    protected DatabaseConduit takeConduit() {
        return MasterController.takeConduit();
    }
    @Override
    protected void releaseConduit(DatabaseConduit conduit) {
        MasterController.releaseConduit(conduit);
    }

    @Override
    protected String getDatabasename() {
        return "haddockdb";
    }

    @Override
    protected CachedStatement getCachedStatement(String query,
            boolean returnKeys) {
        return new HaddockStatement(query, returnKeys);
    }
    
}
