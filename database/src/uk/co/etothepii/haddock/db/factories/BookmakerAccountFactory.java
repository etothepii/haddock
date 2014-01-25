/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.factories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.HaddockStatement;
import uk.co.etothepii.haddock.db.tables.Bookmaker;
import uk.co.etothepii.haddock.db.tables.BookmakerAccount;
import uk.co.etothepii.haddock.db.tables.Identity;

/**
 *
 * @author jrrpl
 */
public class BookmakerAccountFactory extends 
        HaddockFactory<BookmakerAccount> {

    @Override
    protected String getTablename() {
        return "bookmakerAccounts";
    }

    private static final Logger LOG =
            Logger.getLogger(BookmakerAccountFactory.class);

    private static BookmakerAccountFactory factory;

    public static BookmakerAccountFactory getFactory() {
        if (factory == null)
            factory = new BookmakerAccountFactory();
        return factory;
    }

    private BookmakerAccountFactory() {}

    @Override
    protected BookmakerAccount build(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            int bookmakerId = rs.getInt("bookmaker");
            int identityId = rs.getInt("identity");
            String username = rs.getString("username");
            String password = rs.getString("password");
            Date opened = rs.getTimestamp("opened");
            Date limited = rs.getTimestamp("limited");
            Date abandoned = rs.getTimestamp("abandoned");
            Date closed = rs.getTimestamp("closed");
            Bookmaker bookmaker = BookmakerFactory.getFactory().getFromId(bookmakerId);
            Identity identity = IdentityFactory.getFactory().getFromId(identityId);
            return new BookmakerAccount(id,
                    bookmaker, identity, username, password, opened, limited,
                    abandoned, closed);
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
    }

    @Override
    protected String getInsertQueryStr() {
        return "INSERT INTO `" + getDatabasename() + "`.`" + getTablename()
                + "` (`id`, `bookmaker`, `identity`, "
                + "`username`, `password`, `opened`, `limited`, `abandoned`, "
                + "`closed`) VALUES "
                + "(NULL, ?, ?, ?, ?, ?, ?, ?, ?); ";
    }

    @Override
    protected PreparedStatement prepareInsertStatement(
            BookmakerAccount t, DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        LOG.debug(toRet);
        toRet.setInt(1, (int)t.getBookmaker().getId());
        toRet.setInt(2, (int)t.getIdentity().getId());
        toRet.setString(3, t.getUsername());
        toRet.setString(4, t.getPassword());
        if (t.getOpened() != null)
            toRet.setDate(5, new java.sql.Date(t.getOpened().getTime()));
        else
            toRet.setNull(5, Types.DATE);
        if (t.getLimited() != null)
            toRet.setDate(6, new java.sql.Date(t.getLimited().getTime()));
        else
            toRet.setNull(6, Types.DATE);
        if (t.getAbandoned() != null)
            toRet.setDate(7, new java.sql.Date(t.getAbandoned().getTime()));
        else
            toRet.setNull(7, Types.DATE);
        if (t.getClosed() != null)
            toRet.setDate(8, new java.sql.Date(t.getClosed().getTime()));
        else
            toRet.setNull(8, Types.DATE);
        return toRet;
    }

    @Override
    protected String getUpdateQueryStr() {
        return "UPDATE `" + getDatabasename() + "`.`" + getTablename()
                + "` SET `bookmaker` = ?, `identity` = ?, "
                + "`username` = ?, `password` = ?, `opened` = ? , "
                + "`limited` = ?, `abandoned` = ?, `closed` = ? "
                + "WHERE `id` = ? LIMIT 1;";
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(
            BookmakerAccount t, DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledUpdateStatement(conduit);
        toRet.setInt(1, (int)t.getBookmaker().getId());
        toRet.setInt(2, (int)t.getIdentity().getId());
        toRet.setString(3, t.getUsername());
        toRet.setString(4, t.getPassword());
        if (t.getOpened() != null)
            toRet.setDate(5, new java.sql.Date(t.getOpened().getTime()));
        else
            toRet.setNull(5, Types.DATE);
        if (t.getLimited() != null)
            toRet.setDate(6, new java.sql.Date(t.getLimited().getTime()));
        else
            toRet.setNull(6, Types.DATE);
        if (t.getAbandoned() != null)
            toRet.setDate(7, new java.sql.Date(t.getAbandoned().getTime()));
        else
            toRet.setNull(7, Types.DATE);
        if (t.getClosed() != null)
            toRet.setDate(8, new java.sql.Date(t.getClosed().getTime()));
        else
            toRet.setNull(8, Types.DATE);
        toRet.setInt(9, (int)t.getId());
        return toRet;
    }

    private HaddockStatement selectStatementFromIdentityAndBookmakerQuery =
            new HaddockStatement(getSelectFromIdentityAndBookmakerQueryStr(),
            false);
    
    private String getSelectFromIdentityAndBookmakerQueryStr() {
        return "SELECT * FROM `" + getDatabasename() + "`.`" + getTablename()
                + "` WHERE `identity` = ? AND "
                + "`bookmaker` = ? LIMIT 1";
    }

    public BookmakerAccount getFromIdentityAndBookmaker(Identity identity,
            Bookmaker bookmaker) throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return getFromIdentityAndBookmaker(identity, bookmaker, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public BookmakerAccount getFromIdentityAndBookmaker(Identity identity,
            Bookmaker bookmaker, DatabaseConduit conduit) throws SQLException {
        LOG.debug("Identity: " + identity.toString());
        LOG.debug("Bookmaker: " + bookmaker.toString());
        PreparedStatement ps = 
                selectStatementFromIdentityAndBookmakerQuery.getStatement(conduit);
        ps.setInt(1, (int)identity.getId());
        ps.setInt(2, (int)bookmaker.getId());
        LOG.debug(ps);
        BookmakerAccount ba = null;
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            if (rs.next())
                ba = build(rs);
            else
                ba = null;
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
        return ba;
    }

}
