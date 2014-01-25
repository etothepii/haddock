/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.betfair.data.event;

import com.betfair.publicapi.types.exchange.v5.ProfitAndLoss;
import java.util.EventListener;
import uk.co.etothepii.event.ChangeEvent;
import uk.co.etothepii.haddock.betfair.data.Runner;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public interface RunnerChangeListener extends EventListener {

    public void orderIndexChanged(ChangeEvent<Runner, Integer> e);
    public void totalAmountMatchedChanged(ChangeEvent<Runner, Double> e);
    public void lastPriceMatchedChanged(ChangeEvent<Runner, Double> e);
    public void handicapChanged(ChangeEvent<Runner, Double> e);
    public void reductionFactorChanged(ChangeEvent<Runner, Double> e);
    public void vacantChanged(ChangeEvent<Runner, Boolean> e);
    public void asianLineIdChanged(ChangeEvent<Runner, Integer> e);
    public void farSPPriceChanged(ChangeEvent<Runner, Double> e);
    public void nearSPPriceChanged(ChangeEvent<Runner, Double> e);
    public void actualSPPriceChanged(ChangeEvent<Runner, Double> e);
    public void totalBSPBackMatchedAmountChanged(ChangeEvent<Runner, Double> e);
    public void totalBSPLiabilityMatchedAmount(ChangeEvent<Runner, Double> e);
    public void nameChanged(ChangeEvent<Runner, String> e);
    public void profitAndLossChanged(ChangeEvent<Runner, ProfitAndLoss> e);
    
}
