/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.factories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.tables.BankAccount;
import uk.co.etothepii.haddock.db.tables.Identity;

/**
 *
 * @author jrrpl
 */
public class BankAccountFactory extends HaddockFactory<BankAccount> {

    @Override
    protected String getTablename() {
        return "bankAccounts";
    }

    private static BankAccountFactory factory;

    public BankAccountFactory() {}

    public static BankAccountFactory getFactory() {
        if (factory == null)
            factory = new BankAccountFactory();
        return factory;
    }

    @Override
    protected BankAccount build(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            int identityId = rs.getInt("identity");
            String bank = rs.getString("bank");
            String name = rs.getString("name");
            String number = rs.getString("number");
            String sortCode = rs.getString("sortCode");
            Identity identity = IdentityFactory.getFactory(
                    ).getFromId(identityId);
            return new BankAccount(id, identity, bank, name,
                    number, sortCode);
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
    }

    @Override
    protected String getInsertQueryStr() {
        return "INSERT INTO `" + getDatabasename() + "`.`" + getTablename()
                + "` (`id`, `identity`, "
                + "`name`, `number`, `sortCode`) VALUES (NULL, ?, ?, ?, ?);";
    }

    @Override
    protected PreparedStatement prepareInsertStatement(BankAccount t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        toRet.setInt(1, (int)t.getIdentity().getId());
        toRet.setString(2, t.getBank());
        toRet.setString(3, t.getName());
        toRet.setString(4, t.getNumber());
        toRet.setString(5, t.getSortCode());
        return toRet;
    }

    @Override
    protected String getUpdateQueryStr() {
        return "UPDATE `" + getDatabasename() + "`.`" + getTablename()
                + "` SET `identity` = ?, "
                + "`name` = ?, `number` = ?, `sortCode` = ? WHERE `id` = ? "
                + "LIMIT 1;";
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(BankAccount t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledUpdateStatement(conduit);
        toRet.setInt(1, (int)t.getIdentity().getId());
        toRet.setString(2, t.getBank());
        toRet.setString(3, t.getName());
        toRet.setString(4, t.getNumber());
        toRet.setString(5, t.getSortCode());
        toRet.setInt(6, (int)t.getId());
        return toRet;
    }
}
