/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.factories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.HaddockStatement;
import uk.co.etothepii.haddock.db.tables.Alias;
import uk.co.etothepii.haddock.db.tables.Horse;
import uk.co.etothepii.haddock.db.tables.enumerations.Vendor;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class HorseFactory extends HaddockFactory<Horse> {

    private static final Logger LOG = Logger.getLogger(HorseFactory.class);
    private static final Logger LOG_SAV =
            Logger.getLogger(HorseFactory.class.getName().concat(".sav"));

    static {
        LOG.debug("AliasFactory debugging");
    }

    @Override
    protected String getTablename() {
        return "horses";
    }

    private static HorseFactory factory;

    public static synchronized HorseFactory getFactory() {
        if (factory == null)
            factory = new HorseFactory();
        return factory;
    }


    private HorseFactory() {}

    @Override
    protected Horse build(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            int yob = rs.getInt(Horse.YOB.name);
            int aliasId = rs.getInt(Horse.ALIAS.name);
            Alias alias = AliasFactory.getFactory().getFromId(aliasId);
            return new Horse(id, alias, yob);
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
    }

    @Override
    protected String getInsertQueryStr() {
        return "INSERT INTO `" + getDatabasename() + "`.`" + getTablename()
                + "` (`id`, `" + Horse.ALIAS.name + "`, `" + Horse.YOB.name + "`) VALUES ("
                + "NULL, ?, ?);";
    }

    @Override
    protected PreparedStatement prepareInsertStatement(Horse t,
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        if (t.getAlias() == null)
            toRet.setNull(1, Types.INTEGER);
        else
            toRet.setLong(1, t.getAlias().getId());
        toRet.setInt(2, t.getYearOfBirth());
        return toRet;
    }

    @Override
    protected String getUpdateQueryStr() {
        return "UPDATE `" + getDatabasename() + "`.`" + getTablename()
                + "` SET `" + Horse.ALIAS.name + "` = ?, `alias` = ?, `vendor` = ? "
                + "WHERE `id` = ? LIMIT 1;";
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(Horse t,
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledUpdateStatement(conduit);
        if (t.getAlias() == null)
            toRet.setNull(1, Types.INTEGER);
        else
            toRet.setLong(1, t.getAlias().getId());
        toRet.setInt(2, t.getYearOfBirth());
        toRet.setLong(3, t.getId());
        return toRet;
    }
    
    public Horse getFromNameAndAgeOnDate(String name, int age, Date date) {
        DatabaseConduit conduit = takeConduit();
        try {
            return getFromNameAndAgeOnDate(name, age, date, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }
    
    public Horse getFromNameAndAgeOnDate(String name, int age, Date date, 
            DatabaseConduit conduit) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        return getFromNameAndYob(name, cal.get(Calendar.YEAR) - age, conduit);
    }

    public Horse getFromNameAndYob(String name, int yob) {
        DatabaseConduit conduit = takeConduit();
        try {
            return getFromNameAndYob(name, yob, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public static String getBestBetfairGuess(String name) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            if (i < name.length() - 1 && name.charAt(i + 1) == '(')
                return sb.toString();
            else if (name.charAt(i) != '\'') sb.append(name.charAt(i));
        }
        return sb.toString();
    }

    HaddockStatement getFromName = new HaddockStatement("SELECT * FROM "
            + getTablename() + " WHERE `alias` = ? LIMIT 1", false);

    HaddockStatement getFromAliasAndVendor = new HaddockStatement("SELECT h.* FROM "
            + "`aliases` a, aliases b, horses h WHERE "
            + "a.selection = b.selection AND a.vendor = ? AND "
            + "b.vendor = 'TIMEFORM' AND b.id = h.alias AND a.id = ?", false);

    public Horse getFromAliasAndVendor(Alias a, Vendor v) {
        DatabaseConduit conduit = takeConduit();
        try {
            return getFromAliasAndVendor(a, v, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public Horse getFromAliasAndVendor(Alias a, Vendor v,
            DatabaseConduit conduit) {
		PreparedStatement ps = null;
		ResultSet rs = null;
        try {
            ps = getFromAliasAndVendor.getStatement(conduit);
            ps.setString(1, v.name());
            ps.setLong(2, a.getId());
            rs = ps.executeQuery();
            if (rs.next()) {
                Horse h = build(rs);
                if (!rs.next()) return h;
            }
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
		finally {
			try {
				if (rs != null) {
					while (rs.next());
					rs.close();
				}
				if (ps != null) {
					ps.close();	
				}
			}
			catch (SQLException sqle) {
				throw new RuntimeException(sqle);
			}
		}
        return null;
    }

    public Horse getFromNameAndYob(String name, int yob,
            DatabaseConduit conduit) {
		ResultSet rs = null;
		PreparedStatement ps = null;
        try {
            Alias alias = AliasFactory.getFactory().getFromAliasAndVendor(
                    name, Vendor.TIMEFORM);
            if (alias == null) {
                Alias bfAlias = AliasFactory.getFactory().getFromAliasAndVendor(
                        getBestBetfairGuess(name), Vendor.BETFAIR_NAME);
                alias = new Alias(bfAlias == null ? null :
                    bfAlias.getSelection(), name, Vendor.TIMEFORM);
                AliasFactory.getFactory().save(alias);
            }
            ps = getFromName.getStatement(conduit);
            ps.setLong(1, alias.getId());
            LOG.debug(ps);
            rs = ps.executeQuery();
            Horse horse = rs.next() ? build(rs) : null;
            if (horse == null) {
                horse = new Horse(alias, yob);
                save(horse);
            }
            cache(horse);
            return horse;
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
		finally {
			try {
				if (rs != null) {
					while (rs.next());
					rs.close();
				}
				if (ps != null) {
					ps.close();	
				}
			}
			catch (SQLException sqle) {
				throw new RuntimeException(sqle);
			}
		}
    }
}
