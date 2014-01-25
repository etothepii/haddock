/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables;

import uk.co.etothepii.db.Field;
import uk.co.etothepii.db.FieldChangedEvent;

/**
 *
 * @author jrrpl
 */
public class Bookmaker extends HaddockAccessObject implements Listable {

    public static final Field NAME = new Field("name", String.class, 
            Bookmaker.class);
    public static final Field ABV = new Field("abv", String.class, 
            Bookmaker.class);

    private String name;
    private String abv;

    public Bookmaker(String name, String abv) {
        super(0);
        this.name = name;
        this.abv = abv;
    }

    public Bookmaker(int id, String name, String abv) {
        super(id);
        this.name = name;
        this.abv = abv;
    }

    public void setName(String name) {
        if (different(name, this.name)) {
            String old = this.name;
            this.name = name;
            fireFieldChangedListener(new FieldChangedEvent(this, NAME, old,
                    name));
            setChanged(true);
        }
    }

    public String getName() {
        return name;
    }

    public void setAbv(String abv) {
        if (different(abv, this.abv)) {
            String old = this.abv;
            this.abv = abv;
            fireFieldChangedListener(new FieldChangedEvent(this, ABV, old,
                    abv));
            setChanged(true);
        }
    }

    public String getAbv() {
        return abv;
    }

    public String getIdentifyingLabel() {
        return name;
    }

}
