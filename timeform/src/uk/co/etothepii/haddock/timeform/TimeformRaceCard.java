/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.timeform;

import java.net.URL;
import org.apache.log4j.Logger;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class TimeformRaceCard {

    private static final Logger LOG = Logger.getLogger(TimeformRaceCard.class);

    public final String racecourse;
    public final String going;
    public final String countryCode;
    public final URL url;
    public final TimeformRace[] races;

    public TimeformRaceCard(String racecourse, String going, String countryCode, 
            URL url, TimeformRace[] races) {
        this.racecourse = racecourse;
        this.going = going;
        this.countryCode = countryCode;
        this.url = url;
        this.races = races;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(racecourse);
        sb.append("~");
        sb.append(going);
        sb.append("~");
        sb.append(countryCode);
        sb.append("~");
        sb.append(url);
        return sb.toString();
    }

}
