/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables;

import org.apache.log4j.Logger;
import uk.co.etothepii.db.Field;
import uk.co.etothepii.db.FieldChangedEvent;
import uk.co.etothepii.haddock.db.tables.enumerations.YN;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class MarketSelection extends HaddockAccessObject {

    public static final Field SELECTION = new Field("selection",
            Selection.class, MarketSelection.class);
    public static final Field MARKET = new Field("market",
            BFMarket.class, MarketSelection.class);
    public static final Field WIN_FLAG = new Field("winFlag",
            YN.class, MarketSelection.class);

    private static final Logger LOG = Logger.getLogger(MarketSelection.class);

    private Selection selection;
    private BFMarket market;
    private YN winFlag;

    public MarketSelection(int id, Selection selection, BFMarket market,
            YN winFlag) {
        super(id);
        this.selection = selection;
        this.market = market;
        this.winFlag = winFlag;
    }

    public MarketSelection(Selection selection, BFMarket market, YN winFlag) {
        this(0, selection, market, winFlag);
    }

    public Selection getSelection() {
        return selection;
    }

    public void setSelection(Selection selection) {
        if (different(selection, this.selection)) {
            Selection old = this.selection;
            this.selection = selection;
            fireFieldChangedListener(new FieldChangedEvent(this, SELECTION, old,
                    selection));
            setChanged(true);
        }
    }

    public BFMarket getMarket() {
        return market;
    }

    public void setMarket(BFMarket market) {
        if (different(market, this.market)) {
            BFMarket old = this.market;
            this.market = market;
            fireFieldChangedListener(new FieldChangedEvent(this, MARKET, old,
                    market));
            setChanged(true);
        }
    }

    public YN getWinFlag() {
        return winFlag;
    }

    public void setWinFlag(YN winFlag) {
        if (different(winFlag, this.winFlag)) {
            YN old = this.winFlag;
            this.winFlag = winFlag;
            fireFieldChangedListener(new FieldChangedEvent(this, WIN_FLAG, old,
                    winFlag));
            setChanged(true);
        }
    }

}
