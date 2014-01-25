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
import uk.co.etothepii.haddock.db.tables.Identity;

/**
 *
 * @author jrrpl
 */
public class IdentityFactory extends HaddockFactory<Identity> {

	private static final Logger LOG = Logger.getLogger(IdentityFactory.class);

    @Override
    protected String getTablename() {
        return "identities";
    }

    private static IdentityFactory factory;

    public static IdentityFactory getFactory() {
        if (factory == null)
            factory = new IdentityFactory();
        return factory;
    }

    private IdentityFactory() {}

    @Override
    protected Identity build(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            String surname = rs.getString("surname");
            String firstName = rs.getString("firstName");
            String middleNames = rs.getString("middleNames");
            return new Identity(id, surname, firstName, middleNames);
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
    }

    public ArrayList<Identity> getAll() throws SQLException {
        String getAllQuery = "SELECT * FROM `identities` ORDER BY `surname`";
        DatabaseConduit conduit = takeConduit();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conduit.prepareStatement(getAllQuery);
            rs = ps.executeQuery();
            ArrayList<Identity> identities = new ArrayList  <Identity>();
            while (rs.next())
                identities.add(build(rs));
            return identities;
        }
        finally {
            if (rs != null) {
                try {
                    while (rs.next()) {}
                    rs.close();
                }
                catch (SQLException sqle) {
					throw new RuntimeException(sqle);
				}
            }
            if (ps != null){
                try{
                    ps.close();
                }catch (SQLException e){
					throw new RuntimeException(e);
                }
            }
            releaseConduit(conduit);
        }
    }

    @Override
    protected String getInsertQueryStr() {
        return "INSERT INTO `" + getDatabasename() + "`.`" + getTablename()
                + "` (`id`, `surname`, "
                + "`firstName`, `middleNames`) VALUES (NULL, ?, ?, ?);";
    }

    @Override
    protected PreparedStatement prepareInsertStatement(Identity t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        toRet.setString(1, t.getSurname());
        toRet.setString(2, t.getFirstName());
        toRet.setString(3, t.getMiddleNames());
        return toRet;
    }

    @Override
    protected String getUpdateQueryStr() {
        return "UPDATE `" + getDatabasename() + "`.`" + getTablename()
                + "` SET `surname` = ?, "
                + "`firstName` = ?, `middleNames` = ? WHERE `id` = ? "
                + "LIMIT 1;";
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(Identity t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledUpdateStatement(conduit);
        toRet.setString(1, t.getSurname());
        toRet.setString(2, t.getFirstName());
        toRet.setString(3, t.getMiddleNames());
        toRet.setInt(4, (int)t.getId());
        return toRet;
    }

}
