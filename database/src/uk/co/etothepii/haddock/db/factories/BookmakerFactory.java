/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.factories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.tables.Bookmaker;

/**
 *
 * @author jrrpl
 */
public class BookmakerFactory extends HaddockFactory<Bookmaker> {

    private static final Logger LOG = Logger.getLogger(BookmakerFactory.class);

    @Override
    protected String getTablename() {
        return "bookmakers";
    }
    
    private static BookmakerFactory factory;

    private BookmakerFactory() {}
    
    public static BookmakerFactory getFactory() {
        if (factory == null)
            factory = new BookmakerFactory();
        return factory;
    }

    @Override
    protected Bookmaker build(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String abv = rs.getString("abv");
            return new Bookmaker(id, name, abv);
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
    }

    public ArrayList<Bookmaker> getAll() throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return getAll(conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public ArrayList<Bookmaker> getAll(DatabaseConduit conduit) throws SQLException {
        String getAllQuery = "SELECT * FROM `bookmakers` ORDER by `"
                + Bookmaker.NAME.name + "` ASC";
        PreparedStatement ps = null;
        ArrayList<Bookmaker> bookmakers = new ArrayList<Bookmaker>();
        ResultSet rs = null;
        try {
            ps = conduit.prepareStatement(getAllQuery);
            rs = ps.executeQuery();
            while (rs.next())
                bookmakers.add(build(rs));
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
        return bookmakers;
    }
    
    @Override
    protected String getInsertQueryStr() {
        return "INSERT INTO `" + getDatabasename() + "`.`" + getTablename()
                + "` (`id`, `name`, `abv`) "
                + "VALUES (NULL, ?, ?);";
    }

    @Override
    protected PreparedStatement prepareInsertStatement(Bookmaker t,
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        toRet.setString(1, t.getName());
        toRet.setString(2, t.getAbv());
        return toRet;
    }
    
    @Override
    protected String getUpdateQueryStr() {
        return "UPDATE `" + getDatabasename() + "`.`" + getTablename()
                + "` SET `name` = ?, `abv` = ? "
                + "WHERE `id` = ? LIMIT 1";
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(Bookmaker t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledUpdateStatement(conduit);
        toRet.setString(1, t.getName());
        toRet.setString(2, t.getAbv());
        toRet.setInt(3, (int)t.getId());
        return toRet;
    }
}
