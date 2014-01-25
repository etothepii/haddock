/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables.enumerations;

/**
 *
 * @author jrrpl
 */
public enum YN {

    Y("Y","Yes"),
    N("N","No");

    public final String shortText;
    public final String longText;

    private YN(String shortText, String longText) {
        this.shortText = shortText;
        this.longText = longText;
    }

    public static YN getYN(String shortText) {
        if (shortText.equalsIgnoreCase("Y")) return Y;
        else if(shortText.equalsIgnoreCase("N")) return N;
        else throw new IllegalArgumentException("Provided enum does not exist");
    }
}
