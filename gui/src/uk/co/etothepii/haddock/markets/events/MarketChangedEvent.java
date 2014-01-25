/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.markets.events;

import java.util.EventObject;
import org.apache.log4j.Logger;
import uk.co.etothepii.haddock.db.tables.BFMarket;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class MarketChangedEvent extends EventObject {

    private static final Logger LOG = Logger.getLogger(MarketChangedEvent.class);

    private BFMarket market;

    public MarketChangedEvent(Object source, BFMarket market) {
        super(source);
        this.market = market;
    }

    public BFMarket getMarket() {
        return market;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append("[");
        sb.append(source.toString());
        sb.append(",");
        sb.append(market.toString());
        sb.append("]");
        return sb.toString();
    }

}
