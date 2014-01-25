/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.betfair.viewmodel;

import org.apache.log4j.Logger;
import uk.co.etothepii.haddock.betfair.data.Runner;

/**
 *
 *
 * @author jrrpl
 */
public class RunnerViewModel {

    private static final Logger LOG = Logger.getLogger(RunnerViewModel.class);

    private Runner runner;

    public RunnerViewModel(Runner runner) {
        this.runner = runner;
    }

    

}
