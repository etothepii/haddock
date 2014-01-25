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
public class Funds {

    private static final Logger LOG = Logger.getLogger(Funds.class);

    public final double availBalance;
    public final double balance;
    public final double commissionRetain;
    public final double creditLimit;
    public final int currentBetfairPoints;
    public final double expoLimit;
    public final double exposure;
    public final int holidaysAvail;
    public final double nextDiscount;
    public final double withdrawBalance;

    public Funds(double availBalance, double balance, double commissionRetain, 
            double creditLimit, int currentBetfairPoints, double expoLimit, 
            double exposure, int holidaysAvail, double nextDiscount, 
            double withdrawBalance) {
        this.availBalance = availBalance;
        this.balance = balance;
        this.commissionRetain = commissionRetain;
        this.creditLimit = creditLimit;
        this.currentBetfairPoints = currentBetfairPoints;
        this.expoLimit = expoLimit;
        this.exposure = exposure;
        this.holidaysAvail = holidaysAvail;
        this.nextDiscount = nextDiscount;
        this.withdrawBalance = withdrawBalance;
    }

    public double getDecimalDiscount() {
        return nextDiscount / 100d;
    }

}
