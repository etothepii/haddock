/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.betfair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Semaphore;
import org.apache.log4j.Logger;

/**
 * This class allows for the storing of how often one has called various Betfair
 * API calls and the establishment of whether or not its OK to send another call
 * without encurring a charge or getting a null return.
 *
 * @author James Robinson on behalf Ridgefield-Pennine Ltd
 */
public class BetfairAPITimeConstraints {
	public static final Logger LOG = Logger.getLogger(BetfairAPITimeConstraints.class);
    private static Logger SLEEP_LOG = Logger.getLogger(
            BetfairAPITimeConstraints.class.getName().concat(".sleep"));
	public static final Logger GET_MARKET_LOG =
            Logger.getLogger("betfairbot.BetfairAPITimeConstraints.getMarket");

    public static final long DEFAULT_MILLISECONDS_PER_SET_OF_CALLS = 60000l;

    private final int calls;

    private final long millisecondsPerSetOfCalls;

    private boolean unrestricted;

    private final ArrayList<Calendar> callsMadeAt;

    private final Semaphore locker = new Semaphore(1, false);

    private final String name;

    private BetfairAPITimeConstraints(String name) {
        this.name = name;
        this.unrestricted = false;
        millisecondsPerSetOfCalls = 1;
        calls = 1000;
        callsMadeAt = new ArrayList<Calendar>();
    }

    public BetfairAPITimeConstraints(String name, int calls) {
        this(name, calls, DEFAULT_MILLISECONDS_PER_SET_OF_CALLS);
    }

    public BetfairAPITimeConstraints(String name, int calls,
            long millisecondsPerSetOfCalls) {
		this.name = name;
        this.calls = calls;
        this.millisecondsPerSetOfCalls = millisecondsPerSetOfCalls;
        unrestricted = false;
        callsMadeAt = new ArrayList<Calendar>();
    }

    public void makeCall() {
        callsMadeAt.add(Calendar.getInstance());
        if (LOG.isDebugEnabled()){
                LOG.debug("Releaseing lock "+name);
        }
        locker.release();
    }

    public void awaitResourceTime() {
        try {
                if (LOG.isDebugEnabled()){
                        LOG.debug("Aquiring lock for "+name);
                }
                locker.acquire();
                if (LOG.isDebugEnabled()){
                        LOG.debug("got lock for "+name);
                }
        } catch (InterruptedException ex) {
                LOG.warn("Interrupted aquiring lock "+ex.getMessage(), ex);
        }
        if (unrestricted) return;
        long time = System.currentTimeMillis();
        for (int i = callsMadeAt.size() - 1; i >= 0; i--) {
            if (callsMadeAt.get(0).getTimeInMillis() +
                    millisecondsPerSetOfCalls < time) {
                callsMadeAt.remove(0);
            }
        }
        if (callsMadeAt.size() < calls) return;
        else {
            long t =
                callsMadeAt.get(callsMadeAt.size() - calls).getTimeInMillis() +
                millisecondsPerSetOfCalls - time + 1;
            if (t < 1L)
                t = 1L;
            try {
                if (SLEEP_LOG.isDebugEnabled()) {
                        SLEEP_LOG.debug(
                    name + " sleeping for " + t + " milliseconds");
                }
                Thread.sleep(t);
            }
            catch (InterruptedException ex) {
                LOG.warn("Sleep interrpted "+ex.getMessage(), ex);
            }
        }
    }

    public static BetfairAPITimeConstraints getUnrestriced(String name) {
        BetfairAPITimeConstraints toRet = new BetfairAPITimeConstraints(name);
        toRet.unrestricted = true;
        return toRet;
    }
}
