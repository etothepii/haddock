/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.timeform;

import java.net.URL;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class TimeformRace {

    private static final Logger LOG = Logger.getLogger(TimeformRace.class);

    public final Date scheduledOff;
    public final URL url;

    public TimeformRace(Date scheduledOff, URL url) {
        this.scheduledOff = scheduledOff;
        this.url = url;
    }

}
