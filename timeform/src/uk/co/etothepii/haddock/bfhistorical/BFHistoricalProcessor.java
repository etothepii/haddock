/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.bfhistorical;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.ZipFile;
import org.apache.log4j.Logger;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class BFHistoricalProcessor {

    private static final Logger LOG =
            Logger.getLogger(BFHistoricalProcessor.class);

    public BFHistoricalProcessor() {
    }

    public ArrayList<NonTimestampedRow> process(File file) {
        ZipFile zf = null;
        try {
            String ext;
            String name;
            {
                String temp = file.getName();
                LOG.debug("temp: " + temp);
                String[] tempAry = temp.split("\\.");
                ext = tempAry[tempAry.length - 1];
                name = temp.substring(0, temp.length() - ext.length());
            }
            InputStream in;
            if (ext.equalsIgnoreCase("zip")) {
                zf = new ZipFile(file);
                in = zf.getInputStream(zf.getEntry(name.concat("csv")));
            }
            else {
                in = new FileInputStream(file);
            }
            return process(in);
        }
        catch (IOException ioe) {
            LOG.error(ioe.getMessage(), ioe);
        }
        finally {
            if (zf != null) {
                try {
                    zf.close();
                }
                catch (IOException ioe) {
                    LOG.error(ioe.getMessage(), ioe);
                }
            }
        }
        return null;
    }

    private ArrayList<NonTimestampedRow> process(InputStream inputStream) {
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(inputStream);
            br = new BufferedReader(isr);
            ArrayList<NonTimestampedRow> toRet =
                    new ArrayList<NonTimestampedRow>();
            String in = br.readLine();
            while ((in = br.readLine()) != null) {
                try {
                    NonTimestampedRow ntr = NonTimestampedRow.getRow(in);
                    if (ntr != null)
                        toRet.add(ntr);
                }
                catch (RuntimeException re) {
                    LOG.error(in);
                    LOG.error(re.getMessage(), re);
                }
            }
            return toRet;
        }
        catch (IOException ioe) {
            LOG.error(ioe.getMessage(), ioe);
        }
        finally {
            try {
                if (br != null)
                    br.close();
            }
            catch (IOException ioe) {
                LOG.error(ioe.getMessage(), ioe);
            }
            try {
                if (isr != null)
                    isr.close();
            }
            catch (IOException ioe) {
                LOG.error(ioe.getMessage(), ioe);
            }
        }
        return null;
    }
}
