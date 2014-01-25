/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.markets.events;

import java.util.EventListener;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public interface MarketChangedListener extends EventListener {

    public void marketChanged(MarketChangedEvent e);

}
