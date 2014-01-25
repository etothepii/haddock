/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.betfair.data;

import org.apache.log4j.Logger;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class Price {

    private static final Logger LOG = Logger.getLogger(Price.class);

    public final double price;
    public final double back;
    public final double lay;
    public final double totalBSPLayLiability;
    public final double totalBSPBackersStakeVolume;

    public Price(double price, double back, double lay,
            double totalBSPLayLiability, double totalBSPBackersStakeVolume) {
        this.price = price;
        this.back = back;
        this.lay = lay;
        this.totalBSPLayLiability = totalBSPLayLiability;
        this.totalBSPBackersStakeVolume = totalBSPBackersStakeVolume;
    }

    public static Price process(String raw) {
        return process(raw.split("~"));
    }

    public static Price process(String[] raw) {
        return new Price(Double.parseDouble(raw[0]), Double.parseDouble(raw[1]),
                Double.parseDouble(raw[2]), Double.parseDouble(raw[3]),
                Double.parseDouble(raw[4]));
    }


}
