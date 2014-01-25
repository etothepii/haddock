/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package haddockam;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import uk.co.epii.betfairclient.BFExchange;
import uk.co.epii.betfairclient.UserBfImpl;


/**
 *
 * @author jrrpl
 */
public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class);
    private static final String fileName = System.getProperty("user.home") + 
            "/.haddockAM.lock";
    private static File file = new File(fileName);

    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) throws IOException {
        try {
            LOG.debug("Starting");
            run();
        }
        finally {
			System.exit(0);
        }
    }

    private static boolean getRunning() {
        return file.exists();
    }

    private static void setRunning(boolean b) throws IOException {
		if (LOG.isDebugEnabled())
		    LOG.debug("setRunning(" + b + ")");
        if (b) {
            file.createNewFile();
        }
        else {
            file.delete();
        }
    }

    public static void run() throws IOException {
        if (getRunning()) {
			LOG.info("Program already running");
			return;
		}
        try {
            setRunning(true);
            BFExchange bfExchange = BFExchange.getInstance();
			String username = "JosephTildesley";
			String password = "Hannah1016";
			LOG.debug("username: " + username);
			LOG.debug("password: " + password);
			bfExchange.login(new UserBfImpl(username, password));
            uk.co.etothepii.haddock.allmarkets.Main.execute(bfExchange);
            LOG.debug("Finished Cycle");
        }
		catch (RuntimeException re) {
			LOG.error(re.getMessage(), re);
			throw re;
		}
		catch (InterruptedException ie) {
			LOG.error(ie.getMessage(), ie);
		}
        finally {
            setRunning(false);
        }
    }
}
