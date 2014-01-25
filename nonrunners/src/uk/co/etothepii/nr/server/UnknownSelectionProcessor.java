/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.nr.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;
import uk.co.etothepii.haddock.db.factories.AliasFactory;
import uk.co.etothepii.haddock.db.factories.RaceFactory;
import uk.co.etothepii.haddock.db.factories.SelectionFactory;
import uk.co.etothepii.haddock.db.tables.Alias;
import uk.co.etothepii.haddock.db.tables.Race;
import uk.co.etothepii.haddock.db.tables.Selection;
import uk.co.etothepii.haddock.db.tables.enumerations.Vendor;
import uk.co.etothepii.util.ThreadController;
import uk.co.etothepii.util.ThreadControllerStopException;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class UnknownSelectionProcessor {

    private enum RetryStatus{RETRY, RACE_RAN, FOUND, FAILED};

    private static final Logger LOG =
            Logger.getLogger(UnknownSelectionProcessor.class);

    private final LinkedBlockingQueue<UnknownSelection> unknownAliases;
    private final ArrayList<UnknownSelection>  reload;
    private final ThreadController controller;

    public UnknownSelectionProcessor() {
        unknownAliases = new LinkedBlockingQueue<UnknownSelection>();
        reload = new ArrayList<UnknownSelection>();
        controller = new ThreadController(new Runnable() {
            public void run() {
                try {
                    if (!unknownAliases.isEmpty() || reload.isEmpty()) {
                        LOG.debug("waiting");
                        UnknownSelection a = unknownAliases.take();
                        LOG.debug("taken");
                        switch (processLocal(a)) {
                            case RETRY:
                                synchronized (reload) {
                                    reload.add(a);
                                }
                                break;
                            case RACE_RAN: raceRan(a);
                        }
                        LOG.debug("processed loop");
                    }
                    else {
                        LOG.debug("sleeping");
                        final ArrayList<UnknownSelection> thisReload = new ArrayList<UnknownSelection>();
                        synchronized (reload) {
                            while (!reload.isEmpty())
                                thisReload.add(reload.remove(0));
                        }
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    Thread.sleep(60000L * 12);
                                    while (!thisReload.isEmpty()) {
                                        unknownAliases.offer(thisReload.remove(0));
                                    }
                                }
                                catch (InterruptedException ie) {}
                            }
                        }).start();

                    }
                }
                catch (InterruptedException ie) {
                    throw new ThreadControllerStopException();
                }
            }
        });
        start();
    }

    boolean process(UnknownSelection a) {
        LOG.debug("begun processing");
        switch (processLocal(a)) {
            case FOUND:
                return true;
            case RACE_RAN:
                raceRan(a);
                return false;
            case RETRY:
                unknownAliases.offer(a);
            case FAILED:
            default:
                return false;
        }
    }

    private RetryStatus processLocal(UnknownSelection a) {
        LOG.debug("begun local processing");
        if (a.getRace().getMarket() == null)
            RaceFactory.getFactory().reload(a.getRace());
        LOG.debug("Checked for null and reloaded");
        if (a.getRace().getMarket() == null &&
                a.getRace().getScheduled().getTime() >
                System.currentTimeMillis())
            return RetryStatus.RETRY;
        else if (a.getRace().getMarket() == null) return RetryStatus.RACE_RAN;
        LOG.debug("Checked for null and scheduled");
        try {
            ArrayList<Alias> betfairNames = AliasFactory.getFactory(
                    ).getAliasesAttachedToMarket(a.getRace().getMarket(),
                    Vendor.BETFAIR_NAME, false);
            LOG.debug("got betfair selections for market");
            Alias bestMatch = Alias.getBestMatch(betfairNames, a.getAlais());
            LOG.debug("got best match");
            if (bestMatch == null)
                return RetryStatus.FAILED;
            a.getAlais().setSelection(bestMatch.getSelection());
            AliasFactory.getFactory().save(a.getAlais());
            LOG.debug("found selection");
            return RetryStatus.FOUND;
        }
        catch (SQLException sqle) {
            LOG.error(sqle.getMessage(), sqle);
            return RetryStatus.FAILED;
        }
    }

    void raceRan(UnknownSelection a) {

    }

    public final void start() {
        controller.start();
    }

    public void stop() {
        controller.stop();
    }

}

class UnknownSelection {

    private final Alias a;
    private final Race r;

    public UnknownSelection(Alias a, Race r) {
        this.a = a;
        this.r = r;
    }

    public Alias getAlais() {
        return a;
    }

    public Race getRace() {
        return r;
    }

}

