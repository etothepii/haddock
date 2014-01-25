/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.betfair.data;

import java.util.Calendar;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class RemovedRunner {

    private static final Logger LOG = Logger.getLogger(RemovedRunner.class);

    public final String selectionName;
    public final Date removedDate;
    public final double adjustmentFactor;

    public RemovedRunner(String selectionName, Date removedDate,
            double adjustmentFactor) {
        this.selectionName = selectionName;
        this.removedDate = removedDate;
        this.adjustmentFactor = adjustmentFactor;
    }

    public static RemovedRunner process(String raw) {
        try {
            LOG.debug(raw);
            String[] split = raw.split(",");
            String selectionName = split[0];
            double adjustmentFactor = Double.parseDouble(split[2]);
            String[] time = split[1].split("\\.");
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(time[1]));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            while (cal.getTimeInMillis() > System.currentTimeMillis())
                cal.add(Calendar.DAY_OF_MONTH, -1);
            Date removedDate = new Date(cal.getTimeInMillis());
            return new RemovedRunner(selectionName, removedDate, adjustmentFactor);
        }
        catch (IndexOutOfBoundsException ioobe) {
            LOG.error("raw: " + raw);
            LOG.error(ioobe.getMessage(), ioobe);
            throw new IllegalArgumentException(
                    "There was a problem parsing the raw data", ioobe);
        }
    }

    @Override
    public RemovedRunner clone() {
        return new RemovedRunner(selectionName, removedDate, adjustmentFactor);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RemovedRunner)) return false;
        return (selectionName.equals(((RemovedRunner)obj).selectionName));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.selectionName != null ? this.selectionName.hashCode() : 0);
        return hash;
    }
}
