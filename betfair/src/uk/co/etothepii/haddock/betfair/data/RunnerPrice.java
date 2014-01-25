/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.betfair.data;

import org.apache.log4j.Logger;
import uk.co.etothepii.event.ChangeEvent;
import uk.co.etothepii.util.Changes;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class RunnerPrice {

    private static final Logger LOG = Logger.getLogger(RunnerPrice.class);

    public static enum Field{AMOUNT, DIRECTION, TOTAL_BSP_LAY_LIABILITY,
            TOTAL_BSP_BACKERS_STAKE_VOLUME, MATCHED
    };

    public static final double[] PRICES;

    static {
        PRICES = new double[350];
        int[] blockSize =
                new int[] {100, 50, 20, 20, 20, 20, 10, 10, 10, 90};
        double[] priceIncrement =
                new double[] {.01d, .02d, .05d, .1d, .2d, .5d, 1d, 2d, 5d, 10d};
        int index = -1;
        double price = 1d;
        for (int block = 0; block < 10; block++) {
            for (int i = 1; i <= blockSize[block]; i++) {
                index++;
                price += priceIncrement[block];
                PRICES[index] = price;
            }
        }
    }

    public final double price;
    public final int index;
    private final Runner runner;
    private Double amount;
    private BackLay direction;
    private Double totalBSPLayLiability;
    private Double totalBSPBackersStakeVolume;
    private Double matched;

    public RunnerPrice(Runner runner, double price) {
        this(runner, price, 0d, BackLay.NONE, 0d, 0d, 0d);
    }

    public RunnerPrice(Runner runner, double price, double amount, BackLay direction,
            double totalBSPLayLiability, double totalBSPBackersStakeVolume,
            double matched) {
        this.runner = runner;
        this.price = price;
        this.amount = amount;
        this.direction = direction;
        this.totalBSPLayLiability = totalBSPLayLiability;
        this.totalBSPBackersStakeVolume = totalBSPBackersStakeVolume;
        this.matched = matched;
        this.index = getIndex(price);
    }

    public RunnerPrice clone(Runner runner) {
        return new RunnerPrice(runner, price, amount, direction,
                totalBSPLayLiability, totalBSPBackersStakeVolume, matched);
    }

    public double getAmount() {
        return amount;
    }

    public BackLay getDirection() {
        return direction;
    }

    public double getMatched() {
        return matched;
    }

    public double getTotalBSPBackersStakeVolume() {
        return totalBSPBackersStakeVolume;
    }

    public double getTotalBSPLayLiability() {
        return totalBSPLayLiability;
    }

    /**
     *
     * @param back
     * @return whether the amount specified was greater than 0
     */
    public boolean setBack(double back) {
        setAmount(back);
        if (back == 0) {
            setDirection(BackLay.NONE);
            return false;
        }
        else {
            setDirection(BackLay.BACK);
            return true;
        }
    }

    /**
     *
     * @param lay
     * @return whether the amount specified was greater than 0
     */
    public boolean setLay(double lay) {
        setAmount(lay);
        if (lay == 0) {
            setDirection(BackLay.NONE);
            return false;
        }
        else {
            setDirection(BackLay.LAY);
            return true;
        }
    }

    private void setAmount(Double amount) {
        if (Changes.different(this.amount, amount)) {
            Double oldVal = this.amount;
            Double newVal = amount;
            this.amount = amount;
            runner.fireAmountChanged(new ChangeEvent<RunnerPrice, Double>(
                    this, oldVal, newVal));
        }
    }

    boolean setDirection(BackLay direction) {
        if (Changes.different(this.direction, direction)) {
            BackLay oldVal = this.direction;
            BackLay newVal = direction;
            this.direction = direction;
            runner.fireDirectionChanged(new ChangeEvent<RunnerPrice, BackLay>(
                    this, oldVal, newVal));
            return true;
        }
        return false;
    }

    public void setMatched(Double matched) {
        if (Changes.different(matched, this.matched)) {
            Double oldVal = this.matched;
            Double newVal = matched;
            this.matched = matched;
            runner.fireMatchedChanged(new ChangeEvent<RunnerPrice, Double>(
                    this, oldVal, newVal));
        }
    }

    public void setTotalBSPBackersStakeVolume(
            Double totalBSPBackersStakeVolume) {
        if (Changes.different(totalBSPBackersStakeVolume,
                this.totalBSPBackersStakeVolume)) {
            Double oldVal = this.totalBSPBackersStakeVolume;
            Double newVal = totalBSPBackersStakeVolume;
            this.totalBSPBackersStakeVolume = totalBSPBackersStakeVolume;
            runner.fireTotalBSPBackersStakeVolumeChanged(
                    new ChangeEvent<RunnerPrice, Double>(this, oldVal, newVal));
        }
    }

    public void setTotalBSPLayLiability(Double totalBSPLayLiability) {
        if (Changes.different(this.totalBSPLayLiability,
                totalBSPLayLiability)) {
            Double oldVal = this.totalBSPLayLiability;
            Double newVal = totalBSPLayLiability;
            this.totalBSPLayLiability = totalBSPLayLiability;
            runner.fireTotalBSPLayLiabilityChanged(
                    new ChangeEvent<RunnerPrice, Double>(this, oldVal, newVal));
        }
    }

    public static RunnerPrice[] getCleanPrices(Runner runner) {
        RunnerPrice[] toRet = new RunnerPrice[PRICES.length];
        for (int i = 0; i < PRICES.length; i++)
            toRet[i] = new RunnerPrice(runner, PRICES[i]);
        return toRet;
    }

    public Runner getRunner() {
        return runner;
    }

    public static int getIndex(double price) {
        if (price > 1005)
            throw new IllegalArgumentException("Price too big: " + price);
        else if(price > 105) {
            return (int)((price - 100) / 10d + .5) + 259;
        }
        else if(price > 52.5) {
            return (int)((price - 50) / 5d + .5) + 249;
        }
        else if(price > 31) {
            return (int)((price - 30) / 2d + .5) + 239;
        }
        else if(price > 20.5) {
            return (int)((price - 20) / 1d + .5) + 229;
        }
        else if(price > 10.25) {
            return (int)((price - 10) / .5d + .5) + 209;
        }
        else if(price > 6.1) {
            return (int)((price - 6) / .2d + .5) + 189;
        }
        else if(price > 4.05) {
            return (int)((price - 4) / .1d + .5) + 169;
        }
        else if(price > 3.025) {
            return (int)((price - 3) / .05d + .5) + 149;
        }
        else if(price > 2.01) {
            return (int)((price - 2) / .02d + .5) + 99;
        }
        else if(price > 1.005) {
            return (int)((price - 1) / .01d + .5) -1;
        }
        else
            throw new IllegalArgumentException("Price too small: " + price);
    }
}
