/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package haddockrelay;

import java.io.IOException;
import java.io.PrintWriter;
import org.apache.log4j.Logger;
import uk.co.etothepii.comms.Message;
import uk.co.etothepii.comms.TelnetMessageServer;
import uk.co.etothepii.util.MessageReceivedListener;

/**
 *
 * @author jrrpl
 */
public class Main {

    public static final String UNKNOWN_RACECOURSE = "UNKNOWNRACECOURSE";
    public static final String UNKNOWN_ALIAS = "UNKNOWNALIAS";
    public static final String NEW_NONRUNNER = "NEWNONRUNNER";
    public static final int[] BROADCAST_PORTS = new int[] {26747, 6747, 7472, 47267, 7267};
    public static final int[] LISTENING_PORTS = new int[] {26748, 6748, 7482, 48267, 8267};

    private static final Logger LOG = Logger.getLogger(Main.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            final TelnetMessageServer broadcast =
                    new TelnetMessageServer(BROADCAST_PORTS,
                    "EHLO from Etothepii Ltd's Piranha Relay (Outgoing)");
            final TelnetMessageServer listening =
                    new TelnetMessageServer(LISTENING_PORTS,
                    "EHLO from Etothepii Ltd's Piranha Relay (Incoming)");
            listening.addMessageReceivedListener(new MessageReceivedListener() {
                public void processLine(String in, PrintWriter pw) {
                    String[] split = in.split("~");
                    if (split.length == 2) {
                        if (split[0].equals(UNKNOWN_RACECOURSE)) {
                            if (split[1].length() <= 256) {
                                broadcast.broadcastMessage(
                                        new Message(UNKNOWN_RACECOURSE, split[1]));
                            }
                        }
                        else if(split[0].equals(NEW_NONRUNNER)) {
                            try {
                                broadcast.broadcastMessage(
                                        new Message(NEW_NONRUNNER,
                                        Integer.parseInt(split[1]) + ""));
                            }
                            catch (NumberFormatException nfe) {}
                        }
                    }
                    else if(split.length == 3) {
                        if(split[0].equals(UNKNOWN_ALIAS)) {
                            try {
                                broadcast.broadcastMessage(new Message(
                                        UNKNOWN_ALIAS,
                                        Integer.parseInt(split[1]) + "~" +
                                        Integer.parseInt(split[2])));
                            }
                            catch (NumberFormatException nfe) {}
                        }
                    }
                    return;
                }
            });
        }
        catch (IOException ioe) {
            LOG.error(ioe.getMessage(), ioe);
        }
    }

}
