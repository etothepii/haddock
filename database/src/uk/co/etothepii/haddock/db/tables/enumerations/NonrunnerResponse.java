/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables.enumerations;

/**
 *
 * @author jrrpl
 */
public enum NonrunnerResponse {
    NULL_PASSED("Null passed"),
    NO_NONRUNNER_ATTACHED("No nonrunner attached"),
    MARKET_NOT_YET_MATCHED("Market not yet matched"),
    FOUND("Found"),
    LIVE_ON_BETFAIR("Live on betfair"),
    PREVIOUSLY_FOUND("Previously found"),
    RACE_RAN("Race ran"),
    BETFAIR_ERROR("Betfair error"),
    BETFAIR_ACCESS_EXCEPTION("Betfair access exception"),
    REMOVED_BEFORE_MARKET_OPENED("Removed before market opened"),
    SQL_EXCEPTION("SQL exception");

    private final String description;

    private NonrunnerResponse(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
