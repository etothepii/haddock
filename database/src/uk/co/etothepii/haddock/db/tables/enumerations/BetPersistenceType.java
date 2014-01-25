/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables.enumerations;

/**
 *
 * @author jrrpl
 */
public enum BetPersistenceType {

    NONE, //Normal exchange or SP bet. Unmatched exchange bets are lapsed when the
          //market turns in-play and SP bets are matched at the Starting Price.
    IP,   //In Play persistence. Unmatched Exchange bets (or the unmatched portion
          //of a bet) remain in the market when it turns in-play. The bet retains its place
          //in the bet queue and retains the same betId.
    SP,   //MoC Starting Price. The Unmatched (or unmatched portion) Exchange bet
          //is converted to a Market on Close Starting Price bet and matched at the
          //reconciled starting price.



}
