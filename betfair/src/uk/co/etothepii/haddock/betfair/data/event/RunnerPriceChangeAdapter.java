/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.betfair.data.event;

import uk.co.etothepii.event.ChangeEvent;
import uk.co.etothepii.haddock.betfair.data.BackLay;
import uk.co.etothepii.haddock.betfair.data.RunnerPrice;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class RunnerPriceChangeAdapter implements RunnerPriceChangeListener {

    public void amountChanged(ChangeEvent<RunnerPrice, Double> e) {}
    public void directionChanged(ChangeEvent<RunnerPrice, BackLay> e) {}
    public void totalBSPLayLiabilityChanged(ChangeEvent<RunnerPrice, Double> e) {}
    public void totalBSPBackersStakeVolumeChanged(
            ChangeEvent<RunnerPrice, Double> e) {}
    public void matchedChanged(ChangeEvent<RunnerPrice, Double> e) {}

}
