/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.factories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.HaddockStatement;
import uk.co.etothepii.haddock.db.tables.Bookmaker;
import uk.co.etothepii.haddock.db.tables.BookmakerShop;

/**
 *
 * @author jrrpl
 */
public class BookmakerShopFactory extends
        HaddockFactory<BookmakerShop> {

    private static final Logger LOG = Logger.getLogger(BookmakerShopFactory.class);

    @Override
    protected String getTablename() {
        return "bookmakerShops";
    }

    private static BookmakerShopFactory factory;

    public static BookmakerShopFactory getFactory() {
        if (factory == null)
            factory = new BookmakerShopFactory();
        return factory;
    }

    private BookmakerShopFactory() {}

    @Override
    protected BookmakerShop build(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            int bookmakerId = rs.getInt("bookmaker");
            Integer number = rs.getInt("number");
            if (number == 0) number = null;
            String address = rs.getString("address");
            String postcode = rs.getString("postcode");
            Float latitude = rs.getFloat("latitude");
            if (latitude == 0) latitude = null;
            Float longitude = rs.getFloat("longitude");
            if (longitude == 0) longitude = null;
            Bookmaker bookmaker = BookmakerFactory.getFactory(
                    ).getFromId(bookmakerId);
            return new BookmakerShop(id, bookmaker,
                    number, address, postcode, latitude, longitude);
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
    }

    private HaddockStatement getAllQry = new HaddockStatement("SELECT * FROM `"
            + getDatabasename() + "`.`" + getTablename() + "` ORDER by `"
            + BookmakerShop.POST_CODE.name + "` ASC", false);

    public ArrayList<BookmakerShop> getAll() throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return getAll(conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public ArrayList<BookmakerShop> getAll(DatabaseConduit conduit)
            throws SQLException {
        ArrayList<BookmakerShop> bookmakerShops = new ArrayList<BookmakerShop>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = getAllQry.getStatement(conduit);
            rs = ps.executeQuery();
            while (rs.next()) {
                BookmakerShop bs = build(rs);
                bookmakerShops.add(bs);
                if (LOG.isDebugEnabled())
                    LOG.debug(bs.getIdentifyingLabel() + ", " + bs.getPostCode());
            }
        }
        finally {
            if (rs != null) {
                while (rs.next()) {}
                rs.close();
            }
            if (ps != null) {
				ps.close();
			}
        }
        return bookmakerShops;
    }
    
    @Override
    protected String getInsertQueryStr() {
        return "INSERT INTO `" + getDatabasename() + "`.`" + getTablename()
                + "` (`id`, `bookmaker`,"
                + "`number`, `address`, `postcode`, `latitude`, `longitude`) "
                + "VALUES (NULL, ?, ?, ?, ?, ?, ?);";
    }

    @Override
    protected PreparedStatement prepareInsertStatement(BookmakerShop t,
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        toRet.setInt(1, (int)t.getBookmaker().getId());
        if (t.getNumber() == null)
            toRet.setNull(2, Types.INTEGER);
        else
            toRet.setInt(2, t.getNumber());
        toRet.setString(3, t.getAddress());
        toRet.setString(4, t.getPostCode());
        if (t.getLatitude() == null)
            toRet.setNull(5, Types.FLOAT);
        else
            toRet.setFloat(5, t.getLatitude());
        if (t.getLongitude() == null)
            toRet.setNull(6, Types.FLOAT);
        else
            toRet.setFloat(6, t.getLongitude());
        return toRet;
    }
    
    @Override
    protected String getUpdateQueryStr() {
        return "UPDATE `" + getDatabasename() + "`.`" + getTablename()
                + "` SET `bookmaker` = ?, "
                + "`number` = ?, `address` = ?, `postcode` = ?, `latitude` = ?,"
                + " `longitude` = ? WHERE `id` = ? LIMIT 1";
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(BookmakerShop t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledUpdateStatement(conduit);
        toRet.setInt(1, (int)t.getBookmaker().getId());
        if (t.getNumber() == null)
            toRet.setNull(2, Types.INTEGER);
        else
            toRet.setInt(2, t.getNumber());
        toRet.setString(3, t.getAddress());
        toRet.setString(4, t.getPostCode());
        if (t.getLatitude() == null)
            toRet.setNull(5, Types.FLOAT);
        else
            toRet.setFloat(5, t.getLatitude());
        if (t.getLongitude() == null)
            toRet.setNull(6, Types.FLOAT);
        else
            toRet.setFloat(6, t.getLongitude());
        toRet.setInt(7, (int)t.getId());
        return toRet;
    }

}
