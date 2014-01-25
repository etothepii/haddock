/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables.enumerations;

/**
 *
 * @author jrrpl
 */
public enum BetCategoryType {

    NONE, //Equivalent to E. This may change in a future release
    E, // Normal exchange bet
    M, //Market on Close bet. The bet remains unmatched until the market is
       //reconciled and a starting price is determined. If no starting price is available
       //for the selection, the bet lapses
    L; //Limit on Close bet. The bet remains unmatched until the market is
       //reconciled and a starting price is determined. If the starting price is better
       //than the price specified, then the bet is matched. If no starting price is
       //available for the selection, the bet lapses


}
