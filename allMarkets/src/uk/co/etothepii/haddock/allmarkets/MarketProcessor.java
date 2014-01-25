/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.allmarkets;

import com.betfair.publicapi.types.exchange.v5.GetAllMarketsErrorEnum;
import com.betfair.publicapi.types.exchange.v5.GetAllMarketsReq;
import com.betfair.publicapi.types.exchange.v5.GetAllMarketsResp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import uk.co.epii.betfairclient.BFExchange;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.MasterController;
import uk.co.etothepii.haddock.db.factories.BFEventFactory;
import uk.co.etothepii.haddock.db.factories.BFMarketFactory;
import uk.co.etothepii.haddock.db.tables.BFEvent;
import uk.co.etothepii.haddock.db.tables.BFMarket;
import uk.co.etothepii.haddock.db.tables.enumerations.MarketStatus;
import uk.co.etothepii.haddock.db.tables.enumerations.MarketType;
import uk.co.etothepii.haddock.db.tables.enumerations.YN;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class MarketProcessor {

    public static enum UpdateType {NEW, FULL, SMALL, NONE};

    private static final Logger LOG =
            Logger.getLogger(MarketProcessor.class);

    private BFExchange bfExchange;
    private TreeMap<Integer, BFEvent> events;
    private TreeMap<Integer, BFEvent> eventsById;
    private TreeMap<Integer, BFMarket> markets;

    public MarketProcessor(BFExchange bfExchange) {
        this.bfExchange = bfExchange;
        events = new TreeMap<Integer, BFEvent>();
        eventsById = new TreeMap<Integer, BFEvent>();
        markets = new TreeMap<Integer, BFMarket>();
    }

    public TreeMap<Integer, BFMarket> getMarkets() {
        return markets;
    }

    ArrayList<MarketProcessedResult> update(long startedAt) throws SQLException,
            InterruptedException {
        DatabaseConduit conduit = MasterController.takeConduit();
        try {
            ArrayList<MarketProcessedResult> results =
                    updateAllMarkets(startedAt, conduit);
            ArrayList<MarketProcessedResult> toRet =
                    updateMarketsToDatabase(results, conduit, 1000);
            BFMarketFactory.getFactory().closeUnseen(new Date(startedAt));
            BFEventFactory.getFactory().deactivateUnseen(new Date(startedAt));
            return toRet;
        }
        finally {
            MasterController.releaseConduit(conduit);
        }
    }

    ArrayList<MarketProcessedResult> updateMarketsToDatabase(
            ArrayList<MarketProcessedResult> results, DatabaseConduit conduit,
            int pageSize) throws SQLException {
        ArrayList<MarketProcessedResult> toRet =
                new ArrayList<MarketProcessedResult>();
        for (int i = 0; i < results.size(); i += pageSize) {
            ArrayList<MarketProcessedResult> chunk =
                    new ArrayList<MarketProcessedResult>();
            for (int x = i; x < i + pageSize && x < results.size(); x++)
                chunk.add(results.get(x));
            toRet.addAll(updateMarketsToDatabase(chunk, conduit));
        }
        return toRet;
    }

    ArrayList<MarketProcessedResult> updateMarketsToDatabase(
            ArrayList<MarketProcessedResult> results, DatabaseConduit conduit)
            throws SQLException {
        ArrayList<MarketProcessedResult> toRet =
                new ArrayList<MarketProcessedResult>();
        PreparedStatement newQry = conduit.prepareStatement(
                "INSERT INTO bfMarkets (`id`, `betfairId`, `name`, `type`, "
                + "`status`, `date`, `parent`, `delay`, `exchange`, `country`, "
                + "`lastSeen`, `runners`, `winners`, `matched`, `bspMarket`, "
                + "`turningInPlay`) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                + "?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        PreparedStatement fullQry = conduit.prepareStatement(
                "UPDATE bfMarkets SET `betfairId` = ?, `name` = ?, `type` = ?, "
                + "`status` = ?, `date` = ?, `parent` = ?, `delay` = ?, "
                + "`exchange` = ?, `country` = ?, `lastSeen` = ?, "
                + "`runners` = ?, `winners` = ?, `matched` = ?, "
                + "`bspMarket` = ?, `turningInPlay` = ? WHERE `id` = ?");
        PreparedStatement smallQry = conduit.prepareStatement(
                "UPDATE bfMarkets SET `matched` = ?, `lastSeen` = ? "
                + "WHERE `id` = ?");
        ArrayList<BFMarket> toCreate = new ArrayList<BFMarket>();
        boolean executeNew = false;
        boolean executeSmall = false;
        boolean executeFull = false;
        for (MarketProcessedResult result : results) {
            switch (result.updateType) {
                case NEW: fillNew(result.market, newQry); 
                        toCreate.add(result.market); newQry.addBatch(); 
                        executeNew = true;break;
                case FULL: fillFull(result.market, fullQry);
                        fullQry.addBatch(); executeFull = true; break;
                case SMALL: fillSmall(result.market, smallQry);
                        smallQry.addBatch(); executeSmall = true; break;
            }
            if (result.isRace || result.runnersNeedsUpdating)
                toRet.add(result);
        }
        if (executeNew) {
            newQry.executeBatch();
            ResultSet rs = newQry.getGeneratedKeys();
            int count = 0;
            for (BFMarket market : toCreate) {
                count++;
                try {
                    rs.next();
                    market.setId(rs.getInt(1));
                }
                catch (SQLException sqle) {
                    LOG.debug("failed on " + count);
                    throw sqle;
                }
            }
        }
        if (executeFull)
            fullQry.executeBatch();
        if (executeSmall)
            smallQry.executeBatch();
        newQry.close();
        fullQry.close();
        smallQry.close();
        return toRet;
    }

    private void fillNew(BFMarket market, PreparedStatement ps)
            throws SQLException {
        ps.setInt(1, market.getBetfairId());
        ps.setString(2, market.getName());
        ps.setString(3, market.getType().name());
        ps.setString(4, market.getStatus().name());
        ps.setTimestamp(5, new Timestamp(market.getDate().getTime()));
        if (market.getParent() == null)
            ps.setNull(6, Types.INTEGER);
        else
            ps.setLong(6, market.getParent().getId());
        ps.setInt(7, market.getDelay());
        ps.setInt(8, market.getExchange());
        ps.setString(9, market.getCountry());
        ps.setTimestamp(10, new Timestamp(market.getLastSeen().getTime()));
        ps.setInt(11, market.getRunners());
        ps.setInt(12, market.getWinners());
        ps.setLong(13, market.getMatched());
        ps.setString(14, market.getBspMarket().name());
        ps.setString(15, market.getTurningInPlay().name());
    }

    private void fillFull(BFMarket market, PreparedStatement ps)
            throws SQLException{
        fillNew(market, ps);
        ps.setLong(16, market.getId());
    }

    private void fillSmall(BFMarket market, PreparedStatement ps) 
            throws SQLException{
        ps.setLong(1, market.getMatched());
        ps.setTimestamp(2, new Timestamp(market.getLastSeen().getTime()));
        ps.setLong(3, market.getId());
    }

    ArrayList<MarketProcessedResult> updateAllMarkets(long startedAt,
            DatabaseConduit conduit)
            throws InterruptedException, SQLException {
        String allMarketsString = bfExchange.getAllMarkets();
        if (allMarketsString == null) return null;
        if (LOG.isDebugEnabled()){
            LOG.debug("allMarketsStringStart: ".concat(
                allMarketsString.length() > 100 ?
                    allMarketsString.substring(0, 100).concat(
                    " ... ") : allMarketsString));
        }
        return process(allMarketsString, startedAt, conduit);
    }

    private ArrayList<TreeMap<Integer, PreliminaryEvent>> getPrelimEvents(
            ArrayList<PreliminaryMarket> prelimMarkets) {
        ArrayList<TreeMap<Integer, PreliminaryEvent>> prelimEvents =
                new ArrayList<TreeMap<Integer, PreliminaryEvent>>();
        for (PreliminaryMarket market : prelimMarkets) {
            ArrayList<PreliminaryEvent> preliminaryEvents =
                    market.getPreliminaryEvents();
            for (PreliminaryEvent pe : preliminaryEvents) {
                if (pe.parents >= prelimEvents.size()) {
                    for (int i = prelimEvents.size(); i <= pe.parents; i++) {
                        prelimEvents.add(new TreeMap<Integer, PreliminaryEvent>());
                    }
                }
                prelimEvents.get(pe.parents).put(pe.bfId, pe);
            }
        }
        return prelimEvents;
    }

    private void executeAndClose(String sql, DatabaseConduit conduit)
            throws SQLException {
        PreparedStatement ps = conduit.prepareStatement(sql);
        ps.execute();
        ps.close();
    }

    private void updateEvents(ArrayList<PreliminaryMarket> prelimMarkets,
            long startedAt, DatabaseConduit conduit) throws SQLException {
        ArrayList<TreeMap<Integer, PreliminaryEvent>> prelimEvents =
                getPrelimEvents(prelimMarkets);
        if (LOG.isDebugEnabled()) {
            for (int i = 0; i < prelimEvents.size(); i++)
                LOG.debug("LEVEL " + i + ": " + prelimEvents.get(i).size());
        }
        for (int i = 0; i < prelimEvents.size(); i++) {
            TreeMap<Integer, PreliminaryEvent> thesePrelimEvents =
                    prelimEvents.get(i);
            if (thesePrelimEvents.isEmpty()) {
                System.out.println("None at level " + i);
                continue;
            }
            String tmpTblNme = DatabaseConduit.getTemporaryTableName();
            try {
                executeAndClose("CREATE TEMPORARY TABLE " + tmpTblNme +
                        " (seek int(11))", conduit);
                PreparedStatement ps = conduit.prepareStatement(
                        "INSERT INTO " + tmpTblNme + " (`seek`) VALUES (?)");
                for (PreliminaryEvent preliminaryEvent :
                        thesePrelimEvents.values()) {
                    ps.setInt(1, preliminaryEvent.bfId);
                    LOG.debug("bfId: " + preliminaryEvent.bfId);
                    ps.addBatch();
                }
                ps.executeBatch();
                ps.close();
                ps = conduit.prepareStatement("SELECT * FROM " + tmpTblNme +
                        " t LEFT JOIN bfEvents e on t.seek = e.betfairId");
                ResultSet rs = ps.executeQuery();
                ArrayList<PreliminaryEvent> toCreate =
                        new ArrayList<PreliminaryEvent>();
                while (rs.next()) {
                    BFEvent found = buildEvent(rs, toCreate, thesePrelimEvents);
                    if (found == null) {
                        LOG.debug("Failed to load " + rs.getInt("seek"));
                        continue;
                    }
                    events.put(found.getBetfairId(), found);
                    eventsById.put((int)found.getId(), found);
                }
                createEvents(toCreate, startedAt, conduit, 1000);
                rs.beforeFirst();
                while (rs.next()) {
                    if (events.get(rs.getInt("seek")) == null) {
                        LOG.debug("FAILED TO LOAD EVENT " + rs.getInt("seek"));
                    }
                }
                ps.close();
            }
            finally {
                PreparedStatement ps = conduit.prepareStatement(
                        "UPDATE bfEvents e, " + tmpTblNme + " t SET "
                        + "e.lastSeen = ?, e.active = 'Y' "
                        + "WHERE t.seek = e.betfairId");
                ps.setTimestamp(1, new Timestamp(startedAt));
                ps.execute();
                ps.close();
                executeAndClose("DROP TEMPORARY TABLE " + tmpTblNme, conduit);
            }
        }
    }

    private BFEvent buildEvent(ResultSet rs, 
            ArrayList<PreliminaryEvent> toCreate, 
            TreeMap<Integer, PreliminaryEvent> thesePrelimEvents)
            throws SQLException {
        int seek = rs.getInt("seek");
        int id = rs.getInt("id");
        if (rs.wasNull()) {
            toCreate.add(thesePrelimEvents.get(seek));
            LOG.debug("seek: " + seek);
            return null;
        }
        String name = rs.getString("name");
        YN active = YN.valueOf(rs.getString("active"));
        Date lastSeen = rs.getTimestamp("lastSeen");
        int parentId = rs.getInt("parent");
        BFEvent parent;
        if (rs.wasNull())
            parent = null;
        else
            parent = eventsById.get(parentId);
        BFEvent found = new BFEvent(id, seek, parent,
                name, active, lastSeen);
        LOG.debug("found: " + found.toString());
        return found;
    }

    private void createEvents(ArrayList<PreliminaryEvent> toCreate,
            long startedAt, DatabaseConduit conduit, int pageSize)
            throws SQLException{
        if (LOG.isDebugEnabled())
            LOG.debug("Creating " + toCreate.size() + " events");
        for (int i = 0; i < toCreate.size(); i += pageSize) {
            ArrayList<PreliminaryEvent> preliminaryEvents =
                    new ArrayList<PreliminaryEvent>();
            int stop = i + pageSize;
            for (int x = i; x < stop && x < toCreate.size(); x++) {
                preliminaryEvents.add(toCreate.get(x));
            }
            createEvents(preliminaryEvents, startedAt, conduit);
        }
    }

    private void createEvents(ArrayList<PreliminaryEvent> toCreate,
            long startedAt, DatabaseConduit conduit) throws SQLException {
        if (LOG.isDebugEnabled())
            LOG.debug("Creating " + toCreate.size() + " events");
        if (toCreate.isEmpty()) return;
        PreparedStatement ps = conduit.prepareStatement("INSERT INTO bfEvents "
                + "(id, betfairId, name, parent, active, lastSeen) VALUES "
                + "(NULL, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        for (PreliminaryEvent pe : toCreate) {
            ps.setInt(1, pe.bfId);
            ps.setString(2, pe.name);
            BFEvent parent;
            if (pe.parentBfId == null) {
                ps.setNull(3, Types.INTEGER);
                parent = null;
            }
            else {
                parent = events.get(pe.parentBfId);
                ps.setLong(3, parent.getId());
            }
            ps.setString(4, "Y");
            ps.setDate(5, new java.sql.Date(startedAt));
            ps.addBatch();
            events.put(pe.bfId, new BFEvent(pe.bfId, parent, pe.name, YN.Y,
                    new Date(startedAt)));
        }
        LOG.debug(ps);
        ps.executeBatch();
        ResultSet rs = ps.getGeneratedKeys();
        for (PreliminaryEvent pe : toCreate) {
            rs.next();
            int id = rs.getInt(1);
            events.get(pe.bfId).setId(id);
        }
    }

    private void updateMarkets(ArrayList<PreliminaryMarket> prelimMarkets,
            long startedAt, DatabaseConduit conduit) throws SQLException {
        String tmpTblNme = DatabaseConduit.getTemporaryTableName();
        executeAndClose("CREATE TEMPORARY TABLE " + tmpTblNme +
                " (seek int(11))", conduit);
        PreparedStatement ps = conduit.prepareStatement(
                "INSERT INTO " + tmpTblNme + " (`seek`) VALUES (?)");
        for (PreliminaryMarket preliminaryMarket :
                prelimMarkets) {
            ps.setInt(1, preliminaryMarket.marketId);
            ps.addBatch();
        }
        ps.executeBatch();
        ps.close();
        ps = conduit.prepareStatement("SELECT * FROM " + tmpTblNme +
                " t LEFT JOIN bfMarkets m on t.seek = m.betfairId");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            BFMarket market = buildMarket(rs, startedAt);
            if (market != null)
                markets.put(market.getBetfairId(), market);
        }
    }

    private BFMarket buildMarket(ResultSet rs, long startedAt) throws SQLException {
        int id = rs.getInt("id");
        int betfairId = rs.getInt("betfairId");
        if (rs.wasNull())
            return null;
        String name = rs.getString("name");
        MarketType type = MarketType.valueOf(rs.getString("type"));
        MarketStatus status = MarketStatus.valueOf(rs.getString("status"));
        Date date = rs.getTimestamp("date");
        BFEvent parent = eventsById.get(rs.getInt("parent"));
        int delay = rs.getInt("delay");
        int exchange = rs.getInt("exchange");
        String country = rs.getString("country");
        Date lastSeen = new Date(startedAt);
        Date refresh = rs.getTimestamp("refresh");
        int runners = rs.getInt("runners");
        int winners = rs.getInt("winners");
        long matched = rs.getLong("matched");
        YN bspMatched = YN.valueOf(rs.getString("bspMarket"));
        YN turningInPlay = YN.valueOf(rs.getString("turningInPlay"));
        return new BFMarket(id, betfairId, name, type, status, date, parent,
                delay, exchange, country, refresh, lastSeen, runners, winners,
                matched, bspMatched, turningInPlay, null, null);
    }

    private ArrayList<MarketProcessedResult> process(String allMarketsString, 
            long startedAt, DatabaseConduit conduit) throws SQLException {
        String[] marketStrs = allMarketsString.split("(?<!\\\\):");
        for (int i = 0; i < marketStrs.length; i++)
            marketStrs[i] = marketStrs[i].replace("\\:", ":");
        if (LOG.isInfoEnabled()){
            LOG.info("markets.length: " + marketStrs.length);
        }
        ArrayList<PreliminaryMarket> prelims =
                new ArrayList<PreliminaryMarket>(marketStrs.length);
        for (String marketStr : marketStrs) {
//            try {
                PreliminaryMarket market = prelimProcess(marketStr);
                if (market != null)
                    prelims.add(market);
//            }
//            catch (IllegalArgumentException iae) {
//                LOG.error(iae.getMessage());
//            }
        }
        updateEvents(prelims, startedAt, conduit);
        updateMarkets(prelims, startedAt, conduit);
        ArrayList<MarketProcessedResult> toRet =
                new ArrayList<MarketProcessedResult>();
        for (PreliminaryMarket market : prelims)
            toRet.add(processMarket(market, startedAt));
        Collections.sort(toRet);
        return toRet;
    }

    private String[] splitMarket(String market) {
        String[] parts = market.split("(?<!\\\\)~");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].replace("\\~", "~");
        }
        return parts;
    }

    private static PreliminaryMarket prelimProssess(String[] parts) {
        int marketId = Integer.parseInt(parts[0].trim());
        String name = parts[1].trim();
        MarketType type = MarketType.valueOf(
                parts[2].trim());
        MarketStatus status =
                MarketStatus.valueOf(parts[3].trim());
        long date = Long.parseLong(parts[4].trim());
        String[] menuPathStrings =
                parts[5].trim().split("(?<!\\\\)\\\\");
        ArrayList<String> tempMenuPath = new ArrayList<String>();
        for (int i = 1; i < menuPathStrings.length; i++) {
            String s = menuPathStrings[i].replace("\\\\", "\\");
            if (!s.matches("^Group [ABCD]") || i != 2)
                tempMenuPath.add(s);
        }
        String[] menuPath = tempMenuPath.toArray(new String[0]);
        String[] eventHierarchyStrings =
                parts[6].trim().split("(?<!\\\\)/");
        for (int i = 0; i < eventHierarchyStrings.length; i++) 
            eventHierarchyStrings[i] =
                    eventHierarchyStrings[i].replace("\\/", "/");
        int[] eventHierarchy = new int[eventHierarchyStrings.length - 2];
        for(int i = 0; i < eventHierarchy.length; i++) 
            eventHierarchy[i] =
                    Integer.parseInt(eventHierarchyStrings[i + 1].trim());
        if (menuPath.length != eventHierarchy.length)
            throw new IllegalArgumentException("The menuPath and "
                    + "eventHierarchy have a different number of arguments "
                    + parts[5] + " " + parts[6]);
        int delay = Integer.parseInt(parts[7].trim());
        int exchangeId = Integer.parseInt(parts[8].trim());
        String country = parts[9].trim();
        long lastRefresh = Long.parseLong(parts[10].trim());
        int runners = Integer.parseInt(parts[11].trim());
        int winners = Integer.parseInt(parts[12].trim());
        double matched = Double.parseDouble(
                parts[13].trim());
        YN bspMarket = YN.valueOf(parts[14].trim());
        YN inPlay = YN.valueOf(parts[15].trim());
        return new PreliminaryMarket(marketId, name, type, status, date,
                menuPath, eventHierarchy, delay, exchangeId, country, runners,
                winners, matched, bspMarket, inPlay);
    }

    private PreliminaryMarket prelimProcess(String market) {
        if (market.length() < 1) return null;
        String[] parts = splitMarket(market);
        return prelimProssess(parts);
    }

    private MarketProcessedResult processMarket(PreliminaryMarket prelim, long startedAt) {
        BFMarket bfMarket = markets.get(prelim.marketId);
        MarketProcessedResult toRet = new MarketProcessedResult();
        BFEvent parent = events.get(prelim.eventHierarchy[
                prelim.eventHierarchy.length - 1]);
//        BFEvent parent = events.get(Integer.parseInt(
//                prelim.eventHierarchy[prelim.eventHierarchy.length - 1].trim()));
        boolean runnersIncreased;
        if (bfMarket == null) {
            runnersIncreased = true;
            bfMarket = prelim.toBFMarket(parent, startedAt);
            toRet.updateType = UpdateType.NEW;
        }
        else {
            PreliminaryMarket.UpdateResult result = prelim.updateBFMarket(
                    bfMarket, parent, startedAt);
            runnersIncreased = result.runnersIncreased;
            toRet.updateType = result.updateType;
        }
        if (runnersIncreased && prelim.type == MarketType.O)
            toRet.runnersNeedsUpdating = true;
        if (LOG.isDebugEnabled()) {
            LOG.debug(Arrays.toString(prelim.menuPath));
            LOG.debug(parent.toString());
        }
        if (prelim.menuPath.length == 3 && prelim.menuPath[0].equals("Horse Racing") &&
                (prelim.menuPath[1].equals("GB") || prelim.menuPath[1].equals("IRE")) &&
                parent.getName().matches(
                "^[A-Z][A-Za-z]{2,10} [0-9]{1,2}[snrt][tdh] [A-Z][a-z]{2}")) 
            toRet.isRace = true;
        LOG.debug(parent.getName() + ": " + toRet.isRace);
        toRet.market = bfMarket;
        return toRet;
    }

}

class MarketProcessedResult implements Comparable<MarketProcessedResult> {

    public boolean runnersNeedsUpdating;
    public boolean isRace;
    public BFMarket market;
    public MarketProcessor.UpdateType updateType;

    public MarketProcessedResult() {
        this(false, false, null, MarketProcessor.UpdateType.NONE);
    }

    public MarketProcessedResult(boolean runnersNeedsUpdating, boolean isRace,
            BFMarket market, MarketProcessor.UpdateType updateType) {
        this.runnersNeedsUpdating = runnersNeedsUpdating;
        this.isRace = isRace;
        this.market = market;
        this.updateType = updateType;
    }

    public int compareTo(MarketProcessedResult o) {
        int comparison = updateType.compareTo(o.updateType);
        if (comparison != 0) return comparison;
        return o.market.getBetfairId() - market.getBetfairId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MarketProcessedResult) {
            MarketProcessedResult mpr = (MarketProcessedResult)obj;
            return (updateType == mpr.updateType) &&
                    (market.getBetfairId() == mpr.market.getBetfairId());
        }
        else
            return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.market != null ? this.market.hashCode() : 0);
        hash = 97 * hash + (this.updateType != null ? this.updateType.hashCode() : 0);
        return hash;
    }
}

class PreliminaryMarket {

    public final int marketId;
    public final String name;
    public final MarketType type;
    public final MarketStatus status;
    public final long date;
    public final String[] menuPath;
    public final int[] eventHierarchy;
    public final int delay;
    public final int exchangeId;
    public final String country;
    public final int runners;
    public final int winners;
    public final double matched;
    public final YN bspMarket;
    public final YN inPlay;

    public PreliminaryMarket(int marketId, String name, MarketType type,
            MarketStatus status, long date, String[] menuPath,
            int[] eventHierarchy, int delay, int exchangeId, String country,
            int runners, int winners, double matched, YN bspMarket, YN inPlay) {
        this.marketId = marketId;
        this.name = name;
        this.type = type;
        this.status = status;
        this.date = date;
        this.menuPath = menuPath;
        this.eventHierarchy = eventHierarchy;
        this.delay = delay;
        this.exchangeId = exchangeId;
        this.country = country;
        this.runners = runners;
        this.winners = winners;
        this.matched = matched;
        this.bspMarket = bspMarket;
        this.inPlay = inPlay;
    }

    public ArrayList<PreliminaryEvent> getPreliminaryEvents() {
        ArrayList<PreliminaryEvent> toRet = new ArrayList<PreliminaryEvent>();
        for (int i = 0; i < eventHierarchy.length; i++) {
            int parents = i;
            Integer parentBfId = parents == 0 ? null :
                eventHierarchy[parents - 1];
            int bfId = eventHierarchy[parents];
            String eventName = menuPath[parents];
            toRet.add(new PreliminaryEvent(parentBfId, bfId, eventName, parents));
        }
        return toRet;
    }

    public BFMarket toBFMarket(BFEvent parent, long startedAt) {
        return new BFMarket(marketId, name, type, status,
                new Date(date), parent, delay, exchangeId, country, null,
                new Date(startedAt), runners, winners, (long)(matched * 100),
                bspMarket, inPlay, null, null);
    }

    public UpdateResult updateBFMarket(BFMarket bfMarket, BFEvent parent,
            long startedAt) {
        bfMarket.setBetfairId(marketId);
        bfMarket.setName(name);
        bfMarket.setType(type);
        bfMarket.setStatus(status);
        bfMarket.setDate(new Date(date));
        bfMarket.setParent(parent);
        bfMarket.setExchange(exchangeId);
        bfMarket.setCountry(country);
        boolean runnersIncreased = runners > bfMarket.getRunners();
        bfMarket.setRunners(runners);
        bfMarket.setWinners(winners);
        bfMarket.setBspMarket(bspMarket);
        bfMarket.setTurningInPlay(inPlay);
        bfMarket.setDelay(delay);
        boolean bigChanges = bfMarket.isChanged();
        bfMarket.setMatched((long)(matched * 100));
        bfMarket.setLastSeen(new Date(startedAt));
        if (bfMarket.isChanged()) {
            if (!bigChanges)
                return new UpdateResult(runnersIncreased,
                        MarketProcessor.UpdateType.SMALL);
            else
                return new UpdateResult(runnersIncreased,
                        MarketProcessor.UpdateType.FULL);
        }
        else {
            return new UpdateResult(runnersIncreased,
                    MarketProcessor.UpdateType.NONE);
        }
    }

    class UpdateResult {
        boolean runnersIncreased;
        MarketProcessor.UpdateType updateType;

        public UpdateResult() {
            this(false, MarketProcessor.UpdateType.NONE);
        }

        public UpdateResult(boolean runnersIncreased,
                MarketProcessor.UpdateType updateType) {
            this.runnersIncreased = runnersIncreased;
            this.updateType = updateType;
        }
    }
}

class PreliminaryEvent implements Comparable<PreliminaryEvent> {
    public final Integer parentBfId;
    public final int bfId;
    public final String name;
    public final int parents;

    public PreliminaryEvent(Integer parentBfId, int bfId, String name,
            int parents) {
        this.parentBfId = parentBfId;
        this.bfId = bfId;
        this.name = name;
        this.parents = parents;
    }

    public int compareTo(PreliminaryEvent o) {
        int comparasion = o.parents - parents;
        if (comparasion != 0) return comparasion;
        return o.bfId - bfId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PreliminaryEvent) {
            PreliminaryEvent pe = (PreliminaryEvent)obj;
            return pe.bfId == bfId && pe.parents == parents;
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.bfId;
        hash = 59 * hash + this.parents;
        return hash;
    }


}