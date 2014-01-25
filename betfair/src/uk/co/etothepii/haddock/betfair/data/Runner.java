/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.betfair.data;

import com.betfair.publicapi.types.exchange.v5.Bet;
import com.betfair.publicapi.types.exchange.v5.MultiWinnerOddsLine;
import com.betfair.publicapi.types.exchange.v5.PlaceBets;
import com.betfair.publicapi.types.exchange.v5.ProfitAndLoss;
import java.util.ArrayList;
import java.util.Comparator;
import org.apache.log4j.Logger;
import uk.co.etothepii.event.ChangeEvent;
import uk.co.etothepii.haddock.betfair.data.event.RunnerChangeListener;
import uk.co.etothepii.haddock.betfair.data.event.RunnerPriceChangeListener;
import uk.co.etothepii.util.Changes;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class Runner implements Comparable<Runner> {

    private static final Logger LOG = Logger.getLogger(Runner.class);
    private static final Logger LOG_SWITCH = Logger.getLogger(Runner.class.
            getName().concat(".switch"));
    private static final Logger LOG_PROCESS = Logger.getLogger(Runner.class.
            getName().concat(".process"));
    private static final Comparator<Runner> ORDER_INDEX_COMPARATOR =
            new Comparator<Runner>() {
        public int compare(Runner o1, Runner o2) {
            if (o1 == null && o2 == null) return 0;
            else if (o1 == null) return -1;
            else if (o2 == null) return 1;
            else return o1.orderIndex - o2.orderIndex;
        }
    };

    public static enum Field{ORDER_INDEX, TOTAL_AMOUNT_MATCHED,
            LAST_PRICE_MATCHED, HANDICAP, REDUCTION_FACTOR, VACANT,
            ASIAN_LINE_ID, FAR_SP_PRICE, NEAR_SP_PRICE, ACTUAL_SP_PRICE,
            TOTAL_BSP_BACK_MATCHED_AMOUNT, TOTAL_BSP_LIABILITY_MATCHED_AMOUNT,
            NAME, PROFIT_AND_LOSS
    };

    public final int selectionId;
    private String name;
    private ProfitAndLoss profitAndLoss;
    private Integer orderIndex;
    private Double totalAmountMatched;
    private Double lastPriceMatched;
    private Double handicap;
    private Double reductionFactor;
    private Boolean vacant;
    private Integer asianLineId;
    private Double farSPPrice;
    private Double nearSPPrice;
    private Double actualSPPrice;
    private Double totalBSPBackMatchedAmount;
    private Double totalBSPLiabilityMatchedAmount;
    private RunnerPrices runnerPrices;
    private boolean firing = false;


    private final ArrayList<RunnerChangeListener> runnerChangeListeners;
    private final ArrayList<RunnerPriceChangeListener>
            runnerPriceChangeListeners;

    private final boolean[] changed;

    private Exchange exchange;

    private Runner(int selectionId) {
        this(selectionId, 0, 0d, null, null, null, false, 0, null, null, null, 
                null, null);
    }

    private Runner(int selectionId, Integer orderIndex, Double totalAmountMatched,
            Double lastPriceMatched, Double handicap, Double reductionFactor,
            Boolean vacant, Integer asianLineId, Double farSPPrice,
            Double nearSPPrice, Double actualSPPrice,
            Double totalBSPBackMatchedAmount,
            Double totalBSPLiabilityMatchedAmount) {
        this.selectionId = selectionId;
        this.orderIndex = orderIndex;
        this.totalAmountMatched = totalAmountMatched;
        this.lastPriceMatched = lastPriceMatched;
        this.handicap = handicap;
        this.reductionFactor = reductionFactor;
        this.vacant = vacant;
        this.asianLineId = asianLineId;
        this.farSPPrice = farSPPrice;
        this.nearSPPrice = nearSPPrice;
        this.actualSPPrice = actualSPPrice;
        this.totalBSPBackMatchedAmount = totalBSPBackMatchedAmount;
        this.totalBSPLiabilityMatchedAmount = totalBSPLiabilityMatchedAmount;
        this.runnerPrices = new RunnerPrices(this);
        this.name = null;
        this.profitAndLoss = null;
        changed = new boolean[12];
        runnerChangeListeners = new ArrayList<RunnerChangeListener>();
        runnerPriceChangeListeners = new ArrayList<RunnerPriceChangeListener>();
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    private void setOrderIndex(Integer orderIndex) {
        if (Changes.different(orderIndex, this.orderIndex)) {
            Integer oldVal = this.orderIndex;
            Integer newVal = orderIndex;
            this.orderIndex = orderIndex;
            fireOrderIndexChanged(new ChangeEvent<Runner, Integer>(this, oldVal, newVal));
        }
    }

    public void emulate(PlaceBets p) {
        synchronized (this) {
            runnerPrices.emulate(p);
        }
    }

    @Override
    public Runner clone() {
        Runner runner = new Runner(selectionId, orderIndex, totalAmountMatched,
                lastPriceMatched, handicap, reductionFactor, vacant,
                asianLineId, farSPPrice, nearSPPrice, actualSPPrice,
                totalBSPBackMatchedAmount, totalBSPLiabilityMatchedAmount);
        runner.runnerPrices = runnerPrices.clone(runner);
        return runner;
    }



    public Double getTotalAmountMatched() {
        return totalAmountMatched;
    }

    private void setTotalAmountMatched(Double totalAmountMatched) {
        if (Changes.different(totalAmountMatched, this.totalAmountMatched)) {
            Double oldVal = this.totalAmountMatched;
            Double newVal = totalAmountMatched;
            this.totalAmountMatched = totalAmountMatched;
            fireTotalAmountMatchedChanged(new ChangeEvent<Runner, Double>(
                    this, oldVal, newVal));
        }
    }

    public Double getLastPriceMatched() {
        return lastPriceMatched;
    }

    private void setLastPriceMatched(Double lastPriceMatched) {
        if (Changes.different(lastPriceMatched, this.lastPriceMatched)) {
            Double oldVal = this.lastPriceMatched;
            Double newVal = lastPriceMatched;
            this.lastPriceMatched = lastPriceMatched;
            fireLastPriceMatchedChanged(new ChangeEvent<Runner, Double>(
                    this, oldVal, newVal));
        }
    }

    public Double getHandicap() {
        return handicap;
    }

    private void setHandicap(Double handicap) {
        if (Changes.different(handicap, this.handicap)) {
            Double oldVal = this.handicap;
            Double newVal = handicap;
            this.handicap = handicap;
            fireHandicapChanged(new ChangeEvent<Runner, Double>(
                    this, oldVal, newVal));
        }
    }

    void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public Double getReductionFactor() {
        return reductionFactor;
    }

    private void setReductionFactor(Double reductionFactor) {
        if (Changes.different(reductionFactor, this.reductionFactor)) {
            Double oldVal = this.reductionFactor;
            Double newVal = reductionFactor;
            this.reductionFactor = reductionFactor;
            fireReductionFactorChanged(new ChangeEvent<Runner, Double>(
                    this, oldVal, newVal));
        }
    }

    public Boolean isVacant() {
        return vacant;
    }

    private void setVacant(Boolean vacant) {
        if (Changes.different(vacant, this.vacant)) {
            Boolean oldVal = this.vacant;
            Boolean newVal = vacant;
            this.vacant = vacant;
            fireVacantChanged(new ChangeEvent<Runner, Boolean>(
                    this, oldVal, newVal));
        }
    }

    public Integer getAsianLineId() {
        return asianLineId;
    }

    private void setAsianLineId(Integer asianLineId) {
        if (Changes.different(asianLineId, this.asianLineId)) {
            Integer oldVal = this.asianLineId;
            Integer newVal = asianLineId;
            this.asianLineId = asianLineId;
            fireAsianLineIdChanged(new ChangeEvent<Runner, Integer>(
                    this, oldVal, newVal));
        }
    }

    public ProfitAndLoss getProfitAndLoss() {
        return profitAndLoss;
    }

    public Double getFarSPPrice() {
        return farSPPrice;
    }

    private void setFarSPPrice(Double farSPPrice) {
        if (Changes.different(farSPPrice, this.farSPPrice)) {
            Double oldVal = this.farSPPrice;
            Double newVal = farSPPrice;
            this.farSPPrice = farSPPrice;
            fireFarSPPriceChanged(new ChangeEvent<Runner, Double>(
                    this, oldVal, newVal));
        }
    }

    public Double getNearSPPrice() {
        return nearSPPrice;
    }

    private void setNearSPPrice(Double nearSPPrice) {
        if (Changes.different(nearSPPrice, this.nearSPPrice)) {
            Double oldVal = this.nearSPPrice;
            Double newVal = nearSPPrice;
            this.nearSPPrice = nearSPPrice;
            fireNearSPPriceChanged(new ChangeEvent<Runner, Double>(
                    this, oldVal, newVal));
        }
    }

    private void setName(String name) {
        if (Changes.different(name, this.name)) {
            String oldName = this.name;
            String newName = name;
            this.name = name;
            fireNameChanged(new ChangeEvent<Runner, String>(
                    this, oldName, newName));
        }
    }

    public String getName() {
        return name;
    }

    private void setProfitAndLoss(ProfitAndLoss profitAndLoss) {
        if (different(this.profitAndLoss, profitAndLoss)) {
            ProfitAndLoss oldProfitAndLoss = this.profitAndLoss;
            ProfitAndLoss newProfitAndLoss = profitAndLoss;
            this.profitAndLoss = profitAndLoss;
            fireProfitAndLossChanged(new ChangeEvent<Runner, ProfitAndLoss>(
                    this, oldProfitAndLoss, newProfitAndLoss));
        }
    }

    public Double getActualSPPrice() {
        return actualSPPrice;
    }

    private void setActualSPPrice(Double actualSPPrice) {
        if (Changes.different(actualSPPrice, this.actualSPPrice)) {
            Double oldVal = this.actualSPPrice;
            Double newVal = actualSPPrice;
            this.actualSPPrice = actualSPPrice;
            fireActualSPPriceChanged(new ChangeEvent<Runner, Double>(
                    this, oldVal, newVal));
        }
    }

    public RunnerPrices getRunnerPrices() {
        return runnerPrices;
    }

    private void setTotalBSPBackMatchedAmount(Double totalBSPBackMatchedAmount) {
        if (Changes.different(totalBSPBackMatchedAmount,
                this.totalBSPBackMatchedAmount)) {
            Double oldVal = this.totalBSPBackMatchedAmount;
            Double newVal = totalBSPBackMatchedAmount;
            this.totalBSPBackMatchedAmount = totalBSPBackMatchedAmount;
            fireTotalBSPBackMatchedAmountChanged(new ChangeEvent<Runner, Double>(
                    this, oldVal, newVal));
        }
    }

    public Double getTotalBSPBackMatchedAmount() {
        return totalBSPBackMatchedAmount;
    }

    private void setTotalBSPLiabilityMatchedAmount(
            Double totalBSPLiabilityMatchedAmount) {
        if (Changes.different(totalBSPLiabilityMatchedAmount,
                this.totalBSPLiabilityMatchedAmount)) {
            Double oldVal = this.totalBSPLiabilityMatchedAmount;
            Double newVal = totalBSPLiabilityMatchedAmount;
            this.totalBSPLiabilityMatchedAmount =
                    totalBSPLiabilityMatchedAmount;
            fireTotalBSPLiabilityMatchedAmount(new ChangeEvent<Runner, Double>(
                    this, oldVal, newVal));
        }
    }

    public Double getTotalBSPLiabilityMatchedAmount() {
        return totalBSPLiabilityMatchedAmount;
    }

    public void addRunnerChangeListener(RunnerChangeListener l) {
        synchronized (runnerChangeListeners) {
            runnerChangeListeners.add(l);
        }
    }
    
    public void removeRunnerChangeListener(RunnerChangeListener l) {
        synchronized (runnerChangeListeners) {
            runnerChangeListeners.remove(l);
        }
    }
    
    private void fireOrderIndexChanged(ChangeEvent<Runner, Integer> e) {
        synchronized (runnerChangeListeners) {
            for (RunnerChangeListener l : runnerChangeListeners)
                l.orderIndexChanged(e);
        }
    }

    private void fireTotalAmountMatchedChanged(ChangeEvent<Runner, Double> e) {
        synchronized (runnerChangeListeners) {
            for (RunnerChangeListener l : runnerChangeListeners)
                l.totalAmountMatchedChanged(e);
        }
    }

    private void fireLastPriceMatchedChanged(ChangeEvent<Runner, Double> e) {
        synchronized (runnerChangeListeners) {
            for (RunnerChangeListener l : runnerChangeListeners)
                l.lastPriceMatchedChanged(e);
        }
    }

    private void fireHandicapChanged(ChangeEvent<Runner, Double> e) {
        synchronized (runnerChangeListeners) {
            for (RunnerChangeListener l : runnerChangeListeners)
                l.handicapChanged(e);
        }
    }

    private void fireReductionFactorChanged(ChangeEvent<Runner, Double> e) {
        synchronized (runnerChangeListeners) {
            for (RunnerChangeListener l : runnerChangeListeners)
                l.reductionFactorChanged(e);
        }
    }

    private void fireVacantChanged(ChangeEvent<Runner, Boolean> e) {
        synchronized (runnerChangeListeners) {
            for (RunnerChangeListener l : runnerChangeListeners)
                l.vacantChanged(e);
        }
    }

    private void fireAsianLineIdChanged(ChangeEvent<Runner, Integer> e) {
        synchronized (runnerChangeListeners) {
            for (RunnerChangeListener l : runnerChangeListeners)
                l.asianLineIdChanged(e);
        }
    }

    private void fireFarSPPriceChanged(ChangeEvent<Runner, Double> e) {
        synchronized (runnerChangeListeners) {
            for (RunnerChangeListener l : runnerChangeListeners)
                l.farSPPriceChanged(e);
        }
    }

    private void fireNearSPPriceChanged(ChangeEvent<Runner, Double> e) {
        synchronized (runnerChangeListeners) {
            for (RunnerChangeListener l : runnerChangeListeners)
                l.nearSPPriceChanged(e);
        }
    }

    private void fireActualSPPriceChanged(ChangeEvent<Runner, Double> e) {
        synchronized (runnerChangeListeners) {
            for (RunnerChangeListener l : runnerChangeListeners)
                l.actualSPPriceChanged(e);
        }
    }

    private void fireTotalBSPBackMatchedAmountChanged(ChangeEvent<Runner, Double> e) {
        synchronized (runnerChangeListeners) {
            for (RunnerChangeListener l : runnerChangeListeners)
                l.totalBSPBackMatchedAmountChanged(e);
        }
    }

    private void fireTotalBSPLiabilityMatchedAmount(ChangeEvent<Runner, Double> e) {
        synchronized (runnerChangeListeners) {
            for (RunnerChangeListener l : runnerChangeListeners)
                l.totalBSPLiabilityMatchedAmount(e);
        }
    }

    private void fireNameChanged(ChangeEvent<Runner, String> e) {
        synchronized (runnerChangeListeners) {
            for (RunnerChangeListener l : runnerChangeListeners)
                l.nameChanged(e);
        }
    }

    private void fireProfitAndLossChanged(ChangeEvent<Runner, ProfitAndLoss> e) {
        synchronized (runnerChangeListeners) {
            for (RunnerChangeListener l : runnerChangeListeners)
                l.profitAndLossChanged(e);
        }
    }

    public void addRunnerPriceChangeListener(RunnerPriceChangeListener l) {
        synchronized (runnerPriceChangeListeners) {
            runnerPriceChangeListeners.add(l);
        }
    }

    public void removeRunnerPriceChangeListener(RunnerPriceChangeListener l) {
        synchronized (runnerPriceChangeListeners) {
            runnerPriceChangeListeners.remove(l);
        }
    }

    public void fireAmountChanged(ChangeEvent<RunnerPrice, Double> e) {
        synchronized (runnerPriceChangeListeners) {
            for (RunnerPriceChangeListener l : runnerPriceChangeListeners)
                l.amountChanged(e);
        }
    }

    public void fireDirectionChanged(ChangeEvent<RunnerPrice, BackLay> e) {
        synchronized (runnerPriceChangeListeners) {
            for (RunnerPriceChangeListener l : runnerPriceChangeListeners)
                l.directionChanged(e);
        }
    }

    public void fireTotalBSPLayLiabilityChanged(ChangeEvent<RunnerPrice, Double> e) {
        synchronized (runnerPriceChangeListeners) {
            for (RunnerPriceChangeListener l : runnerPriceChangeListeners)
                l.totalBSPLayLiabilityChanged(e);
        }
    }

    public void fireTotalBSPBackersStakeVolumeChanged(ChangeEvent<RunnerPrice, Double> e) {
        synchronized (runnerPriceChangeListeners) {
            for (RunnerPriceChangeListener l : runnerPriceChangeListeners)
                l.totalBSPBackersStakeVolumeChanged(e);
        }
    }

    public void fireMatchedChanged(ChangeEvent<RunnerPrice, Double> e) {
        synchronized (runnerPriceChangeListeners) {
            for (RunnerPriceChangeListener l : runnerPriceChangeListeners)
                l.matchedChanged(e);
        }
    }

    public void updateVolumes(String raw) {
        String[] split = raw.split("(?<!\\\\)\\|");
        for (int i = 1; i < split.length; i++) {
            String[] parts = split[i].split("~");
            if (parts.length == 2) {
                double price = Double.parseDouble(parts[0]);
                double matched = Double.parseDouble(parts[1]);
                runnerPrices.setMatched(price, matched);
            }
            else {
                throw new RuntimeException("raw: " + raw + "\nsplit[" + i + "]: " + split[i]);
            }
        }
    }

    public void updatePrices(String raw) {
        LOG.debug(raw);
        String[] parts = raw.split("\\|");
        updateData(parts[0]);
        if (parts.length > 1)
            updateJustPrices(parts[1]);
    }

    private void updateJustPrices(String raw) {
        String[] rawPrices = raw.split("~");
        LOG.debug("rawPrices.length: " + rawPrices.length);
        for (int i = 0; i < rawPrices.length; i += 5) {
            double price = Double.parseDouble(rawPrices[i]);
            double back = Double.parseDouble(rawPrices[i + 1]);
            double lay = Double.parseDouble(rawPrices[i + 2]);
            double totalBSPLayLiability = Double.parseDouble(rawPrices[i + 3]);
            double totalBSPBackersStakeVolume =
                    Double.parseDouble(rawPrices[i + 4]);
//            RunnerPrice rp = runnerPrices.getPrice(price);
            LOG.debug("back: " + back);
            LOG.debug("lay: " + lay);
            if (back >  0)
                getRunnerPrices().setBack(price, back);
            else
                getRunnerPrices().setLay(price, lay);
            getRunnerPrices().setTotalBSPBackersStakeVolume(price,
                    totalBSPBackersStakeVolume);
            getRunnerPrices().setTotalBSPLayLiability(price,
                    totalBSPLayLiability);
        }
    }

    public void updateProfitAndLoss(ProfitAndLoss profitAndLoss) {
        setName(profitAndLoss.getSelectionName());
        setProfitAndLoss(profitAndLoss);
    }

    private boolean updateData(String raw) {
        LOG_PROCESS.debug("raw: " + raw);
        String[] header = raw.split("~");
        int newSelectionId = Integer.parseInt(header[0]);
        if (this.selectionId != newSelectionId) {
            LOG.debug("Selection Ids do not match");
            return false;
        }
        LOG_PROCESS.debug("Selection Ids match");
        int newOrderIndex = Integer.parseInt(header[1]);
        setOrderIndex(newOrderIndex);
        double newTotalAmountMatched = Double.parseDouble(header[2]);
        setTotalAmountMatched(newTotalAmountMatched);
        LOG_PROCESS.debug("newTotalAmountMatched: " + newTotalAmountMatched);
        LOG_PROCESS.debug("totalAmountMatched: " + totalAmountMatched);
        Double newLastPriceMatched = null;
        try {newLastPriceMatched = Double.parseDouble(header[3]);}
        catch (NumberFormatException nfe) {
            if (!nfe.getMessage().equals("empty String"))
                LOG.error(nfe.getMessage(), nfe);
        }
        catch (ArrayIndexOutOfBoundsException aioobe) {}
        catch (Throwable t) {
            LOG.error(t.getMessage(), t);
        }
        setLastPriceMatched(newLastPriceMatched);
        Double newHandicap = null;
        try {newHandicap = Double.parseDouble(header[4]);}
        catch (NumberFormatException nfe) {
            if (!nfe.getMessage().equals("empty String"))
                LOG.error(nfe.getMessage(), nfe);
        }
        catch (ArrayIndexOutOfBoundsException aioobe) {}
        catch (Throwable t) {LOG.error(t.getMessage(), t);}
        setHandicap(newHandicap);
        Double newReductionFactor = null;
        try {newReductionFactor = Double.parseDouble(header[5]);}
        catch (NumberFormatException nfe) {
            if (!nfe.getMessage().equals("empty String"))
                LOG.error(nfe.getMessage(), nfe);
        }
        catch (ArrayIndexOutOfBoundsException aioobe) {}
        catch (Throwable t) {LOG.error(t.getMessage(), t);}
        setReductionFactor(newReductionFactor);
        boolean newVacant = Boolean.parseBoolean(header[6]);
        setVacant(newVacant);
        int newAsianLineId = Integer.parseInt(header[7]);
        setAsianLineId(newAsianLineId);
        Double newFarSPPrice = null;
        try {newFarSPPrice = Double.parseDouble(header[8]);}
        catch (NumberFormatException nfe) {
            if (!nfe.getMessage().equals("empty String"))
                LOG.error(nfe.getMessage(), nfe);
        }
        catch (ArrayIndexOutOfBoundsException aioobe) {}
        catch (Throwable t) {LOG.error(t.getMessage(), t);}
        setFarSPPrice(newFarSPPrice);
        Double newNearSPPrice = null;
        try {newNearSPPrice = Double.parseDouble(header[9]);}
        catch (NumberFormatException nfe) {
            if (!nfe.getMessage().equals("empty String"))
                LOG.error(nfe.getMessage(), nfe);
        }
        catch (ArrayIndexOutOfBoundsException aioobe) {}
        catch (Throwable t) {LOG.error(t.getMessage(), t);}
        setNearSPPrice(newNearSPPrice);
        Double newActualSPPrice = null;
        try {newActualSPPrice = Double.parseDouble(header[10]);}
        catch (NumberFormatException nfe) {
            if (!nfe.getMessage().equals("empty String"))
                LOG.error(nfe.getMessage(), nfe);
        }
        catch (ArrayIndexOutOfBoundsException aioobe) {}
        catch (Throwable t) {LOG.error(t.getMessage(), t);}
        setActualSPPrice(newActualSPPrice);
        return true;
    }

    private void setFiring(boolean firing) {
        this.firing = firing;
    }

    public static final int AMOUNT_TO_LAY = 0;
    public static final int AVERAGE_PRICE = 1;
    public static final int MAX_PRICE = 2;

    public synchronized double[] getLayForBack(double price, double amount,
            double commission) {
        double amountToLay = 0;
        double avgPrice = 0;
        double maxPrice = Double.MAX_VALUE;
        ArrayList<BetSize> bets = new ArrayList<BetSize>();
        RunnerPrice rp;
        for (int i = 0; (rp = runnerPrices.getLayPrice(i)) != null
                && amount > 0; i++) {
            maxPrice = rp.price;
            LOG.debug("maxPrice: " + maxPrice);
            double backsOff = (rp.price - commission) / price * rp.getAmount();
            LOG.debug("backsOff: " + backsOff);
            double toUse;
            if (backsOff > amount) {
                double ratio = amount / backsOff;
                toUse = rp.getAmount() * ratio;
                amount -= ratio * backsOff;
            }
            else {
                toUse = rp.getAmount();
                amount -= backsOff;
            }
            LOG.debug("toUse: " + toUse);
            bets.add(new BetSize(rp.price, toUse));
        }
        for (BetSize bs : bets) {
            amountToLay += bs.stake;
            avgPrice += (bs.price) * bs.stake;
        }
        avgPrice /= amountToLay;
        return new double[] {amountToLay, avgPrice, maxPrice};
    }

    public static Runner process(String raw) {
        LOG.debug("raw: " + raw);
        String[] parts = raw.split("\\|");
        LOG.debug("parts[0]: " + parts[0]);
        String[] header = parts[0].split("~", 2);
        LOG.debug("parts[1]: " + parts[1]);
        int selectionId = Integer.parseInt(header[0]);
        Runner toRet = new Runner(selectionId);
        toRet.updateData(parts[0]);
        LOG_PROCESS.debug("toRet.totalAmountMatched: " + toRet.totalAmountMatched);
        toRet.updateJustPrices(parts[1]);
        toRet.setFiring(true);
        return toRet;
    }

    public static int getSelectionId(String raw) {
        LOG.debug("raw: " + raw);
        String[] header = raw.split("~", 2);
        return Integer.parseInt(header[0]);
    }

    private class BetSize {

        public final double price;
        public final double stake;

        public BetSize(double price, double stake) {
            this.price = price;
            this.stake = stake;
        }
    }

    public static boolean different(ProfitAndLoss a, ProfitAndLoss b) {
        if (a == null && b == null) return false;
        else if(a == null ^ b == null) return true;
        if (a instanceof MultiWinnerOddsLine ^ b instanceof MultiWinnerOddsLine)
            return true;
        if (a.getFutureIfWin() != b.getFutureIfWin()) return true;
        if (a.getIfWin() != b.getIfWin()) return true;
        if (a.getWorstcaseIfWin() != b.getWorstcaseIfWin()) return true;
        if (a instanceof MultiWinnerOddsLine &&
                b instanceof MultiWinnerOddsLine) {
            MultiWinnerOddsLine A = (MultiWinnerOddsLine)a;
            MultiWinnerOddsLine B = (MultiWinnerOddsLine)b;
            if (A.getIfLoss() != B.getIfLoss()) return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Runner)) {
            return selectionId == ((Runner)obj).selectionId;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.selectionId;
        return hash;
    }

    public int compareTo(Runner o) {
        if (this.equals(o)) return 0;
        return selectionId - o.selectionId;
    }

}
