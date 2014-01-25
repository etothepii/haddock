/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.factories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.HaddockStatement;
import uk.co.etothepii.haddock.db.tables.Alias;
import uk.co.etothepii.haddock.db.tables.Nonrunner;
import uk.co.etothepii.haddock.db.tables.Race;
import uk.co.etothepii.haddock.db.tables.enumerations.NonrunnerResponse;

/**
 *
 * @author jrrpl
 */
public class NonrunnerFactory extends HaddockFactory<Nonrunner> {

    private static final Logger LOG = Logger.getLogger(NonrunnerFactory.class);

    private final Map<Long, Map<Long, Long>> aliasRaceMap;

    @Override
    protected String getTablename() {
        return "nonrunners";
    }

    private static NonrunnerFactory factory;

    public static NonrunnerFactory getFactory() {
        if (factory == null)
            factory = new NonrunnerFactory();
        return factory;
    }

    private NonrunnerFactory() {
        aliasRaceMap = new TreeMap<Long, Map<Long, Long>>();
    }

    @Override
    protected Nonrunner cache(Nonrunner t) {
        Nonrunner n = super.cache(t);
        synchronized (aliasRaceMap) {
            LOG.debug(aliasRaceMap);
            LOG.debug(t);
            LOG.debug(t.getAlias());
            LOG.debug(t.getAlias().getId());
            Map<Long, Long> map = aliasRaceMap.get(t.getAlias().getId());
            if (map == null) {
                map = new TreeMap<Long, Long>();
                aliasRaceMap.put(t.getAlias().getId(), map);
            }
            map.put(t.getRace().getId(), t.getId());
        }
        return n;
    }

    @Override
    protected Nonrunner build(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            int aliasId = rs.getInt("alias");
            int raceId = rs.getInt("race");
            Date found = rs.getTimestamp("found");
            Date bfFound = rs.getTimestamp("bfFound");
            Date declared = rs.getTimestamp("declared");
            Date bfDeclared = rs.getTimestamp("bfDeclared");
            String bfRespString = rs.getString("bfNRResp");
            NonrunnerResponse bfNRResp = bfRespString == null ? null :
                    NonrunnerResponse.valueOf(bfRespString);
            return new Nonrunner(id,
                    AliasFactory.getFactory().getFromId(aliasId),
                    RaceFactory.getFactory().getFromId(raceId), found, bfFound,
                    declared, bfDeclared, bfNRResp);
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
    }

    @Override
    protected String getInsertQueryStr() {
        String toRet = "INSERT INTO `" + getDatabasename() + "`.`" + getTablename()
                + "` (`id`, `alias`, `race`, `found`, `bfFound`, `declared`, "
                + "`bfDeclared`, `bfNRResp`) "
                + "VALUES (NULL, ?, ?, ?, ?, ?, ?, ?);";
        LOG.debug(toRet);
        return toRet;
    }

    @Override
    protected PreparedStatement prepareInsertStatement(Nonrunner t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        toRet.setInt(1, (int)t.getAlias().getId());
        toRet.setInt(2, (int)t.getRace().getId());
        toRet.setTimestamp(3, new Timestamp(t.getFound().getTime()));
        if (t.getBFFound() == null)
            toRet.setNull(4, Types.TIMESTAMP);
        else
            toRet.setTimestamp(4, new Timestamp(t.getBFFound().getTime()));
        LOG.debug("declared: " + t.getDeclared().toString());
        toRet.setTimestamp(5, new Timestamp(t.getDeclared().getTime()));
        if (t.getBFDeclared() == null)
            toRet.setNull(6, Types.TIMESTAMP);
        else
            toRet.setTimestamp(6, new Timestamp(t.getBFDeclared().getTime()));
        if (t.getNonrunnerResponse() == null)
            toRet.setNull(7, Types.TIMESTAMP);
        else
            toRet.setString(7, t.getNonrunnerResponse().toString());
        return toRet;
    }

    @Override
    protected String getUpdateQueryStr() {
        return "UPDATE `" + getDatabasename() + "`.`" + getTablename()
                + "` SET `bfFound` = ?, `bfDeclared` = ?, "
                + "`bfNRResp` = ? WHERE `id` = ? LIMIT 1;";
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(Nonrunner t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledUpdateStatement(conduit);
        if (t.getBFFound() != null)
            toRet.setTimestamp(1, new Timestamp(t.getBFFound().getTime()));
        else
            toRet.setNull(1, Types.TIMESTAMP);
        if (t.getBFDeclared() != null)
            toRet.setTimestamp(2, new Timestamp(t.getBFDeclared().getTime()));
        else
            toRet.setNull(2, Types.TIMESTAMP);
        if (t.getNonrunnerResponse() == null)
            toRet.setNull(3, Types.VARCHAR);
        else
            toRet.setString(3, t.getNonrunnerResponse().toString());
        toRet.setInt(4, (int)t.getId());
        return toRet;
    }

    private HaddockStatement  getFromAliasAndRaceQuery = new HaddockStatement(
            "SELECT * FROM `" + getDatabasename() + "`.`" + getTablename() +
            "` WHERE `alias` = ? AND `race` = ? LIMIT 1", true);

    public Nonrunner getFromAliasAndRace(Alias alias, Race race) throws
            SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return getFromAliasAndRace(alias, race, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public Nonrunner getFromAliasAndRace(Alias alias, Race race,
            DatabaseConduit conduit) throws SQLException {
        if (alias == null || race == null)
            return null;
        Nonrunner toRet = null;
        Long id = null;
        synchronized (aliasRaceMap) {
            Map<Long, Long> raceMap =
                    aliasRaceMap.get(alias.getId());
                if (raceMap != null) {
                    id = raceMap.get(race.getId());
                }
        }
        if (id != null)
            toRet = getFromId(id);
        if (toRet != null)
            return toRet;
        PreparedStatement ps = getFromAliasAndRaceQuery.getStatement(conduit);
        ps.setInt(1, (int)alias.getId());
        ps.setInt(2, (int)race.getId());
        ResultSet rs = null;
        LOG.debug(ps);
        try {
            rs = ps.executeQuery();
            if (rs.next()) {
                toRet = cache(build(rs));
            }
            if (!rs.next())
                return toRet;
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

    private final HaddockStatement getActiveNonrunnersCountQuery =
            new HaddockStatement("SELECT COUNT(*) FROM (SELECT `nr`.`id` "
            + "FROM `nonrunners` as `nr`, `races` as `r` WHERE "
            + "`r`.`scheduled` > NOW() AND `nr`.`race` = `r`.`id`) as  `temp`",
            false);

    public int getActiveNonrunnersCount() throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return getActiveNonrunnersCount(conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public int getActiveNonrunnersCount(DatabaseConduit conduit)
            throws SQLException {
        PreparedStatement ps = 
                getActiveNonrunnersCountQuery.getStatement(conduit);
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
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

    private final HaddockStatement getActiveNonrunnersSync =
            new HaddockStatement("SELECT `nr`.* "
            + "FROM `nonrunners` as `nr`, `races` as `r` WHERE "
            + "`r`.`scheduled` > NOW() AND `nr`.`race` = `r`.`id`", false);

    public Nonrunner[] getActiveNonrunners() throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return getActiveNonrunners(conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public Nonrunner[] getActiveNonrunners(DatabaseConduit conduit)
            throws SQLException {
        PreparedStatement ps = getActiveNonrunnersSync.getStatement(conduit);
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            ArrayList<Nonrunner> nonrunners = new ArrayList<Nonrunner>();
            while (rs.next()) {
                Nonrunner nr = build(rs);
                LOG.debug("nr: " + (nr == null ? "null" : nr.toString()));
                nonrunners.add(nr);
            }
            return (Nonrunner[])nonrunners.toArray(new Nonrunner[0]);
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
