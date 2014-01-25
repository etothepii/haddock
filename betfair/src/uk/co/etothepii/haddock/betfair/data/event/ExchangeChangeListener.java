/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.betfair.data.event;

import java.util.EventListener;
import uk.co.etothepii.event.ChangeEvent;
import uk.co.etothepii.haddock.betfair.data.Exchange;
import uk.co.etothepii.haddock.betfair.data.RemovedRunner;
import uk.co.etothepii.haddock.betfair.data.Runner;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public interface ExchangeChangeListener extends EventListener {

    public void inPlayDelayChanged(ChangeEvent<Exchange, Integer> e);
    public void runnerRemoved(ChangeEvent<Exchange, Runner> e);
    public void removedRunnerAdded(ChangeEvent<Exchange, RemovedRunner> e);

}
