/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables;

import java.util.Date;
import uk.co.etothepii.db.Field;
import uk.co.etothepii.db.FieldChangedEvent;

/**
 *
 * @author jrrpl
 */
public class BookmakerAccountBalance extends HaddockAccessObject {

    public static final Field BOOKMAKER_ACCOUNT = new Field("bookmakerAccount", 
            BookmakerAccount.class, BookmakerAccountBalance.class);
    public static final Field BALANCE = new Field("balance", 
            Integer.class, BookmakerAccountBalance.class);
    public static final Field TIME = new Field("time", 
            Date.class, BookmakerAccountBalance.class);

    private BookmakerAccount bookmakerAccount;
    private int balance;
    private Date time;

    public BookmakerAccountBalance(BookmakerAccount bookmakerAccounts, 
            int balance, Date time) {
        super(0);
        this.bookmakerAccount = bookmakerAccounts;
        this.balance = balance;
        this.time = time;
    }
    
    public BookmakerAccountBalance(int id, BookmakerAccount bookmakerAccounts, 
            int balance, Date time) {
        super(id);
        this.bookmakerAccount = bookmakerAccounts;
        this.balance = balance;
        this.time = time;
    }

    public BookmakerAccount getBookmakerAccount() {
        return bookmakerAccount;
    }

    public void setBookmakerAccount(BookmakerAccount bookmakerAccount) {
        if (different(bookmakerAccount, this.bookmakerAccount)) {
            BookmakerAccount old = this.bookmakerAccount;
            this.bookmakerAccount = bookmakerAccount;
            fireFieldChangedListener(new FieldChangedEvent(this,
                    BOOKMAKER_ACCOUNT, old, bookmakerAccount));
            setChanged(true);
        }
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        if (different(balance, this.balance)) {
            int old = this.balance;
            this.balance = balance;
            fireFieldChangedListener(new FieldChangedEvent(this, BALANCE, old,
                    balance));
            setChanged(true);
        }
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        if (different(time, this.time)) {
            Date old = this.time;
            this.time = time;
            fireFieldChangedListener(new FieldChangedEvent(this, TIME, old,
                    time));
            setChanged(true);
        }
    }

}
