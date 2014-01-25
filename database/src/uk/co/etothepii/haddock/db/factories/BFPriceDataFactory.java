/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.factories;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.tables.BFPriceData;
import uk.co.etothepii.haddock.db.tables.BookmakerAccount;
import uk.co.etothepii.haddock.db.tables.BookmakerAccountBalance;

/**
 *
 * @author jrrpl
 */
public class BFPriceDataFactory extends 
        HaddockFactory<BFPriceData> {

    @Override
    protected String getTablename() {
        return "bfPriceData";
    }

    private static final Logger LOG =
            Logger.getLogger(BFPriceDataFactory.class);

    private static BFPriceDataFactory factory;

    private BFPriceDataFactory() {}

    public static BFPriceDataFactory getFactory() {
        if (factory == null)
            factory = new BFPriceDataFactory();
        return factory;
    }

    @Override
    protected BFPriceData build(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            int marketSelectionId = rs.getInt("marketSelection");
            int price = rs.getInt("price");
            int back = rs.getInt("back");
            int lay = rs.getInt("lay");
            int traded = rs.getInt("traded");
            Timestamp timestamp = rs.getTimestamp("timestamp");
            return new BFPriceData(id, MarketSelectionFactory.getFactory(
                    ).getFromId(marketSelectionId), price, back, lay, traded,
                    timestamp);
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
    }

    @Override
    protected String getInsertQueryStr() {
        return "INSERT INTO `" + getDatabasename() + "`.`" + getTablename()
                + "` (`id`, `marketSelection`, `price`, `back`, `lay`, "
                + "`traded`, `timestamp`) VALUES (NULL, ?, ?, ?, ?, ?, ?); ";
    }

    @Override
    protected PreparedStatement prepareInsertStatement(
            BFPriceData t, DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        toRet.setInt(1, (int)t.getMarketSelection().getId());
        toRet.setInt(2, t.getPrice());
        toRet.setInt(3, t.getBack());
        toRet.setInt(4, t.getLay());
        toRet.setInt(5, t.getTraded());
        toRet.setTimestamp(6, new Timestamp(t.getTimestamp().getTime()));
        return toRet;
    }

    @Override
    protected String getUpdateQueryStr() {
        throw new UnsupportedOperationException("These records should not be "
                + "updated");
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(
            BFPriceData t, DatabaseConduit conduit) throws SQLException {
        throw new UnsupportedOperationException("These records should not be "
                + "updated");
    }

    
}
