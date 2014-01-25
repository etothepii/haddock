/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.gui;

/**
 *
 * @author jrrpl
 */
public interface Editor<T> {

    public void set(T t);
    public void edit();
    public void save();

}
