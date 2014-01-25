/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables;

import java.util.Date;
import uk.co.etothepii.db.Field;

/**
 *
 * @author jrrpl
 */
public class BFPriceData extends HaddockAccessObject {
    
    public static final Field MARKET_SELECTION = new Field("marketSelection", 
            MarketSelection.class, BFPriceData.class);
    public static final Field PRICE = new Field("price", 
            Integer.class, BFPriceData.class);
    public static final Field BACK = new Field("back", 
            Integer.class, BFPriceData.class);
    public static final Field LAY = new Field("lay", 
            Integer.class, BFPriceData.class);
    public static final Field TRADED = new Field("traded", 
            Integer.class, BFPriceData.class);
    public static final Field TIMESTAMP = new Field("timeStamp", 
            Date.class, BFPriceData.class);

    private MarketSelection marketSelection;
    private int price;
    private int back;
    private int lay;
    private int traded;
    private Date timestamp;

    public BFPriceData(int id, MarketSelection marketSelection, int price, int back,
            int lay, int traded, Date timestamp) {
        super(id);
        this.marketSelection = marketSelection;
        this.price = price;
        this.back = back;
        this.lay = lay;
        this.traded = traded;
        this.timestamp = timestamp;
    }

    public BFPriceData(MarketSelection marketSelection, int price, int back,
            int lay, int traded, Date timestamp) {
        this(0, marketSelection, price, back, lay, traded, timestamp);
    }

    public int getBack() {
        return back;
    }

    public int getTraded() {
        return traded;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getPrice() {
        return price;
    }

    public MarketSelection getMarketSelection() {
        return marketSelection;
    }

    public int getLay() {
        return lay;
    }

}
