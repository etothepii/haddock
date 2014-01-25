/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.betfair.data;

import com.betfair.publicapi.types.exchange.v5.GetMarketProfitAndLossErrorEnum;
import com.betfair.publicapi.types.exchange.v5.GetMarketProfitAndLossResp;
import com.betfair.publicapi.types.exchange.v5.PlaceBets;
import com.betfair.publicapi.types.exchange.v5.ProfitAndLoss;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import uk.co.etothepii.event.ChangeEvent;
import uk.co.etothepii.haddock.betfair.data.event.ExchangeChangeListener;
import uk.co.etothepii.haddock.betfair.data.event.RunnerChangeListener;
import uk.co.etothepii.haddock.betfair.data.event.RunnerPriceChangeListener;
import uk.co.etothepii.haddock.bettingconcepts.BetType;
import uk.co.etothepii.haddock.bettingconcepts.BetfairBet;
import uk.co.etothepii.haddock.bettingconcepts.BetfairPrice;
import uk.co.etothepii.haddock.db.tables.enumerations.MarketStatus;
import uk.co.etothepii.util.Changes;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class Exchange {

    private static final Logger LOG = Logger.getLogger(Exchange.class);

    public final int marketId;
    private int inPlayDelay;
    private final ArrayList<Runner> runners;
    private final TreeMap<Integer, Runner> runnerMap;
    private final ArrayList<RemovedRunner> removedRunners;
    private final ArrayList<ExchangeChangeListener> exchangeChangeListeners;
    public final Object sync = new Object();

    private Exchange(int marketId) {
        this(marketId, 0, new ArrayList<Runner>(),
                new ArrayList<RemovedRunner>());
    }

    private Exchange(int marketId, int inPlayDelay, ArrayList<Runner> runners,
            ArrayList<RemovedRunner> removedRunners) {
        this.marketId = marketId;
        this.inPlayDelay = inPlayDelay;
        this.runners = runners;
        this.removedRunners = removedRunners;
        runnerMap = new TreeMap<Integer, Runner>();
        for (Runner r : runners)
            runnerMap.put(r.selectionId, r);
        exchangeChangeListeners = new ArrayList<ExchangeChangeListener>();
        Collections.sort(runners, new Comparator<Runner>() {
            public int compare(Runner o1, Runner o2) {
                return o1.getOrderIndex() - o2.getOrderIndex();
            }

        });

    }

    public void setInPlayDelay(int inPlayDelay) {
        if (Changes.different(this.inPlayDelay, inPlayDelay)) {
            int oldVal = this.inPlayDelay;
            int newVal = inPlayDelay;
            this.inPlayDelay = inPlayDelay;
            fireInPlayDelayChanged(
                    new ChangeEvent<Exchange, Integer>(this, oldVal, newVal));
        }
    }

    public int getInPlayDelay() {
        return inPlayDelay;
    }

    public void removeRunner(Runner runner) {
        if (runners.contains(runner)) {
            if (runners.remove(runner)) {
                fireRunnerRemoved(
                        new ChangeEvent<Exchange, Runner>(this, runner, null));
            }
        }
    }

    public void addRemovedRunner(RemovedRunner removedRunner) {
        if (!removedRunners.contains(removedRunner)) {
            if (removedRunners.add(removedRunner)) {
                fireRemovedRunnerAdded(
                        new ChangeEvent<Exchange, RemovedRunner>(
                            this, null, removedRunner));
            }
        }
    }

    public void addRunnerChangeListener(RunnerChangeListener l) {
        for (Runner r : runners) {
            r.addRunnerChangeListener(l);
        }
    }

    public void addRunnerPriceChangeListener(RunnerPriceChangeListener l) {
        for (Runner r : runners) {
            r.addRunnerPriceChangeListener(l);
        }
    }

    public void removeRunnerChangeListener(RunnerChangeListener l) {
        for (Runner r : runners) {
            r.removeRunnerChangeListener(l);
        }
    }

    public void removeRunnerPriceChangeListener(RunnerPriceChangeListener l) {
        for (Runner r : runners) {
            r.removeRunnerPriceChangeListener(l);
        }
    }

    public void addExchangeChangeListener(
            ExchangeChangeListener l) {
        synchronized (exchangeChangeListeners) {
            exchangeChangeListeners.add(l);
        }
    }

    public void removeExchangeChangeListener(
            ExchangeChangeListener l) {
        synchronized (exchangeChangeListeners) {
            exchangeChangeListeners.remove(l);
        }
    }

    public void fireInPlayDelayChanged(ChangeEvent<Exchange, Integer> e) {
        synchronized (exchangeChangeListeners) {
            for (ExchangeChangeListener l : exchangeChangeListeners) {
                l.inPlayDelayChanged(e);
            }
        }
    }

    public void fireRunnerRemoved(ChangeEvent<Exchange, Runner> e) {
        synchronized (exchangeChangeListeners) {
            for (ExchangeChangeListener l : exchangeChangeListeners) {
                l.runnerRemoved(e);
            }
        }
    }

    public void fireRemovedRunnerAdded(ChangeEvent<Exchange, RemovedRunner> e) {
        synchronized (exchangeChangeListeners) {
            for (ExchangeChangeListener l : exchangeChangeListeners) {
                l.removedRunnerAdded(e);
            }
        }
    }

    public void updateVolumes(String raw) {
        LOG.debug("updateVolumes: " + raw);
        if (raw == null) return;
        String[] parts = raw.split("(?<!\\\\):");
        ArrayList<Runner> copy = new ArrayList<Runner>(runners);
        for (int i = 1; i < parts.length; i++) {
            int selectionId = Runner.getSelectionId(parts[i]);
            for (int j = 0; j < copy.size(); j++) {
                Runner r = copy.get(j);
                if (r.selectionId == selectionId) {
                    r.updateVolumes(parts[i]);
                    copy.remove(j);
                    break;
                }
            }
        }
    }

    public void updatePrices(String raw) {
        synchronized (sync) {
            if (raw == null) return;
            String[] parts = raw.split(":");
            String[] header = parts[0].split("~");
            int newMarketId = Integer.parseInt(header[0]);
            if (marketId != newMarketId) return;
            int newInPlayDelay = Integer.parseInt(header[1]);
            setInPlayDelay(newInPlayDelay);
            if (header.length > 2) {
                String[] split = header[2].split(";");
                for (int i = 0; i < split.length; i++)
                    addRemovedRunner(RemovedRunner.process(split[i]));
            }
            ArrayList<Runner> copy = new ArrayList<Runner>(runners);
            for (int i = 1; i < parts.length; i++) {
                int selectionId = Runner.getSelectionId(parts[i]);
                for (int j = 0; j < copy.size(); j++) {
                    Runner r = copy.get(j);
                    if (r.selectionId == selectionId) {
                        r.updatePrices(parts[i]);
                        copy.remove(j);
                        break;
                    }
                }
            }
            for (int i = 0; i < copy.size(); i++) {
                removeRunner(copy.get(i));
            }
        }
    }

    public void updateProfitAndLoss(GetMarketProfitAndLossResp
            getMarketProfitAndLossResp) {
        if (getMarketProfitAndLossResp.getErrorCode() ==
                GetMarketProfitAndLossErrorEnum.OK) {
            ArrayList<Runner> copy = new ArrayList<Runner>(runners);
            for (ProfitAndLoss profitAndLoss :
                    getMarketProfitAndLossResp.getAnnotations().getProfitAndLoss()) {
                int selectionId = profitAndLoss.getSelectionId();
                for (int j = 0; j < copy.size(); j++) {
                    Runner r = copy.get(j);
                    if (r.selectionId == selectionId) {
                        r.updateProfitAndLoss(profitAndLoss);
                        copy.remove(j);
                        break;
                    }
                }
            }
        }
    }

    public void emulate(PlaceBets p) {
        synchronized (sync) {
            for(Runner r : runners) {
                if (p.getSelectionId() == r.selectionId) {
                    r.emulate(p);
                    return;
                }
            }
        }
    }

    public static Exchange process(String raw) {
        if (raw == null) return new Exchange(0);
        LOG.debug(raw);
        String[] parts = raw.split("(?<!\\\\):");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].replaceAll("\\:", ":");
        }
        String[] header = parts[0].split("~");
        ArrayList<Runner> runners = new ArrayList<Runner>(parts.length - 1);
        ArrayList<RemovedRunner> removedRunners;
        if (header.length > 7 && header[9].length() > 0) {
            try {
                String[] split = header[9].split(";");
                removedRunners = new ArrayList<RemovedRunner>(split.length);
                for (int i = 0; i < split.length; i++)
                    removedRunners.add(RemovedRunner.process(split[i]));
            }
            catch (RuntimeException iae) {
                LOG.error("header[9]: " + header[9]);
                LOG.error("header[]: " + parts[0]);
                LOG.error("raw: " + raw);
                throw(iae);
            }
        }
        else
            removedRunners = new ArrayList<RemovedRunner>();
        int marketId = Integer.parseInt(header[0]);
        String currency = header[1];
        MarketStatus marketStatus = MarketStatus.valueOf(header[2]);
        int inPlayDelay = Integer.parseInt(header[3]);
        int winners = Integer.parseInt(header[4]);
        String marketInfo = header[5];
        boolean discountAllowed = Boolean.parseBoolean(header[6]);
        double baseRate = Double.parseDouble(header[7]);
        long refereshTime = Long.parseLong(header[8]);
        boolean bsp = header[10].equals("Y");
        LOG.debug("parts.length: " + parts.length);
        for (int i = 1; i < parts.length; i++) {
            Runner runner = Runner.process(parts[i]);
            LOG.debug("runner.getLastPriceMatched(): " +
                    runner.getLastPriceMatched());
            runners.add(runner);
        }
        Exchange e =
                new Exchange(marketId, inPlayDelay, runners, removedRunners);
        for (Runner r : runners)
            r.setExchange(e);
        return e;
    }

    public static Exchange processCompleteMarketPricesCompressed(String raw) {
        if (raw == null) return new Exchange(0);
        LOG.debug(raw);
        String[] parts = raw.split("(?<!\\\\):");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].replaceAll("\\:", ":");
        }
        String[] header = parts[0].split("~");
        ArrayList<Runner> runners = new ArrayList<Runner>(parts.length - 1);
        ArrayList<RemovedRunner> removedRunners;
        if (header.length > 2 && header[3].length() > 0) {
            try {
                String[] split = header[3].split(";");
                removedRunners = new ArrayList<RemovedRunner>(split.length);
                for (int i = 0; i < split.length; i++)
                    removedRunners.add(RemovedRunner.process(split[i]));
            }
            catch (RuntimeException iae) {
                LOG.error("header[3]: " + header[3]);
                LOG.error("header[]: " + parts[0]);
                LOG.error("raw: " + raw);
                throw(iae);
            }
        }
        else
            removedRunners = new ArrayList<RemovedRunner>();
        int marketId = Integer.parseInt(header[0]);
        int inPlayDelay = Integer.parseInt(header[1]);
        LOG.debug("parts.length: " + parts.length);
        for (int i = 1; i < parts.length; i++) {
            Runner runner = Runner.process(parts[i]);
            LOG.debug("runner.getLastPriceMatched(): " +
                    runner.getLastPriceMatched());
            runners.add(runner);
        }
        Exchange e =
                new Exchange(marketId, inPlayDelay, runners, removedRunners);
        for (Runner r : runners)
            r.setExchange(e);
        return e;
    }

    @Override
    public Exchange clone() {
        ArrayList<Runner> runners = new ArrayList<Runner>();
        ArrayList<RemovedRunner> removedRunners = new ArrayList<RemovedRunner>();
        for (Runner r : this.runners)
            runners.add(r.clone());
        for (RemovedRunner r : this.removedRunners)
            removedRunners.add(r.clone());
        return new Exchange(marketId, inPlayDelay, runners, removedRunners);
    }

    public Map<Runner, ArrayList<BetfairBet>> getArbBets() {
        Exchange ex = clone();
        Map<Runner, ArrayList<BetfairBet>> map = 
                ex.getArbBets(new HashMap<Runner, ArrayList<BetfairBet>>());
        HashMap<Runner, ArrayList<BetfairBet>> toRet =
                new HashMap<Runner, ArrayList<BetfairBet>>();
        for (Map.Entry<Runner, ArrayList<BetfairBet>> e : map.entrySet()) {
            Map<BetfairPrice, Double> bets = new HashMap<BetfairPrice, Double>();
            for (BetfairBet b : e.getValue()) {
                Double d = bets.get(b.getPrice());
                bets.put(b.getPrice(), b.getAmount() + (d == null ? 0 : d));
            }
            ArrayList<BetfairBet> bets2 = new ArrayList<BetfairBet>();
            for (Map.Entry<BetfairPrice, Double> en : bets.entrySet()) {
                bets2.add(new BetfairBet(en.getValue(), en.getKey(), BetType.LAY));
            }
            toRet.put(e.getKey(), bets2);
        }
        return toRet;
    }

    public Map<Runner, ArrayList<BetfairBet>> getArbBets(
            Map<Runner, ArrayList<BetfairBet>> soFar) {
        double tp = 0;
        double min = Double.MAX_VALUE;
        for (Runner r : getRunners()) {
            RunnerPrice rp = r.getRunnerPrices().getLayPrice(0);
            if (rp != null) {
                tp += 1d / rp.price;
                double avail = rp.price * rp.getAmount();
                if (avail < min) min = avail;
            }
        }
        if (tp > 1) {
            for (Runner r : runners) {
                RunnerPrice rp = r.getRunnerPrices().getLayPrice(0);
                if (rp == null)
                    continue;
                double stake = min / rp.price;
                ArrayList<BetfairBet> bets = soFar.get(r);
                if (bets == null) {
                    bets = new ArrayList<BetfairBet>();
                    soFar.put(r, bets);
                }
                bets.add(new BetfairBet(stake,
                        BetfairPrice.getPrice(rp.price), BetType.LAY));
                double residue = rp.getAmount() - stake;
                if (residue > 0.01)
                    r.getRunnerPrices().setLay(rp.price, residue);
                else {
                    r.getRunnerPrices().setLay(rp.price, 0d);

                }
            }
            return getArbBets(soFar);
        }
        else
            return soFar;
    }

    public ArrayList<RemovedRunner> getRemovedRunners() {
        return removedRunners;
    }

    public ArrayList<Runner> getRunners() {
        return runners;
    }

    public Runner getRunner(int selectionId) {
        return runnerMap.get(selectionId);
    }

}
