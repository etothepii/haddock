/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables;

import java.awt.image.BufferedImage;
import uk.co.etothepii.db.Field;
import uk.co.etothepii.db.FieldChangedEvent;
import uk.co.etothepii.haddock.db.tables.enumerations.CardMonth;
import uk.co.etothepii.haddock.db.tables.enumerations.YN;

/**
 *
 * @author jrrpl
 */
public class Card extends HaddockAccessObject implements Listable {

    public static final Field IDENTITY = new Field("identity", Identity.class,
            Card.class);
    public static final Field BANK_ACCOUNT = new Field("bankAccount",
            BankAccount.class, Card.class);
    public static final Field TYPE = new Field("type",
            String.class, Card.class);
    public static final Field NUMBER = new Field("number",
            String.class, Card.class);
    public static final Field FROM_MONTH = new Field("fromMonth",
            CardMonth.class, Card.class);
    public static final Field FROM_YEAR = new Field("fromYear",
            String.class, Card.class);
    public static final Field END_MONTH = new Field("endMonth",
            CardMonth.class, Card.class);
    public static final Field END_YEAR = new Field("endYear",
            String.class, Card.class);
    public static final Field ISSUE_NUMBER = new Field("issueNumber",
            String.class, Card.class);
    public static final Field NAME = new Field("name",
            String.class, Card.class);
    public static final Field CVV = new Field("cvv",
            String.class, Card.class);
    public static final Field PASSWORD = new Field("password",
            String.class, Card.class);
    public static final Field ACTIVE = new Field("active",
            YN.class, Card.class);
    public static final Field FRONT = new Field("front",
            BufferedImage.class, Card.class);
    public static final Field BACK = new Field("back",
            BufferedImage.class, Card.class);

    private Identity identity;
    private BankAccount bankAccount;
    private String type;
    private String number;
    private CardMonth fromMonth;
    private String fromYear;
    private CardMonth endMonth;
    private String endYear;
    private String issueNumber;
    private String name;
    private String cvv;
    private String password;
    private YN active;
    private BufferedImage front;
    private BufferedImage back;

    public Card(Identity identity, BankAccount bankAccount, String type, 
            String number, CardMonth fromMonth, String fromYear, 
            CardMonth endMonth, String endYear, String issueNumber, 
            String name, String cvv, String password, YN active, 
            BufferedImage front, BufferedImage back) {
        super(0);
        this.identity = identity;
        this.bankAccount = bankAccount;
        this.type = type;
        this.number = number;
        this.fromMonth = fromMonth;
        this.fromYear = fromYear;
        this.endMonth = endMonth;
        this.endYear = endYear;
        this.issueNumber = issueNumber;
        this.name = name;
        this.cvv = cvv;
        this.password = password;
        this.active = active;
        this.front = front;
        this.back = back;
    }

    public Card(int id, Identity identity, BankAccount bankAccount, String type, 
            String number, CardMonth fromMonth, String fromYear, 
            CardMonth endMonth, String endYear, String issueNumber, 
            String name, String cvv, String password, YN active, 
            BufferedImage front, BufferedImage back) {
        super(id);
        this.identity = identity;
        this.bankAccount = bankAccount;
        this.type = type;
        this.number = number;
        this.fromMonth = fromMonth;
        this.fromYear = fromYear;
        this.endMonth = endMonth;
        this.endYear = endYear;
        this.issueNumber = issueNumber;
        this.name = name;
        this.cvv = cvv;
        this.password = password;
        this.active = active;
        this.front = front;
        this.back = back;
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

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        if (different(bankAccount, this.bankAccount)) {
            BankAccount old = this.bankAccount;
            this.bankAccount = bankAccount;
            fireFieldChangedListener(new FieldChangedEvent(this, BANK_ACCOUNT,
                    old, bankAccount));
            setChanged(true);
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (different(type, this.type)) {
            String old = type;
            this.type = type;
            fireFieldChangedListener(new FieldChangedEvent(this, TYPE, old,
                    type));
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

    public CardMonth getFromMonth() {
        return fromMonth;
    }

    public void setFromMonth(CardMonth fromMonth) {
        if (different(fromMonth, this.fromMonth)) {
            CardMonth old = this.fromMonth;
            this.fromMonth = fromMonth;
            fireFieldChangedListener(new FieldChangedEvent(this, FROM_MONTH,
                    old, fromMonth));
            setChanged(true);
        }
    }

    public String getFromYear() {
        return fromYear;
    }

    public void setFromYear(String fromYear) {
        if (different(fromYear, this.fromYear)) {
            String old = this.fromYear;
            this.fromYear = fromYear;
            fireFieldChangedListener(new FieldChangedEvent(this, FROM_YEAR, old,
                    fromYear));
            setChanged(true);
        }
    }

    public CardMonth getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(CardMonth endMonth) {
        if (different(endMonth, this.endMonth)) {
            CardMonth old = this.endMonth;
            this.endMonth = endMonth;
            fireFieldChangedListener(new FieldChangedEvent(this, END_MONTH, old,
                    endMonth));
            setChanged(true);
        }
    }

    public String getEndYear() {
        return endYear;
    }

    public void setEndYear(String endYear) {
        if (different(endYear, this.endYear)) {
            String old = this.endYear;
            this.endYear = endYear;
            fireFieldChangedListener(new FieldChangedEvent(this, END_YEAR, old,
                    endYear));
            setChanged(true);
        }
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(String issueNumber) {
        if (different(issueNumber, this.issueNumber)) {
            String old = this.issueNumber;
            this.issueNumber = issueNumber;
            fireFieldChangedListener(new FieldChangedEvent(this, ISSUE_NUMBER,
                    old, issueNumber));
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
            fireFieldChangedListener(new FieldChangedEvent(this, NAME, old,
                    name));
            setChanged(true);
        }
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        if (different(cvv, this.cvv)) {
            String old = this.cvv;
            this.cvv = cvv;
            fireFieldChangedListener(new FieldChangedEvent(this, CVV, old,
                    cvv));
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

    public YN getActive() {
        return active;
    }

    public void setActive(YN active) {
        if (different(active, this.active)) {
            YN old = this.active;
            this.active = active;
            fireFieldChangedListener(new FieldChangedEvent(this, ACTIVE, old,
                    active));
            setChanged(true);
        }
    }

    public BufferedImage getBack() {
        return back;
    }

    public void setBack(BufferedImage back) {
        if (different(back, this.back)) {
            BufferedImage old = this.back;
            this.back = back;
            fireFieldChangedListener(new FieldChangedEvent(this, BACK, old,
                    back));
            setChanged(true);
        }
    }

    public BufferedImage getFront() {
        return front;
    }

    public void setFront(BufferedImage front) {
        if (different(front, this.front)) {
            BufferedImage old = this.front;
            this.front = front;
            fireFieldChangedListener(new FieldChangedEvent(this, FRONT, old,
                    front));
            setChanged(true);
        }
    }

    @Override
    public String getIdentifyingLabel() {
        return number;
    }

}
