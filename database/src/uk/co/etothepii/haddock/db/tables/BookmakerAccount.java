/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.Field;
import uk.co.etothepii.db.FieldChangedEvent;

/**
 *
 * @author jrrpl
 */
public class BookmakerAccount extends HaddockAccessObject {

    public static final Field BOOKMAKER = new Field("bookmaker", 
            Bookmaker.class, BookmakerAccount.class);
    public static final Field IDENTITY = new Field("identity", 
            Identity.class, BookmakerAccount.class);
    public static final Field USERNAME = new Field("username", 
            String.class, BookmakerAccount.class);
    public static final Field PASSWORD = new Field("password", 
            String.class, BookmakerAccount.class);
    public static final Field OPENED = new Field("opened", 
            Date.class, BookmakerAccount.class);
    public static final Field LIMITED = new Field("limited", 
            Date.class, BookmakerAccount.class);
    public static final Field ABANDONED = new Field("abandoned", 
            Date.class, BookmakerAccount.class);
    public static final Field CLOSED = new Field("closed", 
            Date.class, BookmakerAccount.class);

    private static final Logger LOG = Logger.getLogger(BookmakerAccount.class);

    public enum DateType {
        OPENED(0, "Opened"),
        LIMITED(1, "Limited"),
        ABANDONED(2, "Abandoned"),
        CLOSED(3, "Closed");

        public final int index;
        public final String name;

        private DateType (int index, String name) {
            this.index = index;
            this.name = name;
        }
    };


    private Bookmaker bookmaker;
    private Identity identity;
    private String username;
    private String password;
    private Date opened;
    private Date limited;
    private Date abandoned;
    private Date closed;

    public BookmakerAccount(int id, Bookmaker bookmaker, Identity identity,
            String username, String password, Date opened, Date limited,
            Date abandoned, Date closed) {
        super(id);
        this.bookmaker = bookmaker;
        this.identity = identity;
        this.username = username;
        this.password = password;
        this.opened = opened;
        this.limited = limited;
        this.abandoned = abandoned;
        this.closed = closed;
    }

    public BookmakerAccount(Bookmaker bookmaker, Identity identity,
            String username, String password, Date opened, Date limited,
            Date abandoned, Date closed) {
        super(0);
        this.bookmaker = bookmaker;
        this.identity = identity;
        this.username = username;
        this.password = password;
        this.opened = opened;
        this.limited = limited;
        this.abandoned = abandoned;
        this.closed = closed;
    }

    public Bookmaker getBookmaker() {
        return bookmaker;
    }

    public void setBookmaker(Bookmaker bookmaker) {
        if (different(bookmaker, this.bookmaker)) {
            Bookmaker old = this.bookmaker;
            this.bookmaker = bookmaker;
            fireFieldChangedListener(new FieldChangedEvent(this, BOOKMAKER,
                    old, bookmaker));
            setChanged(true);
        }
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        if (different(identity, this.identity)) {
            Identity old = this.identity;
            this.identity = identity;
            fireFieldChangedListener(new FieldChangedEvent(this, IDENTITY, old,
                    identity));
            setChanged(true);
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (different(username, this.username)) {
            String old = this.username;
            this.username = username;
            fireFieldChangedListener(new FieldChangedEvent(this, USERNAME, old,
                    username));
            setChanged(true);
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (different(password, this.password)) {
            String old = this.password;
            this.password = password;
            fireFieldChangedListener(new FieldChangedEvent(this, PASSWORD, old,
                    password));
            setChanged(true);
        }
    }

    public Date getOpened() {
        return opened;
    }

    public void setOpened(Date opened) {
        LOG.debug("setOpened(" + (opened == null ? "NULL" :
            sdf.format(opened)) + ")");
        if (different(opened, this.opened)) {
            Date old = this.opened;
            this.opened = opened;
            fireFieldChangedListener(new FieldChangedEvent(this, OPENED, old,
                    opened));
            setChanged(true);
        }
    }

    public Date getLimited() {
        return limited;
    }

    public void setLimited(Date limited) {
        if (different(limited, this.limited)) {
            Date old = this.limited;
            this.limited = limited;
            fireFieldChangedListener(new FieldChangedEvent(this, LIMITED, old,
                    limited));
            setChanged(true);
        }
    }

    public Date getAbandoned() {
        return abandoned;
    }

    public void setAbandoned(Date abandoned) {
        if (different(abandoned, this.abandoned)) {
            Date old = this.abandoned;
            this.abandoned = abandoned;
            fireFieldChangedListener(new FieldChangedEvent(this, ABANDONED, old,
                    abandoned));
            setChanged(true);
        }
    }

    public Date getClosed() {
        return closed;
    }

    public void setClosed(Date closed) {
        if (different(closed, this.closed)) {
            Date old = this.closed;
            this.closed = closed;
            fireFieldChangedListener(new FieldChangedEvent(this, CLOSED, old,
                    closed));
            setChanged(true);
        }
    }

    public Date getDate(DateType dateType) {
        switch(dateType) {
            case ABANDONED:
                return abandoned;
            case CLOSED:
                return closed;
            case LIMITED:
                return limited;
            case OPENED:
                return opened;
            default:
                return null;
        }

    }

    public void setDate(DateType dateType, Date date) {
        switch(dateType) {
            case ABANDONED:
                setAbandoned(date); break;
            case CLOSED:
                setClosed(date); break;
            case LIMITED:
                setLimited(date); break;
            case OPENED:
                setOpened(date); break;
        }

    }
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1000);
        sb.append(getId());
        sb.append(",");
        sb.append(getBookmaker().getId());
        sb.append(",");
        sb.append(getIdentity().getId());
        sb.append(",");
        sb.append(getUsername());
        sb.append(",");
        sb.append(getPassword());
        sb.append(",");
        sb.append(getOpened() == null ? "NULL" : sdf.format(getOpened()));
        sb.append(",");
        sb.append(getLimited() == null ? "NULL" : sdf.format(getLimited()));
        sb.append(",");
        sb.append(getAbandoned() == null ? "NULL" : sdf.format(getAbandoned()));
        sb.append(",");
        sb.append(getClosed() == null ? "NULL" : sdf.format(getClosed()));
        return sb.toString();
    }



}
