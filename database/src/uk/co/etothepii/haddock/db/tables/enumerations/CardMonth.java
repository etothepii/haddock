/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables.enumerations;

/**
 *
 * @author jrrpl
 */
public enum CardMonth {

    JANUARY("01","Jan","January"),
    FEBRUARY("02","Feb","February"),
    MARCH("03","Mar","March"),
    APRIL("04","Apr","April"),
    MAY("05","May","May"),
    JUNE("06","Jun","June"),
    JULY("07","Jul","July"),
    AUGUST("08","Aug","August"),
    SEPTEMBER("09","Sep","September"),
    OCTOBER("10","Oct","October"),
    NOVEMBER("11","Nov","November"),
    DECEMBER("12","Dec","December");

    public final String numeric;
    public final String shortName;
    public final String longName;

    private CardMonth(String numeric, String shortName, String longName) {
        this.numeric = numeric;
        this.shortName = shortName;
        this.longName = longName;
    }

    public static CardMonth getCardMonth(String numeric) {
        try {
            return getCardMonth(Integer.parseInt(numeric) - 1);
        }
        catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Argument unparseable");
        }
    }

    public static CardMonth getCardMonth(int month) {
        if (month == 0) return JANUARY;
        else if (month == 1) return FEBRUARY;
        else if (month == 2) return MARCH;
        else if (month == 3) return APRIL;
        else if (month == 4) return MAY;
        else if (month == 5) return JUNE;
        else if (month == 6) return JULY;
        else if (month == 7) return AUGUST;
        else if (month == 8) return SEPTEMBER;
        else if (month == 9) return OCTOBER;
        else if (month == 10) return NOVEMBER;
        else if (month == 11) return DECEMBER;
        else throw new IllegalArgumentException("month supplied is incorrect");
    }

}
