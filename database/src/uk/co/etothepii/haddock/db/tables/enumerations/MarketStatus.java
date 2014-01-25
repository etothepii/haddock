/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables.enumerations;

import org.apache.log4j.Logger;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public enum MarketStatus {

    ACTIVE,
    SUSPENDED,
    CLOSED;

    private static final Logger LOG = Logger.getLogger(MarketStatus.class);

}
