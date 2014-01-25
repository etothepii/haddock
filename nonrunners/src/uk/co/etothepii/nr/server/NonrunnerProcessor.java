/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.nr.server;

import java.sql.SQLException;
import java.util.concurrent.Exchanger;
import org.apache.log4j.Logger;
import uk.co.etothepii.haddock.betfair.data.Exchange;
import uk.co.etothepii.haddock.db.factories.AliasFactory;
import uk.co.etothepii.haddock.db.factories.NonrunnerFactory;
import uk.co.etothepii.haddock.db.tables.Alias;
import uk.co.etothepii.haddock.db.tables.BFMarket;
import uk.co.etothepii.haddock.db.tables.Nonrunner;
import uk.co.etothepii.haddock.db.tables.enumerations.NonrunnerResponse;
import uk.co.etothepii.haddock.db.tables.enumerations.Vendor;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class NonrunnerProcessor {

    private static final Logger LOG =
            Logger.getLogger(NonrunnerProcessor.class);

    public NonrunnerProcessor() {
    }

    public void newNonrunner(long id) {
        try {
            Nonrunner n = NonrunnerFactory.getFactory().getFromId(id);
            if (n.getRace().getMarket() == null) {
                n.setNonrunnerResponse(
                        NonrunnerResponse.MARKET_NOT_YET_MATCHED);
                NonrunnerFactory.getFactory().save(n);
                return;
            }
            BFMarket m = n.getRace().getMarket();
            if (n.getAlias().getSelection() == null) {
                n.setNonrunnerResponse(NonrunnerResponse.NO_NONRUNNER_ATTACHED);
                NonrunnerFactory.getFactory().save(n);
                return;
            }
            Alias betfairId = AliasFactory.getFactory(
                    ).getFromSelectionAndVendor(n.getAlias().getSelection(),
                    Vendor.BETFAIR_ID);
            
        }
        catch (SQLException sqle) {
            LOG.error(sqle.getMessage(), sqle);
        }
    }

}