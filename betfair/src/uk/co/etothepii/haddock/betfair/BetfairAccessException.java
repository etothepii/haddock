/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.betfair;

/**
 *
 * @author philip
 */
public class BetfairAccessException extends Exception {

    private Throwable cause;

    public BetfairAccessException(String message, Throwable cause) {
        super(message, cause);
        this.cause = cause;
    }

    public BetfairAccessException(String message) {
        this(message, null);
    }

    @Override
    public Throwable getCause() {
        return super.getCause();
    }
}
