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
public class BookmakerShop extends HaddockAccessObject implements Listable {

    public static final Field BOOKMAKER = new Field("bookmaker",
            Bookmaker.class, BookmakerShop.class);
    public static final Field NUMBER = new Field("number",
            Integer.class, BookmakerShop.class);
    public static final Field ADDRESS = new Field("address",
            String.class, BookmakerShop.class);
    public static final Field POST_CODE = new Field("postCode",
            String.class, BookmakerShop.class);
    public static final Field LATITUDE = new Field("latitude",
            Float.class, BookmakerShop.class);
    public static final Field LONGITUDE = new Field("longitude",
            Float.class, BookmakerShop.class);

    private Bookmaker bookmaker;
    private Integer number;
    private String address;
    private String postCode;
    private Float latitude;
    private Float longitude;

    public BookmakerShop(Bookmaker bookmaker, Integer number, String address,
            String postCode, Float latitude, Float longitude) {
        super(0);
        this.bookmaker = bookmaker;
        this.number = number;
        this.address = address;
        this.postCode = postCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public BookmakerShop(int id, Bookmaker bookmaker, Integer number,
            String address, String postCode, Float latitude, Float longitude) {
        super(id);
        this.bookmaker = bookmaker;
        this.number = number;
        this.address = address;
        this.postCode = postCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Bookmaker getBookmaker() {
        return bookmaker;
    }

    public void setBookmaker(Bookmaker bookmaker) {
        if (different(bookmaker, this.bookmaker)) {
            Bookmaker old = this.bookmaker;
            this.bookmaker = bookmaker;
            fireFieldChangedListener(new FieldChangedEvent(this, BOOKMAKER, old,
                    bookmaker));
            setChanged(true);
        }
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(int number) {
        if (different(number, this.number)) {
            int old = this.number;
            this.number = number;
            fireFieldChangedListener(new FieldChangedEvent(this, NUMBER, old,
                    number));
            setChanged(true);
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (different(address, this.address)) {
            String old = this.address;
            this.address = address;
            fireFieldChangedListener(new FieldChangedEvent(this, ADDRESS, old,
                    address));
            setChanged(true);
        }
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        if (different(postCode, this.postCode)) {
            String old = this.postCode;
            this.postCode = postCode;
            fireFieldChangedListener(new FieldChangedEvent(this, POST_CODE, old,
                    postCode));
            setChanged(true);
        }
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        if (different(latitude, this.latitude)) {
            float old = this.latitude;
            this.latitude = latitude;
            fireFieldChangedListener(new FieldChangedEvent(this, LATITUDE, old,
                    latitude));
            setChanged(true);
        }
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        if (different(longitude, this.longitude)) {
            float old = this.longitude;
            this.longitude = longitude;
            fireFieldChangedListener(new FieldChangedEvent(this, LONGITUDE, old,
                    longitude));
            setChanged(true);
        }
    }

    public String getIdentifyingLabel() {
        StringBuilder sb = new StringBuilder();
        String[] parts = getAddress().split(",");
        sb.append(parts[0].trim());
        if (getNumber() == null) {
            sb.append(", ");
            if (parts.length > 1)
                sb.append(parts[1].trim());
        }
        else {
            StringBuilder num = new StringBuilder();
            num.append(getNumber());
            num.append(", ");
            sb.insert(0, num.toString());
        }
        return sb.toString();
    }

}
