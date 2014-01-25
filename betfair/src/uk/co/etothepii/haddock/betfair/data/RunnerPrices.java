/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.betfair.data;

import com.betfair.publicapi.types.exchange.v5.BetTypeEnum;
import com.betfair.publicapi.types.exchange.v5.PlaceBets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.apache.log4j.Logger;
import uk.co.etothepii.haddock.bettingconcepts.BetType;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class RunnerPrices {

    private static final Logger LOG = Logger.getLogger(RunnerPrices.class);

    private final Runner runner;
    private RunnerPrice[] prices;
    private ArrayList<Integer> backIndices;
    private ArrayList<Integer> layIndices;

    public RunnerPrices(Runner runner) {
        this.runner = runner;
        prices = RunnerPrice.getCleanPrices(runner);
        backIndices = new ArrayList<Integer>();
        layIndices = new ArrayList<Integer>();
    }

    private RunnerPrices(Runner runner, RunnerPrice[] prices,
            ArrayList<Integer> backIndices, ArrayList<Integer> layIndices) {
        this.runner = runner;
        this.prices = prices;
        this.backIndices = backIndices;
        this.layIndices = layIndices;
    }



    public double getAmount(double price) {
        synchronized (runner) {
            return getPrice(price).getAmount();
        }
    }

    public RunnerPrices clone(Runner runner) {
        RunnerPrices toRet = new RunnerPrices(runner);
        RunnerPrice[] runnerPrices = new RunnerPrice[prices.length];
        for (int i = 0; i < prices.length; i++)
            runnerPrices[i] = prices[i].clone(runner);
        ArrayList<Integer> back = new ArrayList<Integer>();
        Collections.copy(backIndices, back);
        ArrayList<Integer> lay = new ArrayList<Integer>();
        Collections.copy(layIndices, lay);
        return new RunnerPrices(runner, prices, backIndices, layIndices);
    }



    public RunnerPrice[] getPrices() {
        return prices;
    }

    public BackLay getDirection(double price) {
        synchronized (runner) {
            return getPrice(price).getDirection();
        }
    }

    public double getMatched(double price) {
        synchronized (runner) {
            return getPrice(price).getMatched();
        }
    }

    public double getTotalBSPBackersStakeVolume(double price) {
        synchronized (runner) {
            return getPrice(price).getTotalBSPBackersStakeVolume();
        }
    }

    public double getTotalBSPLayLiability(double price) {
        synchronized (runner) {
            return getPrice(price).getTotalBSPLayLiability();
        }
    }

    public void setBack(double price, double back) {
        synchronized (runner) {
            RunnerPrice rp = getPrice(price);
            layIndices.remove(new Integer(rp.index));
            if (rp.setBack(back)) {
                if (!backIndices.contains(new Integer(rp.index))) {
                    for (int i = 0; i < backIndices.size(); i++) {
                        if (rp.index > backIndices.get(i)) {
                            backIndices.add(i, new Integer(rp.index));
                            return;
                        }
                    }
                    backIndices.add(new Integer(rp.index));
                }
                for (int i = rp.index - 1; i >= 0; i--) {
                    if (!prices[i].setDirection(BackLay.BACK)) break;
                }
            }
            else {
                backIndices.remove(new Integer(rp.index));
            }
        }
    }

    public void setLay(double price, double lay) {
        synchronized (runner) {
            RunnerPrice rp = getPrice(price);
            backIndices.remove(new Integer(rp.index));
            LOG.debug("(price, lay): (" + price + ", " + lay + ")");
            if (rp.setLay(lay)) {
                if (!layIndices.contains(new Integer(rp.index))) {
                    for (int i = 0; i < layIndices.size(); i++) {
                        if (rp.index < layIndices.get(i)) {
                            layIndices.add(i, new Integer(rp.index));
                            return;
                        }
                    }
                    layIndices.add(new Integer(rp.index));
                }
                for (int i = rp.index + 1; i < prices.length; i++) {
                    if (!prices[i].setDirection(BackLay.LAY)) break;
                }
            }
            else {
                layIndices.remove(new Integer(rp.index));
            }
        }
    }

    public void emulate(PlaceBets p) {
        double toSet = getAmount(p.getPrice()) - p.getSize();
        if (toSet < 0.015) toSet = 0d;
        if (p.getBetType() == BetTypeEnum.L)
            setLay(p.getPrice(), toSet);
        else
            setBack(p.getPrice(), toSet);
    }

    public int getSize() {
        return prices.length;
    }

    public RunnerPrice get(int index) {
        try {
            return prices[index];
        }
        catch (ArrayIndexOutOfBoundsException aioobe) {
            return null;
        }
    }

    public void setMatched(double price, double matched) {
        synchronized (runner) {
            getPrice(price).setMatched(matched);
        }
    }

    public void setTotalBSPBackersStakeVolume(double price,
            double totalBSPBackersStakeVolume) {
        synchronized (runner) {
            getPrice(price).setTotalBSPBackersStakeVolume(
                    totalBSPBackersStakeVolume);
        }
    }

    public void setTotalBSPLayLiability(double price,
            double totalBSPLayLiability) {
        synchronized (runner) {
            getPrice(price).setTotalBSPLayLiability(totalBSPLayLiability);
        }
    }

    public RunnerPrice getPrice(double price) {
        synchronized (runner) {
            return prices[RunnerPrice.getIndex(price)];
        }
    }

    /**
     *
     * @param centre
     * @param offset must not be 0
     * @return
     */
    public Integer getPriceNoEmpties(int centre, int offset) {
        synchronized (runner) {
            int seen = 0;
            if (offset == 0) throw new IllegalArgumentException(
                    "offset is 0; naughty");
            else if(offset > 0) {
                for (int i = centre; i < prices.length; i++) {
                    if (prices[i].getAmount() > 0) {
                        seen++;
                        if (seen == offset)
                            return i;
                    }
                }
            }
            else {
                for (int i = centre - 1; i >= 0; i--) {
                    if (prices[i].getAmount() > 0) {
                        seen--;
                        if (seen == offset)
                            return i;
                    }
                }
            }
            return null;
        }
    }

    public RunnerPrice getLayPrice(int index) {
        synchronized (runner) {
            LOG.debug("layIndices.size(): " + layIndices.size());
            if (layIndices.size() > index)
                return prices[layIndices.get(index)];
            else
                return null;
        }
    }

    public RunnerPrice getBackPrice(int index) {
        synchronized (runner) {
            if (backIndices.size() > index)
                return prices[backIndices.get(index)];
            else
                return null;
        }
    }

}
