/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables;

import java.util.Date;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.Field;
import uk.co.etothepii.haddock.db.tables.enumerations.BetCategoryType;
import uk.co.etothepii.haddock.db.tables.enumerations.BetPersistenceType;
import uk.co.etothepii.haddock.db.tables.enumerations.BetStatusType;
import uk.co.etothepii.haddock.db.tables.enumerations.BetType;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class BFMUBet extends HaddockAccessObject {

    private static final Logger LOG = Logger.getLogger(BFMUBet.class);

    public static final Field ASIAN_LINE_ID = new Field("asianLineId",
            Integer.class, BFMUBet.class);
    public static final Field BET_CATEGORY_TYPE = new Field("betCategoryType",
            BetCategoryType.class, BFMUBet.class);
    public static final Field BET_ID = new Field("betId",
            Long.class, BFMUBet.class);
    public static final Field BET_PERSISTENCE_TYPE = new Field(
            "betPersistenceType", BetPersistenceType.class, BFMUBet.class);
    public static final Field BET_STATUS_TYPE = new Field(
            "betStatusType", BetStatusType.class, BFMUBet.class);
    public static final Field BET_TYPE = new Field("betType", BetType.class,
            BFMUBet.class);
    public static final Field BSP_LIABILITY = new Field("bspLiability",
            Integer.class, BFMUBet.class);
    public static final Field MARKET_ID = new Field("marketId", Integer.class,
            BFMUBet.class);
    public static final Field MATCHED_DATE = new Field("matchedDate",
            Date.class, BFMUBet.class);
    public static final Field PLACED_DATE = new Field("placedDate",
            Date.class, BFMUBet.class);
    public static final Field PRICE = new Field("price", Integer.class,
            BFMUBet.class);
    public static final Field SELECTION_ID = new Field("selectionId",
            Integer.class, BFMUBet.class);
    public static final Field SIZE = new Field("size", Integer.class,
            BFMUBet.class);
    public static final Field TRANSACTION_ID = new Field("transactionId",
            Long.class, BFMUBet.class);

    private int asianLineId;
    private BetCategoryType betCategoryType;
    private long betId;
    private BetPersistenceType betPersistenceType;
    private BetStatusType betStatusType;
    private BetType betType;
    private int bspLiability;
    private int marketId;
    private Date matchedDate;
    private Date placedDate;
    private int price;
    private int selectionId;
    private int size;

    public BFMUBet(int asianLineId, BetCategoryType betCategoryType,
            long betId, BetPersistenceType betPersistenceType,
            BetStatusType betStatusType, BetType betType, int bspLiability,
            int marketId, Date matchedDate, Date placedDate, int price,
            int selectionId, int size, long transactionId) {
        super(transactionId);
        this.asianLineId = asianLineId;
        this.betCategoryType = betCategoryType;
        this.betId = betId;
        this.betPersistenceType = betPersistenceType;
        this.betStatusType = betStatusType;
        this.betType = betType;
        this.bspLiability = bspLiability;
        this.marketId = marketId;
        this.matchedDate = matchedDate;
        this.placedDate = placedDate;
        this.price = price;
        this.selectionId = selectionId;
        this.size = size;
    }

    public int getAsianLineId() {
        return asianLineId;
    }

    public void setAsianLineId(int asianLineId) {
        if (different(asianLineId, this.asianLineId)) {
            this.asianLineId = asianLineId;
            setChanged(true);
        }
    }

    public BetCategoryType getBetCategoryType() {
        return betCategoryType;
    }

    public void setBetCategoryType(BetCategoryType betCategoryType) {
        if (different(betCategoryType, this.betCategoryType)) {
            this.betCategoryType = betCategoryType;
            setChanged(true);
        }
    }

    public long getBetId() {
        return betId;
    }

    public void setBetId(long betId) {
        if (different(betId, this.betId)) {
            this.betId = betId;
            setChanged(true);
        }
    }

    public BetPersistenceType getBetPersistenceType() {
        return betPersistenceType;
    }

    public void setBetPersistenceType(BetPersistenceType betPersistenceType) {
        if (different(betPersistenceType, this.betPersistenceType)) {
            this.betPersistenceType = betPersistenceType;
            setChanged(true);
        }
    }

    public BetStatusType getBetStatusType() {
        return betStatusType;
    }

    public void setBetStatusType(BetStatusType betStatusType) {
        if (different(betStatusType, this.betStatusType)) {
            this.betStatusType = betStatusType;
            setChanged(true);
        }
    }

    public BetType getBetType() {
        return betType;
    }

    public void setBetType(BetType betType) {
        if (different(betType, this.betType)) {
            this.betType = betType;
            setChanged(true);
        }
    }

    public int getBspLiability() {
        return bspLiability;
    }

    public void setBspLiability(int bspLiability) {
        if (different(bspLiability, this.bspLiability)) {
            this.bspLiability = bspLiability;
            setChanged(true);
        }
    }

    public int getMarketId() {
        return marketId;
    }

    public void setMarketId(int marketId) {
        if (different(marketId, this.marketId)) {
            this.marketId = marketId;
            setChanged(true);
        }
    }

    public Date getMatchedDate() {
        return matchedDate;
    }

    public void setMatchedDate(Date matchedDate) {
        if (different(matchedDate, this.matchedDate)) {
            this.matchedDate = matchedDate;
            setChanged(true);
        }
    }

    public Date getPlacedDate() {
        return placedDate;
    }

    public void setPlacedDate(Date placedDate) {
        if (different(placedDate, this.placedDate)) {
            this.placedDate = placedDate;
            setChanged(true);
        }
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        if (different(price, this.price)) {
            this.price = price;
            setChanged(true);
        }
    }

    public int getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(int selectionId) {
        if (different(selectionId, this.selectionId)) {
            this.selectionId = selectionId;
            setChanged(true);
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if (different(size, this.size)) {
            this.size = size;
            setChanged(true);
        }
    }

}
