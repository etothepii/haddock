/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.timeform;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.MasterController;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class HorseCleaner {

    private final DatabaseConduit conduit;
    private final PreparedStatement getHorsesQry;
    private final PreparedStatement aliasesQry;
    private final PreparedStatement insertQry;
    private final PreparedStatement updateQry;
    private final BlockingQueue<Horse> horses;
    private final BlockingQueue<Alias> aliases;
    private final BlockingQueue<Alias> savedAliases;
    private final ArrayList<Thread> threads;
    private final ArrayList<Alias> theseSavedAliases;
    private final ArrayList<Alias> theseAliases;
    private boolean running = false;

    private static final Logger LOG_COUNT = Logger.getLogger(
            HorseCleaner.class.getName().concat(".count"));
    private static final Logger LOG = Logger.getLogger(HorseCleaner.class);

    public HorseCleaner() throws SQLException {
        theseSavedAliases = new ArrayList<Alias>();
        theseAliases = new  ArrayList<Alias>();
        threads = new ArrayList<Thread>();
        horses = new LinkedBlockingQueue<Horse>();
        aliases = new LinkedBlockingQueue<Alias>();
        savedAliases = new LinkedBlockingQueue<Alias>();
        conduit = MasterController.takeConduit();
        getHorsesQry = conduit.prepareStatement(
                "SELECT * FROM `horses` `h` WHERE `h`.`alias` IS NULL");
        aliasesQry = conduit.prepareStatement(
                "SELECT * FROM `aliases` WHERE `alias` = ? AND `vendor`"
                + " = 'BETFAIR_NAME' AND `selection` IS NOT NULL");
        insertQry = conduit.prepareStatement("INSERT "
                + "INTO `aliases` (`id`, `selection`, `alias`, "
                + "`vendor`) VALUES (NULL, ?, ?, 'TIMEFORM')",
                PreparedStatement.RETURN_GENERATED_KEYS);
        updateQry = conduit.prepareStatement("UPDATE "
                + "`horses` SET `alias` = ? WHERE `id` = ?");
        getNewHorses();
        startAliasesQry();
        startInsertQry();
        startUpdateQry();
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    private boolean theseSavedAliasesEmpty() {
        synchronized (theseSavedAliases) {
            return theseSavedAliases.isEmpty();
        }
    }

    private boolean theseAliasesEmpty() {
        synchronized (theseAliases) {
            return theseAliases.isEmpty();
        }
    }

    public synchronized void stop() {
        System.out.println("Stopping");
        while (!threads.isEmpty())
            threads.remove(0).interrupt();
        try {
            updateQry.close();
            insertQry.close();
            aliasesQry.close();
            getHorsesQry.close();
            conduit.dispose();
        }
        catch (SQLException sqle) {
            LOG.error(sqle.getMessage(), sqle);
        }
    }

    private synchronized void startUpdateQry() {
        Thread updateThread = new Thread(new Runnable() {
            public void run() {
                LOG.debug("Update started");
                while (!Thread.interrupted()) {
                    LOG.debug("Update loop");
                    try {
                        if (savedAliases.peek() == null &&
                                !theseSavedAliasesEmpty()) {
                            synchronized (updateQry) {
                                for (Alias a : theseSavedAliases) {
                                    updateQry.setInt(1, a.id);
                                    updateQry.setInt(2, a.horse.id);
                                    updateQry.addBatch();
                                }
                                updateQry.executeBatch();
                                updateQry.clearBatch();
                            }
                            synchronized (theseSavedAliases) {
                                theseSavedAliases.clear();
                            }
                            boolean stop = true;
                            synchronized (horses) {
                                stop = stop && horses.isEmpty();
                            }
                            synchronized (aliases) {
                                stop = stop && aliases.isEmpty();
                            }
                            synchronized (theseAliases) {
                                stop = stop && theseAliases.isEmpty();
                            }
                            synchronized (savedAliases) {
                                stop = stop && savedAliases.isEmpty();
                            }
                            synchronized (theseSavedAliases) {
                                stop = stop && theseSavedAliases.isEmpty();
                            }
                            if (stop) {
                                LOG.debug("Exiting");
                                stop();
                            }
                        }
                        else {
                            LOG.debug("Waiting for alias to be delivered");
                            Alias a = savedAliases.take();
                            LOG_COUNT.debug("savedAliases<: " +
                                    savedAliases.size());
                            if (!theseSavedAliases.contains(a)) {
                                theseSavedAliases.add(a);
                                LOG_COUNT.debug("theseSavedAliases>: " +
                                        theseSavedAliases.size());
                            }
                        }
                    }
                    catch (InterruptedException ie) {
                        LOG.debug("Interrupted");
                    }
                    catch (SQLException sqle) {
                        LOG.error(sqle.getMessage(), sqle);
                    }
                }
                LOG.debug("Update Thread ended naturally");
            }
        });
        updateThread.start();
        threads.add(updateThread);
    }

    private synchronized void startInsertQry() {
        Thread insertThread = new Thread(new Runnable() {
            public void run() {
                LOG.debug("Insert started");
                try {
                    while (!Thread.interrupted()) {
                        LOG.debug("Insert loop");
                        if (aliases.peek() == null &&
                                !theseAliasesEmpty()) {
                            LOG.debug("Got some in theseAliases");
                            synchronized (insertQry) {
                                for (Alias a : theseAliases) {
                                    insertQry.setInt(1, a.selection);
                                    insertQry.setString(2, a.horse.name);
                                    insertQry.addBatch();
                                }
                                LOG.debug(insertQry);
                                insertQry.executeBatch();
                                ResultSet keys = insertQry.getGeneratedKeys();
                                int keyCount = 0;
                                while (keys.next())
                                    keyCount++;
                                keys.first();
                                LOG.debug("keyCount: " + keyCount);
                                LOG.debug("theseAliases: " +
                                        theseAliases.size());
                                for (int i = 0; i < theseAliases.size(); i++) {
                                    theseAliases.get(i).id = keys.getInt(1);
                                    keys.next();
                                }
                                keys.close();
                                synchronized (theseAliases) {
                                    savedAliases.addAll(theseAliases);
                                    LOG_COUNT.debug("savedAliases>: " +
                                            savedAliases.size());
                                    theseAliases.clear();
                                    LOG_COUNT.debug("theseAliases<: " +
                                            theseAliases.size());
                                }
                            }
                        }
                        else {
                            LOG.debug("Waiting for alias to be delivered");
                            Alias a = aliases.take();
                            LOG_COUNT.debug("aliases<: " + aliases.size());
                            if (!theseAliases.contains(a)) {
                                synchronized (theseAliases) {
                                    theseAliases.add(a);
                                    LOG_COUNT.debug("theseAliases: " +
                                            theseAliases.size());
                                }
                            }
                        }
                    }
                }
                catch (InterruptedException ie) {
                    LOG.debug("Interrupted");
                }
                catch (SQLException sqle) {
                    LOG.error(sqle.getMessage(), sqle);
                }
                LOG.debug("Insert Thread ended naturally");
            }
        });
        insertThread.start();
        threads.add(insertThread);
    }

    private static int aliasesAdded = 0;
    private static int theseAliasesAdded = 0;

    private synchronized void startAliasesQry() {
        Thread aliasesThread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (!Thread.interrupted()) {
                        ResultSet rs;
                        Horse h = horses.take();
                        LOG_COUNT.debug("horses>: " + horses.size());
                        LOG.debug("awaiting aliasesQry");
                        synchronized (aliasesQry) {
                            LOG.debug("received aliasesQry");
                            LOG.debug(h.name + " --> " +
                                    h.getBestBetfairGuess());
                            aliasesQry.setString(1, h.getBestBetfairGuess());
                            LOG.debug("Set value");
                            rs = aliasesQry.executeQuery();
                            LOG.debug("Executed query");
                        }
                        if (!rs.next()) {
                            LOG.debug("continuing");
                            continue;
                        }
                        int selection = rs.getInt("selection");
                        LOG.debug("Got selection");
                        rs.close();
                        LOG.debug("Closed ResultSet");
                        aliases.offer(new Alias(selection, h));
                        LOG_COUNT.debug("added " + (++aliasesAdded)
                                + " aliases");
                    }
                }
                catch (InterruptedException ie) {
                    LOG.debug("Interrupted");
                }
                catch (SQLException sqle) {
                    LOG.error(sqle.getMessage(), sqle);
                }
                LOG.debug("Aliases Thread ended naturally");
            }
        });
        aliasesThread.start();
        threads.add(aliasesThread);
    }

    private synchronized void getNewHorses() {
        try {
            ResultSet horseRS = getHorsesQry.executeQuery();
            while (horseRS.next() && !Thread.interrupted()) {
                int id = horseRS.getInt("id");
                String name = horseRS.getString("name");
                Horse h = new Horse(id, name);
                if (!horses.contains(h)) {
                    horses.offer(h);
                    LOG_COUNT.debug("horses>: " + horses.size());
                }
            }
        }
        catch (SQLException sqle) {
            LOG.error(sqle.getMessage(), sqle);
        }
    }

    private class Horse {

        public final int id;
        public final String name;
        private String bestBetfairGuess = null;

        public Horse(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getBestBetfairGuess() {
            if (bestBetfairGuess != null)
                return bestBetfairGuess;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < name.length(); i++) {
                if (i < name.length() - 1 && name.charAt(i + 1) == '(')
                    return sb.toString();
                else if (name.charAt(i) != '\'') sb.append(name.charAt(i));
            }
            bestBetfairGuess = sb.toString();
            return bestBetfairGuess;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Horse) {
                Horse h = (Horse)obj;
                return id == h.id;
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 97 * hash + this.id;
            return hash;
        }
    }

    private class Alias {

        public int id;
        public final int selection;
        public final Horse horse;

        public Alias(int selection, Horse horse) {
            id = 0;
            this.selection = selection;
            this.horse = horse;
        }
    }

}
