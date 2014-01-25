/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables;

import org.apache.log4j.Logger;
import uk.co.etothepii.db.DataAccessObject;
import uk.co.etothepii.db.Field;
import uk.co.etothepii.db.FieldChangedEvent;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public abstract class BFDataAccessObject extends DataAccessObject {

    public static final Field BETFAIR_ID = new Field("betfairId",
            Integer.class, BFDataAccessObject.class);

    private static final Logger LOG = Logger.getLogger(BFDataAccessObject.class);

    public BFDataAccessObject(int id, int betfairId, boolean recalled) {
        super(id, recalled);
        this.betfairId = betfairId;
    }

    public BFDataAccessObject(int id, int betfairId) {
        this(id, betfairId, id != 0);
    }

    private int betfairId;

    public int getBetfairId() {
        return betfairId;
    }

    public void setBetfairId(int betfairId) {
        if (different(betfairId, this.betfairId)) {
            int old = this.betfairId;
            this.betfairId = betfairId;
            fireFieldChangedListener(new FieldChangedEvent(this, BETFAIR_ID,
                    old, betfairId));
            setChanged(true);
        }
    }

}
