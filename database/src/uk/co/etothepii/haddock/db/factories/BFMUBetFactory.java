/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.factories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.tables.BFMUBet;
import uk.co.etothepii.haddock.db.tables.enumerations.BetCategoryType;
import uk.co.etothepii.haddock.db.tables.enumerations.BetPersistenceType;
import uk.co.etothepii.haddock.db.tables.enumerations.BetStatusType;
import uk.co.etothepii.haddock.db.tables.enumerations.BetType;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class BFMUBetFactory extends HaddockFactory<BFMUBet> {

    private static final SimpleDateFormat sdf =
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private static final Logger LOG = Logger.getLogger(BFMUBetFactory.class);

    private static final Logger LOG_QUERY =
            Logger.getLogger(BFMUBetFactory.class.getName().concat("query"));

    @Override
    protected String getTablename() {
        return "bfMUBet";
    }

    private static BFMUBetFactory factory;

    public static BFMUBetFactory getFactory() {
        if (factory == null)
            factory = new BFMUBetFactory();
        return factory;
    }

    private BFMUBetFactory() {}

    @Override
    public BFMUBet build(ResultSet rs) {
        try {
            LOG.debug(rs.toString());
            int asianLineId = rs.getInt(BFMUBet.ASIAN_LINE_ID.name);
            BetCategoryType betCategoryType =
                    BetCategoryType.valueOf(rs.getString(
                    BFMUBet.BET_CATEGORY_TYPE.name));
            long betId = rs.getLong(BFMUBet.BET_ID.name);
            BetPersistenceType betPersistenceType =
                    BetPersistenceType.valueOf(
                    rs.getString(BFMUBet.BET_PERSISTENCE_TYPE.name));
            BetStatusType betStatusType =
                    BetStatusType.valueOf(
                    rs.getString(BFMUBet.BET_STATUS_TYPE.name));
            BetType betType =
                    BetType.valueOf(rs.getString(BFMUBet.BET_TYPE.name));
            int bspLiability = rs.getInt(BFMUBet.BSP_LIABILITY.name);
            int marketId = rs.getInt(BFMUBet.MARKET_ID.name);
            Date matchedDate = new Date(rs.getTimestamp(
                    BFMUBet.MATCHED_DATE.name).getTime());
            Date placedDate = new Date(rs.getTimestamp(
                    BFMUBet.PLACED_DATE.name).getTime());
            int price = rs.getInt(BFMUBet.PRICE.name);
            int selectionId = rs.getInt(BFMUBet.SELECTION_ID.name);
            int size = rs.getInt(BFMUBet.SIZE.name);
            long transactionId = rs.getLong(BFMUBet.TRANSACTION_ID.name);
            return new BFMUBet(asianLineId, betCategoryType, betId,
                    betPersistenceType, betStatusType, betType, bspLiability,
                    marketId, matchedDate, placedDate, price, selectionId, size,
                    transactionId);
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
    }

    @Override
    protected String getInsertQueryStr() {
        return "INSERT INTO `" + getDatabasename() + "`.`" + getTablename()
                + "` (`" + BFMUBet.ASIAN_LINE_ID.name + "`, `"
                + BFMUBet.BET_CATEGORY_TYPE.name + "`, `"  
                + BFMUBet.BET_ID.name + "`, `" 
                + BFMUBet.BET_PERSISTENCE_TYPE.name + "`, `" 
                + BFMUBet.BET_STATUS_TYPE.name + "`, `" 
                + BFMUBet.BET_TYPE.name + "`, `" 
                + BFMUBet.BSP_LIABILITY.name + "`, `" 
                + BFMUBet.MARKET_ID.name + "`, `" 
                + BFMUBet.MATCHED_DATE.name + "`, `" 
                + BFMUBet.PLACED_DATE.name + "`, `"  
                + BFMUBet.PRICE.name + "`, `" 
                + BFMUBet.SELECTION_ID.name + "`, `" 
                + BFMUBet.SIZE.name + "`, `" 
                + BFMUBet.TRANSACTION_ID.name + "`) VALUES ("
                + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    }

    @Override
    protected PreparedStatement prepareInsertStatement(BFMUBet t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        fillStatement(toRet, t);
        return toRet;
    }

    private void fillStatement(PreparedStatement ps, BFMUBet t)
            throws SQLException {
        ps.setInt(1, t.getAsianLineId());
        ps.setString(2, t.getBetCategoryType().name());
        ps.setLong(3, t.getBetId());
        ps.setString(4, t.getBetPersistenceType().name());
        ps.setString(5, t.getBetStatusType().name());
        ps.setString(6, t.getBetType().name());
        ps.setInt(7, t.getBspLiability());
        ps.setInt(8, t.getMarketId());
        ps.setTimestamp(9, new Timestamp(t.getMatchedDate().getTime()));
        ps.setTimestamp(10, new Timestamp(t.getPlacedDate().getTime()));
        ps.setInt(11, t.getPrice());
        ps.setInt(12, t.getSelectionId());
        ps.setInt(13, t.getSize());
        ps.setLong(14, t.getId());
    }

    @Override
    protected String getUpdateQueryStr() {
        return "UPDATE `" + getDatabasename() + "`.`" + getTablename()
                + "` SET `" + BFMUBet.ASIAN_LINE_ID.name + "` = ?, `"
                + BFMUBet.BET_CATEGORY_TYPE.name + "` = ?, `"
                + BFMUBet.BET_ID.name + "` = ?, `"
                + BFMUBet.BET_PERSISTENCE_TYPE.name + "` = ?, `"
                + BFMUBet.BET_STATUS_TYPE.name + "` = ?, `"
                + BFMUBet.BET_TYPE.name + "` = ?, `"
                + BFMUBet.BSP_LIABILITY.name + "` = ?, `"
                + BFMUBet.MARKET_ID.name + "` = ?, `"
                + BFMUBet.MATCHED_DATE.name + "` = ?, `"
                + BFMUBet.PLACED_DATE.name + "` = ?, `"
                + BFMUBet.PRICE.name + "` = ?, `"
                + BFMUBet.SELECTION_ID.name + "` = ?, `"
                + BFMUBet.SIZE.name + "` = ?, `"
                + "WHERE `" + BFMUBet.TRANSACTION_ID.name + "` = ? LIMIT 1";
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(BFMUBet t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledUpdateStatement(conduit);
        fillStatement(toRet, t);
        return toRet;
    }

}
