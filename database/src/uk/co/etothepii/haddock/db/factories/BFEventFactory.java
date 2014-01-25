/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.factories;

import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.HaddockStatement;
import uk.co.etothepii.haddock.db.tables.BFEvent;
import uk.co.etothepii.haddock.db.tables.enumerations.YN;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class BFEventFactory extends HaddockBFFactory<BFEvent> {

    private static final Logger LOG = Logger.getLogger(BFEventFactory.class);
    private static final Logger LOG_QUERY = Logger.getLogger(
            BFEventFactory.class.getName().concat(".query"));

    @Override
    protected String getTablename() {
        return "bfEvents";
    }

    private static BFEventFactory factory;

    public static BFEventFactory getFactory() {
        if (factory == null)
            factory = new BFEventFactory();
        return factory;
    }

    public BFEventFactory() {}

    @Override
    protected BFEvent build(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            int betfairId = rs.getInt("betfairId");
            int parentId = rs.getInt("parent");
            String name = rs.getString("name");
            String activeStr = rs.getString("active");
            YN active = YN.valueOf(activeStr);
            Timestamp lastSeenTS = rs.getTimestamp("lastSeen");
            Date lastSeen = lastSeenTS == null ? null :
                new Date(lastSeenTS.getTime());
            return new BFEvent(id, betfairId, getFromId(parentId), name, active,
                    lastSeen);
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
    }

    @Override
    protected String getInsertQueryStr() {
        return "INSERT INTO `" + getDatabasename() + "`.`" + getTablename()
                + "` (`id`, `betfairId`, `name`, `parent`, `active`, `lastSeen`"
                + ") VALUES (NULL, ?, ?, ?, ?, ?);";
    }

    @Override
    protected PreparedStatement prepareInsertStatement(BFEvent t,
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        toRet.setInt(1, t.getBetfairId());
        toRet.setString(2, t.getName());
        if (t.getParent() == null) {
            toRet.setNull(3, Types.INTEGER);
        }
        else {
            toRet.setInt(3, (int)t.getParent().getId());
        }
        toRet.setString(4, t.getActive().toString());
        if (t.getLastSeen() == null) {
            toRet.setNull(5, Types.DATE);
        }
        else {
            toRet.setTimestamp(5, new Timestamp(t.getLastSeen().getTime()));
        }
        return toRet;
    }

    @Override
    protected String getUpdateQueryStr() {
        return "UPDATE `" + getDatabasename() + "`.`" + getTablename()
                + "` SET `betfairId` = ?, `name` = ?, `parent` = ?, "
                + "`active` = ?, `lastSeen` = ? WHERE `id` = ? LIMIT 1;";
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(BFEvent t,
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledUpdateStatement(conduit);
        toRet.setInt(1, t.getBetfairId());
        toRet.setString(2, t.getName());
        if (t.getParent() == null) {
            toRet.setNull(3, Types.INTEGER);
        }
        else {
            toRet.setInt(3, (int)t.getParent().getId());
        }
        toRet.setString(4, t.getActive().toString());
        if (t.getLastSeen() == null) {
            toRet.setNull(5, Types.DATE);
        }
        else {
            toRet.setTimestamp(5, new Timestamp(t.getLastSeen().getTime()));
        }
        toRet.setInt(6, (int)t.getId());
        return toRet;
    }

    private HaddockStatement getBFEventActiveChildrenQuery =
            new HaddockStatement("SELECT * FROM `" + getDatabasename() + "`.`"
                + getTablename() + "` WHERE `parent` = ? AND `active` = 'Y'",
                false);

    private HaddockStatement getBFEventActiveNullChildrenQuery =
            new HaddockStatement("SELECT * FROM `" + getDatabasename() + "`.`"
                + getTablename() + "` WHERE `parent` IS NULL AND "
                + "`active` = 'Y'", false);

    public ArrayList<BFEvent> getBFActiveEventChildren(BFEvent bfEvent)
            throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return getBFActiveEventChildren(bfEvent, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public ArrayList<BFEvent> getBFActiveEventChildren(BFEvent bfEvent,
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement ps;
        if (bfEvent == null)
            ps = getBFEventActiveNullChildrenQuery.getStatement(conduit);
        else {
            ps = getBFEventActiveChildrenQuery.getStatement(conduit);
            ps.setInt(1, (int)bfEvent.getId());
        }
        LOG_QUERY.debug(ps);
        ResultSet rs = null;
        ArrayList<BFEvent> toRet = new ArrayList<BFEvent>();
        try {
            rs = ps.executeQuery();
            while (rs.next())
                toRet.add(build(rs));
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
        return toRet;
    }

    @Override
    public void save(BFEvent t, boolean cache) throws SQLException {
        super.save(t, cache);
        if (t.getParent() != null) {
            save(t.getParent(), cache);
        }
    }
    
    private HaddockStatement getUKAndIreMeetingsQuery = new HaddockStatement(
            "SELECT * FROM `" + getDatabasename() + "`.`" + getTablename() + 
            "` WHERE (`" + BFEvent.PARENT.name + "` = 29562 OR `" + 
            BFEvent.PARENT.name + "` = 29570) AND `" + BFEvent.NAME.name + 
            "` LIKE ? AND `lastSeen` > ? AND `lastSeen` < ?", false);

    public ArrayList<BFEvent> getUKAndIREMeetings(Date startDay, Date stopDate)
            throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            return getUKAndIREMeetings(startDay, stopDate, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public ArrayList<BFEvent> getUKAndIREMeetings(Date startDay, Date stopDate,
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement ps = null;
		ResultSet rs = null;
		try {
        	ps = getUKAndIreMeetingsQuery.getStatement(conduit);
        	ArrayList<BFEvent> meetings = new ArrayList<BFEvent>();
        	Calendar cal = Calendar.getInstance();
        	Calendar lastSeen = Calendar.getInstance();
        	SimpleDateFormat day = new SimpleDateFormat("d");
        	SimpleDateFormat month = new SimpleDateFormat("MMM");
        	for (cal.setTimeInMillis(startDay.getTime());
                 	cal.getTimeInMillis() < stopDate.getTime();
                 	cal.add(Calendar.DAY_OF_MONTH, 1)) {
            	Date date = new Date(cal.getTimeInMillis());
            	lastSeen.setTimeInMillis(cal.getTimeInMillis());
            	lastSeen.add(Calendar.MONTH, -1);
            	java.sql.Date searchFrom = new java.sql.Date(lastSeen.getTimeInMillis());
            	lastSeen.add(Calendar.MONTH, 2);
            	java.sql.Date searchTo = new java.sql.Date(lastSeen.getTimeInMillis());
            	StringBuilder sb = new StringBuilder("%");
            	sb.append(day.format(date));
            	sb.append("% ");
            	sb.append(month.format(date));
            	ps.setString(1, sb.toString());
            	ps.setDate(2, searchFrom);
            	ps.setDate(3, searchTo);
            	rs = ps.executeQuery();
            	while (rs.next()) {
                	meetings.add(build(rs));
            	}
        	}
        	return meetings;
		}
		finally {
			if (rs != null) {
				while (rs.next());
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
		}
    }

    private HaddockStatement closeUnseenQuery = new HaddockStatement(
            "UPDATE `" + getDatabasename() + "`.`" + getTablename() + "` "
            + " SET `active` = 'N' WHERE `lastSeen` IS NULL OR "
            + "`lastSeen`  < ?", false);
    /**
     * Removes anything which hasn't been seen since a specified time.
     * More precisely removes anything that hasn't been seen since the
     * second before since MySQL Timestamp does not have resolution to the
     * millisecond.
     *
     * @param startedAt the time at which one last started looking for new
     *                  markets
     * @throws SQLException
     */
    public void deactivateUnseen(Date startedAt) throws SQLException {
        DatabaseConduit conduit = takeConduit();
        try {
            deactivateUnseen(startedAt, conduit);
        }
        finally {
            releaseConduit(conduit);
        }
    }

    public void deactivateUnseen(Date startedAt, DatabaseConduit conduit)
            throws SQLException {
        PreparedStatement ps = closeUnseenQuery.getStatement(conduit);
        ps.setTimestamp(1, new Timestamp(startedAt.getTime() - 1500L));
        ps.execute();
    }
}
