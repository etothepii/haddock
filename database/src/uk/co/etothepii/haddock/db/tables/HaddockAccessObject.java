/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables;

import uk.co.etothepii.db.DataAccessObject;

/**
 *
 * @author jrrpl
 */
public abstract class HaddockAccessObject extends DataAccessObject {

    public HaddockAccessObject(int id) {
        super(id);
    }

    public HaddockAccessObject(long id) {
        super(id);
    }

}
