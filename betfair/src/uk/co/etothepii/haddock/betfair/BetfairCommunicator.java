/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.betfair;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import uk.co.etothepii.components.PasswordDialog;
import uk.co.etothepii.haddock.betfair.data.Funds;
import uk.co.etothepii.haddock.bettingconcepts.BetfairBet;
import uk.co.etothepii.haddock.bettingconcepts.BetfairBetMap;
import uk.co.etothepii.haddock.bettingconcepts.event.BetfairBetChangedEvent;
import uk.co.etothepii.haddock.bettingconcepts.event.BetfairBetChangedListener;
import uk.co.etothepii.haddock.bettingconcepts.event.NewBetfairBetEvent;
import uk.co.etothepii.haddock.bettingconcepts.event.NewBetfairBetListener;
import uk.co.etothepii.util.LoginData;
import uk.co.etothepii.util.changes.DifferenceChecker;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class BetfairCommunicator {

    private static final Logger LOG = Logger.getLogger(BetfairCommunicator.class);

    static {
        LOG.debug("BetfairCommunicator debugging");
    }

    public static final String FUNDS = "FUNDS";
    public static final String NEW_BETS = "NEW_BETS";
    public static final String UPDATE_BETS = "UPDATE_BETS";
    
    private LoginData loginData;
    private User user = null;
    private Funds funds = null;
    private final ArrayList<NewBetfairBetListener> newBetfairBetListeners;
    private final ArrayList<BetfairBetChangedListener> betfairBetChangedListeners;
    private final ArrayList<PropertyChangeListener> propertyChangeListeners;
    private final Map<Integer, Map<Integer, BetfairBetMap>> matchedBets;

    public BetfairCommunicator() {
        this(true);
    }

    public BetfairCommunicator(boolean gui) {
        newBetfairBetListeners = new ArrayList<NewBetfairBetListener>();
        betfairBetChangedListeners = new ArrayList<BetfairBetChangedListener>();
        propertyChangeListeners = new ArrayList<PropertyChangeListener>();
        matchedBets = new TreeMap<Integer, Map<Integer, BetfairBetMap>>();
        if (gui)
            loginData = PasswordDialog.getLoginData(null,
                    "Please enter your login details", "Betfair login");
        else {
            System.out.print("Username: ");
            String username = System.console().readLine();
            System.out.print("Password: ");
            char[] password = System.console().readPassword();
            loginData = new LoginData(username, password);
        }
        try {
            user = new User(loginData.getUsername(), loginData.getPassword());
        }
        catch (IllegalArgumentException iae) {
            IllegalArgumentException curr = iae;
            while (user == null) {
                loginData = PasswordDialog.getLoginData(null,
                        curr.getMessage(), "Betfair login");
                try {
                    user = new User(loginData.getUsername(),
                            loginData.getPassword());
                }
                catch (IllegalArgumentException iae2) {
                    curr = iae2;
                }
            }
        }
        updateFunds();
        updateMUBets(null);
    }

    public BetfairBetMap getMatchedBets(int marketId, int selectionId) {
        Map<Integer, BetfairBetMap> marketBets =
                matchedBets.get(marketId);
        if (marketBets == null) {
            marketBets = new TreeMap<Integer, BetfairBetMap>();
            matchedBets.put(marketId, marketBets);
        }
        BetfairBetMap selectionBets = marketBets.get(selectionId);
        if (selectionBets == null) {
            selectionBets = new BetfairBetMap();
            marketBets.put(selectionId, selectionBets);
        }
        return selectionBets;

    }

    private void updateFunds() {
        Funds oldFunds = funds;
        try {
            funds = user.getFunds();
            if (DifferenceChecker.different(oldFunds, funds)) {
                firePropertyChangeEvent(new PropertyChangeEvent(this, FUNDS,
                        oldFunds, funds));
            }
        }
        catch (BetfairAccessException bae) {
            LOG.error(bae.getMessage(), bae);
        }
    }

    public final void updateMUBets(Integer marketId) {
        LOG.debug("updating: " + (marketId == null ? "All" : marketId));
        ArrayList<BetfairBet> bets = user.getAllMatchedBetfairBets(null,
                marketId);
        Collections.sort(bets, new Comparator<BetfairBet>() {
            public int compare(BetfairBet o1, BetfairBet o2) {
                int ret = o1.getBfMarketId() - o2.getBfMarketId();
                if (ret != 0) return ret;
                ret = o1.getBfSelectionId() - o2.getBfSelectionId();
                if (ret != 0) return ret;
                ret = (int)(o1.getTransactionId() - o2.getTransactionId());
                return ret;
            }
        });
        while (!bets.isEmpty()) {
            int thisMarketId = bets.get(0).getBfMarketId();
            Map<Integer, BetfairBetMap> marketMatchedBets =
                    matchedBets.get(thisMarketId);
            if (marketMatchedBets == null)
                marketMatchedBets =
                        new TreeMap<Integer, BetfairBetMap>();
            ArrayList<BetfairBet> thisMarket = new ArrayList<BetfairBet>();
            while (bets.size() > 0 &&
                    bets.get(0).getBfMarketId() == thisMarketId)
                thisMarket.add(bets.remove(0));
            while(!thisMarket.isEmpty()) {
                int thisSelectionId = thisMarket.get(0).getBfSelectionId();
                BetfairBetMap selectionMatchedBets =
                        marketMatchedBets.get(thisSelectionId);
                if (selectionMatchedBets == null)
                    selectionMatchedBets = new BetfairBetMap();
                while (!thisMarket.isEmpty() &&
                        thisMarket.get(0).getBfSelectionId() == thisSelectionId) {
                    BetfairBet thisBet = thisMarket.remove(0);
                    BetfairBet replacing = selectionMatchedBets.get(
                            thisBet.getTransactionId());
                    if (replacing == null) {
                        selectionMatchedBets.put(
                                thisBet.getTransactionId(), thisBet);
                        fireNewBetfairBetEvent(
                                new NewBetfairBetEvent(this, thisBet));
                    }
                    else if (!replacing.equals(thisBet)) {
                        selectionMatchedBets.put(
                                thisBet.getTransactionId(), thisBet);
                        fireBetfairBetChangedEvent(new BetfairBetChangedEvent(
                                this, replacing, thisBet));
                    }
                }
            }
        }
    }

    public void addPropertyChangedListener(PropertyChangeListener changeListener) {
        synchronized (propertyChangeListeners) {
            propertyChangeListeners.add(changeListener);
        }
    }

    public void removePropertyChangedListener(PropertyChangeListener changeListener) {
        synchronized (propertyChangeListeners) {
            propertyChangeListeners.remove(changeListener);
        }
    }
    
    public void firePropertyChangeEvent(PropertyChangeEvent e) {
        synchronized (propertyChangeListeners) {
            for (PropertyChangeListener l : propertyChangeListeners) {
                l.propertyChange(e);
            }
        }
    }

    public void addNewBetfairBetListener(NewBetfairBetListener l) {
        synchronized (newBetfairBetListeners) {
            newBetfairBetListeners.add(l);
        }
    }

    public void removeNewBetfairBetListener(NewBetfairBetListener l) {
        synchronized (newBetfairBetListeners) {
            newBetfairBetListeners.remove(l);
        }
    }

    public void fireNewBetfairBetEvent(NewBetfairBetEvent e) {
        synchronized (newBetfairBetListeners) {
            for (NewBetfairBetListener l : newBetfairBetListeners) {
                l.newBetfairBet(e);
            }
        }
    }

    public void addBetfairBetChangedListener(BetfairBetChangedListener l) {
        synchronized (betfairBetChangedListeners) {
            betfairBetChangedListeners.add(l);
        }
    }

    public void removeBetfairBetChangedListener(BetfairBetChangedListener l) {
        synchronized (betfairBetChangedListeners) {
            betfairBetChangedListeners.remove(l);
        }
    }

    public void fireBetfairBetChangedEvent(BetfairBetChangedEvent e) {
        synchronized (betfairBetChangedListeners) {
            for (BetfairBetChangedListener l : betfairBetChangedListeners) {
                l.betfairBetChanged(e);
            }
        }
    }

    public Funds getFunds() {
        return funds;
    }

    public String getBetsChangeName(String field, int marketId, int selectionId) {
        StringBuilder sb = new StringBuilder(field);
        sb.append("~");
        sb.append(marketId);
        sb.append("~");
        sb.append(selectionId);
        return sb.toString();
    }

}
