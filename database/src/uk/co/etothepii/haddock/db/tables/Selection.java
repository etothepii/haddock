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
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class Selection extends HaddockAccessObject {

    public static final Field DISPLAY_NAME = new Field("displayName",
            String.class, Selection.class);

    private static final Logger LOG = Logger.getLogger(Selection.class);

    private String displayName;

    public Selection(int id, String displayName) {
        super(id);
        this.displayName = displayName;
    }

    public Selection(String displayName) {
        this(0, displayName);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        if (different(displayName, this.displayName)) {
            String old = this.displayName;
            this.displayName = displayName;
            fireFieldChangedListener(new FieldChangedEvent(this, DISPLAY_NAME,
                    old, displayName));
            setChanged(true);
        }
    }

}
