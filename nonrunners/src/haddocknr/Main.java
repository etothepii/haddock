/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package haddocknr;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import org.apache.log4j.Logger;
import uk.co.etothepii.comms.Message;
import uk.co.etothepii.comms.MessageListener;
import uk.co.etothepii.comms.TelnetMessageClient;
import uk.co.etothepii.haddock.betfair.User;
import uk.co.etothepii.haddock.db.factories.NonrunnerFactory;
import uk.co.etothepii.haddock.db.tables.BFMarket;
import uk.co.etothepii.haddock.db.tables.Nonrunner;
import uk.co.etothepii.haddock.db.tables.Race;
import uk.co.etothepii.nr.client.BetfairProcessor;
import uk.co.etothepii.nr.server.BHAReader;
import uk.co.etothepii.nr.server.UnknownSelectionProcessor;

/**
 *
 * @author jrrpl
 */
public class Main {

    public static final int[] LISTENING_PORTS =
            new int[] {26747, 6747, 7472, 47267, 7267};
    public static final int[] BROADCAST_PORTS =
            new int[] {26748, 6748, 7482, 48267, 8267};

    private static final Logger LOG = Logger.getLogger(Main.class);
    private static final String fileName = System.getProperty("user.home") +
            "/.haddockNR.lock";
    private static File file = new File(fileName);

    /**
     * @param args the command line arguments
     */

    private static boolean getRunning() {
        return file.exists();
    }

    private static void setRunning(boolean b) throws IOException {
        if (b) {
            file.createNewFile();
        }
        else {
            file.delete();
        }
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("SERVER")) {
            if (args.length > 2) {
                for (int i = 1; i < args.length - 1; i++) {
                    if (args[i].equals("-e")) {
                        if (getRunning()) System.exit(0);
                        try {
                            setRunning(true);
                        }
                        catch (IOException ioe) {
                            try {
                                setRunning(false);
                            }
                            catch (IOException ioe2) {}
                            System.exit(0);
                        }
                        try {
                            int time = Integer.parseInt(args[i + 1]);
                            int h = time / 100;
                            int m = time % 100;
                            if (h < 24 && m < 60) {
                                Calendar cal = Calendar.getInstance();
                                LOG.debug(cal.get(Calendar.HOUR_OF_DAY));
                                LOG.debug(cal.get(Calendar.MINUTE));
                                int dh = h - cal.get(Calendar.HOUR_OF_DAY);
                                if (dh < 0) dh += 24;
                                int dm = m - cal.get(Calendar.MINUTE);
                                if (dm < 0) {
                                    dm += 60;
                                    dh--;
                                }
                                LOG.debug("dm: " + dm);
                                LOG.debug("dh: " + dh);
                                cal.add(Calendar.HOUR_OF_DAY, dh);
                                cal.add(Calendar.MINUTE, dm);
                                cal.set(Calendar.SECOND, 0);
                                cal.set(Calendar.MILLISECOND, 0);
                                final long sleep = cal.getTimeInMillis() -
                                        System.currentTimeMillis();
                                LOG.debug("exiting in " + sleep
                                        + "milliseconds");
                                new Thread(new Runnable() {
                                    public void run() {
                                        try {
                                            Thread.sleep(sleep);
                                        }
                                        catch (InterruptedException ie) {}
                                        try {
                                            setRunning(false);
                                        }
                                        catch (IOException ioe) {}
                                        System.exit(0);
                                    }
                                }).start();
                            }

                        }
                        catch (NumberFormatException nfe) {}
                    }
                    break;
                }
            }
            try {
                LOG.debug("HELLO");
                UnknownSelectionProcessor uap = new UnknownSelectionProcessor();
                TelnetMessageClient tmc = new TelnetMessageClient(
                        BROADCAST_PORTS, "localhost");
                try {Thread.sleep(5000L);} catch (InterruptedException ie) {}
                LOG.debug("starting ...");
                new BHAReader(BHAReader.BASE_FILE, BHAReader.BASE_URL, tmc, uap);
            }
            catch (IOException ioe) {
                LOG.error(ioe.getMessage(), ioe);
                System.exit(0);
            }
        }
        else if (args.length > 0 && args[0].equals("CLIENT")) {
            try {
                LOG.debug("Running client");
                TelnetMessageClient client =
                        new TelnetMessageClient(LISTENING_PORTS,
                        "etothepii.r-pl.co.uk");
                NonrunnerFactory.getFactory().getFromId(1622L);
                client.addMessageListener(
                        TelnetMessageClient.NEW_NON_RUNNER_COMMAND,
                        new MessageListener() {
                    public void messageReceived(Message e) {
                        long id = Long.parseLong(e.getMessage());
                        LOG.debug("Received nonrunner id: " + id);
                        Nonrunner n = NonrunnerFactory.getFactory().getFromId(
                                id);
                        if (n == null) {
                            LOG.debug("Non runner was null");
                            return;
                        }
                        Race r = n.getRace();
                        if (r == null) {
                            LOG.debug("Race for Nonrunner " + n.getId() + " was null");
                            return;
                        }
                        BFMarket b = r.getMarket();
                        if (b == null) {
                            LOG.debug("BFMarket for race " + r.getId() + " was null");
                            return;
                        }
                        String s = "firefox http://sports.betfair.com/Index.do?mi=" +
                                b.getBetfairId() + "&ex=1&origin=MRL";
                        try {
                            LOG.debug("Executing " + s);
                            Runtime.getRuntime().exec(s);
                        }
                        catch (IOException ioe) {}
                    }
                });
            }
            catch (IOException ioe) {
                LOG.error(ioe.getMessage(), ioe);
                System.exit(0);
            }
        }
    }

}
