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
import uk.co.etothepii.haddock.db.HaddockStatement;
import uk.co.etothepii.haddock.db.tables.BookmakerAccount;
import uk.co.etothepii.haddock.db.tables.BookmakerAccountBalance;

/**
 *
 * @author jrrpl
 */
public class BookmakerAccountBalanceFactory extends 
        HaddockFactory<BookmakerAccountBalance> {

    @Override
    protected String getTablename() {
        return "bookmakerAccountBalances";
    }

    private static final Logger LOG =
            Logger.getLogger(BookmakerAccountBalanceFactory.class);

    private static BookmakerAccountBalanceFactory factory;

    private BookmakerAccountBalanceFactory() {}

    public static BookmakerAccountBalanceFactory getFactory() {
        if (factory == null)
            factory = new BookmakerAccountBalanceFactory();
        return factory;
    }

    @Override
    protected BookmakerAccountBalance build(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            int bookmakerAccountId = rs.getInt("bookmakerAccount");
            int balance = rs.getInt("balance");
            Date time = rs.getTimestamp("time");
            BookmakerAccount bookmakerAccount =
                    BookmakerAccountFactory.getFactory().getFromId(
                    bookmakerAccountId);
            return new BookmakerAccountBalance(id, bookmakerAccount,
                    balance, time);
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
    }

    @Override
    protected String getInsertQueryStr() {
        return "INSERT INTO `" + getDatabasename() + "`.`" + getTablename()
                + "` (`id`, "
                + "`bookmakerAccount`, `balance`, `time`) VALUES "
                + "(NULL, ?, ?, ?); ";
    }

    @Override
    protected PreparedStatement prepareInsertStatement(
            BookmakerAccountBalance t, DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        toRet.setInt(1, (int)t.getBookmakerAccount().getId());
        toRet.setInt(2, t.getBalance());
        toRet.setTimestamp(3, new Timestamp(t.getTime().getTime()));
        return toRet;
    }

    @Override
    protected String getUpdateQueryStr() {
        return "UPDATE `" + getDatabasename() + "`.`" + getTablename()
                + "` SET `bookmakerAccount` = ?, "
                + "`balance` = ?, `time` = ? WHERE `id` = ? LIMIT 1; ";
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(
            BookmakerAccountBalance t, DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledUpdateStatement(conduit);
        toRet.setInt(1, (int)t.getBookmakerAccount().getId());
        toRet.setInt(2, t.getBalance());
        toRet.setTimestamp(3, new Timestamp(t.getTime().getTime()));
        toRet.setInt(4, (int)t.getId());
        return toRet;
    }

    private HaddockStatement selectMostRecentFromAccountStatement =
            new HaddockStatement("SELECT * FROM `" + getDatabasename() 
                + "`.`" + getTablename() + "` WHERE `bookmakerAccount` = ? "
                + "ORDER BY `time` DESC LIMIT 1", false);

    public BookmakerAccountBalance getMostRecentFromAccount(
            BookmakerAccount bookmakerAccount) throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return getMostRecentFromAccount(bookmakerAccount, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public BookmakerAccountBalance getMostRecentFromAccount(
            BookmakerAccount bookmakerAccount, DatabaseConduit conduit)
            throws SQLException {
        if (bookmakerAccount.getId() <= 0) return null;
        PreparedStatement ps =
                selectMostRecentFromAccountStatement.getStatement(conduit);
        ps.setInt(1, (int)bookmakerAccount.getId());
        if (LOG.isDebugEnabled()){
            LOG.debug(ps);
        }
        ResultSet rs = null;
        BookmakerAccountBalance bab = null;
        try {
            rs = ps.executeQuery();
            if (rs.next())
                bab = build(rs);
            else
                bab = null;
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
        return bab;
    }
}
