/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package haddocktimeformapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import uk.co.etothepii.haddock.timeform.HorseCleaner;
import uk.co.etothepii.haddock.timeform.TimeformInterface;

/**
 *
 * @author jrrpl
 */
public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
		Properties logProperties = new Properties();
		logProperties.load(new FileInputStream ("/Users/jrrpl/NetBeansProjects/HaddockTimeformApp/src/log4j.properties"));
		PropertyConfigurator.configure(logProperties);
		LOG.debug("Started");
        if (args.length == 1 && args[0].equals("HORSECLEANER")) {
            try {
                HorseCleaner hc = new HorseCleaner();
            }
            catch (SQLException sqle) {
                LOG.error(sqle.getMessage(), sqle);
            }
        }
        else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
            TimeformInterface timeformInterface = new TimeformInterface();
            Date startDate = null;
            Date stopDate = null;
            File directory = null;
            if (args.length <= 1) {
                Calendar cal = Calendar.getInstance();
                if (cal.get(Calendar.HOUR_OF_DAY) < 7)
                    cal.add(Calendar.DATE, -1);
                cal.set(Calendar.HOUR_OF_DAY, 12);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                startDate = new Date(cal.getTimeInMillis());
                stopDate = new Date(cal.getTimeInMillis());
            }
            if (args.length %2 == 1) {
                directory = new File(args[args.length - 1]);
            }
            if (args.length >= 2) {
                try {
                    startDate = sdf.parse(args[0].concat(args[0].length() == 8 ? "12" : ""));
                    stopDate = sdf.parse(args[1].concat(args[1].length() == 8 ? "12" : ""));
                }
                catch (ParseException pe) {
                    System.out.println(
                            "There was an error parsing the dates supplied");
                    System.exit(1);
                }
            }
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(startDate.getTime());
            for (; cal.getTimeInMillis() <= stopDate.getTime();
                    cal.add(Calendar.DAY_OF_MONTH, 1)) {
                Date date = new Date(cal.getTimeInMillis());
                LOG.debug(date);
                timeformInterface.download(date, directory);
            }
            System.exit(0);
        }
    }

    private static String stripApostrophe(String in) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < in.length(); i++) {
            if (in.charAt(i + 1) == '(') return sb.toString();
            else if (in.charAt(i) != '\'') sb.append(in.charAt(i));
        }
        return sb.toString();
    }

}
