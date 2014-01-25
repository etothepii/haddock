/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables;

import org.apache.log4j.Logger;
import uk.co.etothepii.db.Field;
import uk.co.etothepii.db.FieldChangedEvent;

/**
 *
 * @author jrrpl
 */
public class Horse extends HaddockAccessObject {

    public static final Field ALIAS = new Field("alias",
            Alias.class, Horse.class);
    public static final Field YOB = new Field("YoB", Integer.class,
            Horse.class);

    private static final Logger LOG = Logger.getLogger(Horse.class);

    private Alias alias;
    private Integer year;

    public Horse(int id, Alias alias, Integer year) {
        super(id);
        this.alias = alias;
        this.year = year;
    }

    public Horse(Alias alias, Integer year) {
        this(0, alias, year);
    }

    public Alias getAlias() {
        return alias;
    }

    public void setAlias(Alias alias) {
        if (different(alias, this.alias)) {
            Alias old = this.alias;
            this.alias = alias;
            fireFieldChangedListener(
                    new FieldChangedEvent(this, ALIAS, old, alias));
            setChanged(true);
        }
    }

    public Integer getYearOfBirth() {
        return year;
    }

    public void setYearOfBirth(Integer year) {
        if (different(year, this.year)) {
            Integer old = this.year;
            this.year = year;
            fireFieldChangedListener(new FieldChangedEvent(this,
                    YOB, old, year));
            setChanged(true);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append(getId());
        sb.append(",");
        sb.append(alias);
        sb.append(",");
        sb.append(year);
        return sb.toString();
    }
}
