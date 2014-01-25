/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.factories;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.HaddockStatement;
import uk.co.etothepii.haddock.db.tables.Racecourse;
import uk.co.etothepii.haddock.db.tables.enumerations.RacecourseVendor;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class RacecourseFactory extends HaddockFactory<Racecourse> {

    private final Map<String, Integer> nameMap;
    private final Map<String, Integer> abvMap;

    private static final Logger LOG = Logger.getLogger(RacecourseFactory.class);

    @Override
    protected String getTablename() {
        return "racecourses";
    }

    private static RacecourseFactory factory;

    public static RacecourseFactory getFactory() {
        if (factory == null)
            factory = new RacecourseFactory();
        return factory;
    }

    public RacecourseFactory() {
        nameMap = new TreeMap<String, Integer>();
        abvMap = new TreeMap<String, Integer>();
    }

    @Override
    protected Racecourse build(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String abv = rs.getString("abv");
            String telephone = rs.getString("telephone");
            String webAddress = rs.getString("webAddress");
            String railway = rs.getString("railway");
            String description = rs.getString("description");
            Blob flatCourseBlob = rs.getBlob("flatCourse");
            BufferedImage flatCourse;
            if (flatCourseBlob != null) {
                try {
                    flatCourse = ImageIO.read(flatCourseBlob.getBinaryStream());
                }
                catch (IOException ioe) {
                    flatCourse = null;
                }
            }
            else {
                flatCourse = null;
            }
            Blob nationalHuntCourseBlob = rs.getBlob("flatCourse");
            BufferedImage nationalHuntCourse;
            if (nationalHuntCourseBlob != null) {
                try {
                    nationalHuntCourse = ImageIO.read(
                            nationalHuntCourseBlob.getBinaryStream());
                }
                catch (IOException ioe) {
                    nationalHuntCourse = null;
                }
            }
            else {
                nationalHuntCourse = null;
            }
            String country = rs.getString("country");
            String timeZoneStr = rs.getString("timeZone");
            TimeZone timeZone = timeZoneStr == null ? null :
                TimeZone.getTimeZone(timeZoneStr);
            if (LOG.isDebugEnabled()) {
                LOG.debug("timeZoneStr: " + timeZoneStr);
                if (timeZone != null)
                    LOG.debug("timeZone.getID(): " + timeZone.getID());
            }
            return new Racecourse(id, name, abv, telephone, webAddress, railway,
                    description, flatCourse, nationalHuntCourse, country,
                    timeZone);
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
    }

    public ArrayList<Racecourse> getAll() throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return getAll(conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public ArrayList<Racecourse> getAll(DatabaseConduit conduit)
            throws SQLException {
        PreparedStatement ps = null;
        String getAllQuery = "SELECT * FROM `" + getTablename()
                + "` ORDER BY `name`";
        ResultSet rs = null;
        try {
            ps = conduit.prepareStatement(getAllQuery);
            rs = ps.executeQuery();
            ArrayList<Racecourse> identities = new ArrayList<Racecourse>();
            while (rs.next())
                identities.add(build(rs));
            return identities;
        }
        finally {
            if (rs != null) {
                while (rs.next()) {}
                rs.close();
            }
            if (ps != null){
                ps.close();
            }
        }
    }

    @Override
    protected Racecourse cache(Racecourse t) {
        Racecourse toRet = super.cache(t);
        nameMap.put(toRet.getName(), (int)toRet.getId());
        if (toRet.getAbv() != null)
            abvMap.put(toRet.getAbv(), (int)toRet.getId());
        return toRet;
    }



    @Override
    protected String getInsertQueryStr() {
        return "INSERT INTO `" + getDatabasename() + "`.`" + getTablename()
                + "` (`id`, `name`, `abv`, `telephone`, `webAddress`, "
                + "`railway`, `description`, `flatCourse`, "
                + "`nationalHuntCourse`, `country`, `timeZone`) VALUES ("
                + "NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    }

    @Override
    protected PreparedStatement prepareInsertStatement(Racecourse t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        toRet.setString(1, t.getName());
        toRet.setString(2, t.getAbv());
        toRet.setString(3, t.getTelephone());
        toRet.setString(4, t.getWebAddress());
        toRet.setString(5, t.getRailway());
        toRet.setString(6, t.getDescription());
        if (t.getFlatCourse() == null) {
            toRet.setNull(7, Types.BLOB);
        }
        else {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(t.getFlatCourse(), "jpeg", os);
                ByteArrayInputStream is = new ByteArrayInputStream(
                        os.toByteArray());
                toRet.setBlob(7, is);
            }
            catch (IOException ioe) {
                toRet.setNull(7, Types.BLOB);
            }
        }
        if (t.getNationalHuntCourse() == null) {
            toRet.setNull(8, Types.BLOB);
        }
        else {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(t.getNationalHuntCourse(), "jpeg", os);
                ByteArrayInputStream is = new ByteArrayInputStream(
                        os.toByteArray());
                toRet.setBlob(8, is);
            }
            catch (IOException ioe) {
                toRet.setNull(8, Types.BLOB);
            }
        }
        toRet.setString(9, t.getCountry());
        if (t.getTimezone() == null) {
            toRet.setNull(10, Types.VARCHAR);
        }
        else
            toRet.setString(10, t.getTimezone().getID());
        return toRet;
    }

    @Override
    protected String getUpdateQueryStr() {
        return "UPDATE `" + getDatabasename() + "`.`" + getTablename()
                + "` SET `name` = ?, `abv` = ?, `telephone` = ?, "
                + "`webAddress` = ?, `railway` = ?, `description` = ?, "
                + "`flatCourse` = ?, `nationalHuntCourse` = ?, `country` = ?, "
                + "`timeZone` = ? WHERE `id` = ? LIMIT 1;";
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(Racecourse t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        toRet.setString(1, t.getName());
        toRet.setString(2, t.getAbv());
        toRet.setString(3, t.getTelephone());
        toRet.setString(4, t.getWebAddress());
        toRet.setString(5, t.getRailway());
        toRet.setString(6, t.getDescription());
        if (t.getFlatCourse() == null) {
            toRet.setNull(7, Types.BLOB);
        }
        else {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(t.getFlatCourse(), "jpeg", os);
                ByteArrayInputStream is = new ByteArrayInputStream(
                        os.toByteArray());
                toRet.setBlob(7, is);
            }
            catch (IOException ioe) {
                toRet.setNull(7, Types.BLOB);
            }
        }
        if (t.getNationalHuntCourse() == null) {
            toRet.setNull(8, Types.BLOB);
        }
        else {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(t.getNationalHuntCourse(), "jpeg", os);
                ByteArrayInputStream is = new ByteArrayInputStream(
                        os.toByteArray());
                toRet.setBlob(8, is);
            }
            catch (IOException ioe) {
                toRet.setNull(8, Types.BLOB);
            }
        }
        toRet.setString(9, t.getCountry());
        if (t.getTimezone() == null) {
            toRet.setNull(10, Types.VARCHAR);
        }
        else
            toRet.setString(10, t.getTimezone().getID());
        toRet.setInt(11, (int)t.getId());
        return toRet;
    }

    private final HaddockStatement getFromNameQuery = new HaddockStatement(
            "SELECT * FROM `racecourses` WHERE `name` LIKE ? LIMIT 1", false);

    private final HaddockStatement getFromNameAndCountryQuery = new HaddockStatement(
            "SELECT * FROM `racecourses` WHERE `name` LIKE ? AND "
            + "`country` LIKE ? LIMIT 1", false);

    private final HaddockStatement getFromAliasQuery = new HaddockStatement(
            "SELECT `r`.* FROM `racecourses` as `r`, "
            + "`racecourseAliases` as `ra` WHERE `r`.`id` = `ra`.`racecourse` "
            + "AND `ra`.`alias` LIKE ? LIMIT 1", false);

    private final HaddockStatement getFromAliasOrNameQuery =
            new HaddockStatement("SELECT r.* FROM racecourses r LEFT JOIN "
            + "racecourseAliases a  ON (r.id = a.racecourse "
            + "AND a.vendor = ?) WHERE a.alias LIKE ? OR r.name LIKE ?",
            false);

    public Racecourse getFromName(String name) throws SQLException {
        Racecourse temp = getFromNameDirect(getFromNameQuery, name);
        if (temp == null) {
            temp = getFromNameDirect(getFromAliasQuery, name);
        }
        return temp;
    }

    public Racecourse getFromName(String name, RacecourseVendor vendor)
            throws SQLException{
        DatabaseConduit conduit = takeConduit();
		PreparedStatement ps = null;
		ResultSet rs = null;
        try {
            ps = getFromAliasOrNameQuery.getStatement(conduit);
            if (vendor != null)
                ps.setString(1, vendor.name());
            else
                ps.setNull(1, Types.VARCHAR);
            ps.setString(2, name);
            ps.setString(3, name);
            if (LOG.isDebugEnabled())
                LOG.debug(ps.toString());
            rs = ps.executeQuery();
            if (rs.next() && !rs.next() && rs.previous())
                return build(rs);
            return null;
        }
        finally {
			if (rs != null) {
				while (rs.next());
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
            releaseConduit(conduit);
        }
    }

    public Racecourse getFromNameAndCountry(String name, String country)
            throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return getFromNameAndCountry(name, country, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    private Racecourse getFromNameAndCountry(String name, String country,
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement ps = getFromNameAndCountryQuery.getStatement(conduit);
        try {
            ps.setString(1, name);
            ps.setString(2, country);
        }
        catch (NullPointerException npe) {
            LOG.error("ps: " + (ps == null ? "null" : ps.toString()));
            LOG.error("name: " + (name == null ? "null" : name.toString()));
            throw npe;
        }
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            if (rs.next())
                return build(rs);
            else
                return null;
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
    }

    private Racecourse getFromNameDirect(HaddockStatement hs, String name)
            throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return getFromNameDirect(hs, name, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    private Racecourse getFromNameDirect(HaddockStatement hs, String name,
            DatabaseConduit conduit) throws SQLException {
        Integer id = nameMap.get(name);
        if (id != null)
            return getFromId(id);
        PreparedStatement ps = hs.getStatement(conduit);
        try {
            ps.setString(1, name);
        }
        catch (NullPointerException npe) {
            LOG.error("ps: " + (ps == null ? "null" : ps.toString()));
            LOG.error("name: " + (name == null ? "null" : name.toString()));
            throw npe;
        }
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            if (rs.next())
                return build(rs);
            else
                return null;
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
    }

    private final HaddockStatement getFromAbvQuery = new HaddockStatement(
            "SELECT * FROM `racecourses` WHERE `abv` LIKE ? LIMIT 1", false);

    public Racecourse getFromAbv(String abv) throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return getFromAbv(abv, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public Racecourse getFromAbv(String abv, DatabaseConduit conduit)
            throws SQLException {
        Integer id = abvMap.get(abv);
        if (id != null)
            return getFromId(id);
        PreparedStatement ps = getFromAbvQuery.getStatement(conduit);
        ps.setString(1, abv);
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            if (rs.next())
                return build(rs);
            else
                return null;
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
    }

}
