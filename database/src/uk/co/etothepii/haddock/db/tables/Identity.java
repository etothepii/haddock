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
public class Identity extends HaddockAccessObject implements Listable {

    public static final Field SURNAME = new Field("surname", String.class,
            Identity.class);
    public static final Field FIRST_NAME = new Field("firstName", String.class,
            Identity.class);
    public static final Field MIDDLE_NAME = new Field("middleName",
            String.class, Identity.class);

    private static final Logger LOG = Logger.getLogger(Identity.class);
    
    private String surname;
    private String firstName;
    private String middleNames;

    public Identity(String surname, String firstName, String middleNames) {
        super(0);
        this.surname = surname;
        this.firstName = firstName;
        this.middleNames = middleNames;
    }

    public Identity(int id, String surname, String firstName, String middleNames) {
        super(id);
        this.surname = surname;
        this.firstName = firstName;
        this.middleNames = middleNames;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public String getMiddleNames() {
        return middleNames;
    }

    public void setFirstName(String firstName) {
        if (different(firstName, this.firstName)) {
            String old = this.firstName;
            this.firstName = firstName;
            fireFieldChangedListener(new FieldChangedEvent(firstName,
                    FIRST_NAME, old, firstName));
            setChanged(true);
        }
    }

    public void setSurname(String surname) {
        if (different(surname, this.surname)) {
            String old = this.surname;
            this.surname = surname;
            fireFieldChangedListener(new FieldChangedEvent(surname, SURNAME,
                    old, surname));
            setChanged(true);
        }
    }

    public void setMiddleNames(String middleNames) {
        if (different(middleNames, this.middleNames)) {
            String old = this.middleNames;
            this.middleNames = middleNames;
            fireFieldChangedListener(new FieldChangedEvent(middleNames,
                    MIDDLE_NAME, old, middleNames));
            setChanged(true);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1000);
        sb.append(surname);
        sb.append(", ");
        sb.append(firstName);
        sb.append(" ");
        sb.append(middleNames);
        return sb.toString();
    }

    @Override
    public String getIdentifyingLabel() {
        StringBuilder surname = new StringBuilder(this.surname);
        StringBuilder forename = new StringBuilder(firstName);
        LOG.debug("Middle Name: " + getMiddleNames());
        if (middleNames != null) {
            forename.append(" ");
            forename.append(middleNames);
        }
        if (surname.length() > 0 && forename.length() > 0);
            surname.append(", ");
        surname.append(forename);
        return surname.toString();
    }

}
