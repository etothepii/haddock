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
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.HaddockStatement;
import uk.co.etothepii.haddock.db.tables.Alias;
import uk.co.etothepii.haddock.db.tables.BFHistoricalHorseracingDatum;
import uk.co.etothepii.haddock.db.tables.Selection;
import uk.co.etothepii.haddock.db.tables.enumerations.InPlayFlag;
import uk.co.etothepii.haddock.db.tables.enumerations.Vendor;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class BFHistoricalHorseracingDatumFactory extends 
        HaddockFactory<BFHistoricalHorseracingDatum> {

    private static final Logger LOG =
            Logger.getLogger(BFHistoricalHorseracingDatumFactory.class);
    private static final Logger LOG_SAV =
            Logger.getLogger(BFHistoricalHorseracingDatumFactory.class.getName().concat(".sav"));

    @Override
    protected String getTablename() {
        return "bfHistoricalHorseracingData";
    }

    private static BFHistoricalHorseracingDatumFactory factory;

    public static synchronized BFHistoricalHorseracingDatumFactory getFactory() {
        if (factory == null)
            factory = new BFHistoricalHorseracingDatumFactory();
        return factory;
    }

    private BFHistoricalHorseracingDatumFactory() {}

    @Override
    protected BFHistoricalHorseracingDatum build(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            int raceId = rs.getInt("race");
            int marketSelectionId = rs.getInt("marketSelection");
            double odds = rs.getDouble("odds");
            int numberOfBets = rs.getInt("numberOfBets");
            double volumeMatched = rs.getDouble("volumeMatched");
            Date lastTaken = rs.getTimestamp("lastTaken");
            Date firstTaken = rs.getTimestamp("firstTaken");
            InPlayFlag inPlay = InPlayFlag.valueOf(rs.getString("inPlay"));
            return new BFHistoricalHorseracingDatum(id,
                    RaceFactory.getFactory().getFromId(raceId),
                    MarketSelectionFactory.getFactory(
                    ).getFromId(marketSelectionId), odds, numberOfBets,
                    volumeMatched, lastTaken, firstTaken, inPlay);
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
    }

    @Override
    protected String getInsertQueryStr() {
        return "INSERT INTO `" + getDatabasename() + "`.`" + getTablename()
                + "` (`id`, `" + BFHistoricalHorseracingDatum.RACE.name + "`, "
                + "`" + BFHistoricalHorseracingDatum.MARKET_SELECTION.name
                + "`, `" + BFHistoricalHorseracingDatum.ODDS.name + "`, "
                + "`" + BFHistoricalHorseracingDatum.NUMBER_OF_BETS.name + "`, "
                + "`" + BFHistoricalHorseracingDatum.VOLUME_MATCHED.name + "`, "
                + "`" + BFHistoricalHorseracingDatum.LAST_TAKEN.name + "`, "
                + "`" + BFHistoricalHorseracingDatum.FIRST_TAKEN.name + "`, "
                + "`" + BFHistoricalHorseracingDatum.IN_PLAY.name + "`) "
                + "VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?);";
    }

    @Override
    protected PreparedStatement prepareInsertStatement(
            BFHistoricalHorseracingDatum t, DatabaseConduit conduit)
            throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        if (t.getRace() == null)
            toRet.setNull(1, Types.INTEGER);
        else
            toRet.setInt(1, (int)t.getRace().getId());
        if (t.getMarketSelection() == null)
            toRet.setNull(2, Types.INTEGER);
        else
            toRet.setInt(2, (int)t.getMarketSelection().getId());
        toRet.setDouble(3, t.getOdds());
        toRet.setInt(4, t.getNumberOfBets());
        toRet.setDouble(5, t.getVolumeMatched());
        toRet.setTimestamp(6, new Timestamp(t.getLastTaken().getTime()));
        toRet.setTimestamp(7, new Timestamp(t.getFirstTaken().getTime()));
        toRet.setString(8, t.getInPlay().name());
        return toRet;
    }

    @Override
    protected String getUpdateQueryStr() {
        return "UPDATE `" + getDatabasename() + "`.`" + getTablename()
                + "` SET `" + BFHistoricalHorseracingDatum.RACE.name + "` = ?,"
                + " `" + BFHistoricalHorseracingDatum.MARKET_SELECTION.name
                + "` = ?, `" + BFHistoricalHorseracingDatum.ODDS.name
                + "` = ?, `" + BFHistoricalHorseracingDatum.NUMBER_OF_BETS.name
                + "` = ?, `" + BFHistoricalHorseracingDatum.VOLUME_MATCHED.name
                + "` = ?, `" + BFHistoricalHorseracingDatum.LAST_TAKEN.name
                + "` = ?, `" + BFHistoricalHorseracingDatum.FIRST_TAKEN.name
                + "` = ?, `" + BFHistoricalHorseracingDatum.IN_PLAY.name
                + "` = ? WHERE `id` = ? LIMIT 1;";
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(
            BFHistoricalHorseracingDatum t, DatabaseConduit conduit)
            throws SQLException {
        PreparedStatement toRet = getUnfilledUpdateStatement(conduit);
        if (t.getRace() == null)
            toRet.setNull(1, Types.INTEGER);
        else
            toRet.setInt(1, (int)t.getRace().getId());
        if (t.getMarketSelection() == null)
            toRet.setNull(2, Types.INTEGER);
        else
            toRet.setInt(2, (int)t.getMarketSelection().getId());
        toRet.setDouble(3, t.getOdds());
        toRet.setInt(4, t.getNumberOfBets());
        toRet.setDouble(5, t.getVolumeMatched());
        toRet.setTimestamp(6, new Timestamp(t.getLastTaken().getTime()));
        toRet.setTimestamp(7, new Timestamp(t.getFirstTaken().getTime()));
        toRet.setString(8, t.getInPlay().name());
        toRet.setInt(9, (int)t.getId());
        return toRet;
    }
}
