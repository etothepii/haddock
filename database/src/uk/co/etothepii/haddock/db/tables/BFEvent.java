/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables;

import java.util.Comparator;
import java.util.Date;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.Field;
import uk.co.etothepii.db.FieldChangedEvent;
import uk.co.etothepii.haddock.db.tables.enumerations.YN;
/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class BFEvent extends BFDataAccessObject {

    public static final Field PARENT = new Field("parent",
            BFEvent.class, BFEvent.class);
    public static final Field NAME = new Field("name",
            String.class, BFEvent.class);
    public static final Field ACTIVE = new Field("active",
            YN.class, BFEvent.class);
    public static final Field LAST_SEEN = new Field("lastSeen",
            Date.class, BFEvent.class);

    private static final Logger LOG = Logger.getLogger(BFEvent.class);

    public static final Comparator<BFEvent> ALPHABETICAL =
            new Comparator<BFEvent>() {
        public int compare(BFEvent o1, BFEvent o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    private BFEvent parent;
    private String name;
    private YN active;
    private Date lastSeen;

    public BFEvent(int betfairId, BFEvent parent, String name, YN active,
            Date lastSeen) {
        this(0, betfairId, parent, name, active, lastSeen);
    }

    public BFEvent(int id, int betfairId, BFEvent parent, String name,
            YN active, Date lastSeen) {
        super(id, betfairId);
        this.parent = parent;
        this.name = name;
        this.active = active;
        this.lastSeen = lastSeen;
    }

    public BFEvent getParent() {
        return parent;
    }

    public void setParent(BFEvent parent) {
        if (different(parent, this.parent)) {
            BFEvent old = this.parent;
            this.parent = parent;
            fireFieldChangedListener(new FieldChangedEvent(
                    this, PARENT, old, parent));
            setChanged(true);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (different(name, this.name)) {
            String old = this.name;
            this.name = name;
            fireFieldChangedListener(new FieldChangedEvent(this,
                    NAME, old, name));
            setChanged(true);
        }
    }

    public YN getActive() {
        return active;
    }

    public void setActive(YN active) {
        if (different(this.active, active)) {
            YN old = this.active;
            this.active = active;
            fireFieldChangedListener(new FieldChangedEvent(this,
                    ACTIVE, old, active));
            setChanged(true);
        }
        if (parent != null)
            parent.setActive(active);
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        if (different(lastSeen, this.lastSeen)) {
            Date old = this.lastSeen;
            this.lastSeen = lastSeen;
            fireFieldChangedListener(new FieldChangedEvent(this,
                    LAST_SEEN, old, lastSeen));
            setChanged(true);
        }
        if (parent != null)
            parent.setLastSeen(lastSeen);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append(getId());
        sb.append(",");
        sb.append(getBetfairId());
        sb.append(",");
        sb.append(parent == null ? "NULL" : parent.getId());
        sb.append(",");
        sb.append(name);
        return sb.toString();
    }

}
