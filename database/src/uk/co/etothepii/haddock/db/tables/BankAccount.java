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
public class BankAccount extends HaddockAccessObject implements Listable {

    public static final Field IDENTITY = new Field("identity", Identity.class,
            BankAccount.class);
    public static final Field BANK = new Field("bank", String.class,
            BankAccount.class);
    public static final Field NAME = new Field("name", String.class,
            BankAccount.class);
    public static final Field NUMBER = new Field("number", String.class,
            BankAccount.class);
    public static final Field SORT_CODE = new Field("sortCode", String.class,
            BankAccount.class);

    private Identity identity;
    private String bank;
    private String name;
    private String number;
    private String sortCode;

    public BankAccount(Identity identity, String bank, String name,
            String number, String sortCode) {
        this(0, identity, bank, name, number, sortCode);
    }

    public BankAccount(int id, Identity identity, String bank, String name,
            String number, String sortCode) {
        super(id);
        this.identity = identity;
        this.bank = bank;
        this.name = name;
        this.number = number;
        this.sortCode = sortCode;
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

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        if (different(bank, this.bank)) {
            String old = this.bank;
            this.bank = bank;
            fireFieldChangedListener(new FieldChangedEvent(
                    this, BANK, old, bank));
            setChanged(true);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (different(name, this.name)) {
            String old = name;
            this.name = name;
            fireFieldChangedListener(new FieldChangedEvent(
                    this, NAME, old, name));
            setChanged(true);
        }
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        if (different(number, this.number)) {
            String old = this.number;
            this.number = number;
            fireFieldChangedListener(new FieldChangedEvent(this, NUMBER, old,
                    number));
            setChanged(true);
        }
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        if (different(sortCode, this.sortCode)) {
            String old = this.sortCode;
            this.sortCode = sortCode;
            fireFieldChangedListener(new FieldChangedEvent(this, SORT_CODE, old,
                    sortCode));
            setChanged(true);
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public String getIdentifyingLabel() {
        StringBuilder sb = new StringBuilder(1000);
        sb.append(bank);
        sb.append(" ");
        sb.append(sortCode);
        sb.append(" ");
        sb.append(number);
        return sb.toString();
    }

}
