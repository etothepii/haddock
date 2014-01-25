/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.etothepii.haddock.db.tables;

import java.awt.image.BufferedImage;
import java.util.TimeZone;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.Field;
import uk.co.etothepii.db.FieldChangedEvent;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class Racecourse extends HaddockAccessObject {

    public static final Field NAME = new Field("name", String.class,
            Racecourse.class);
    public static final Field ABV = new Field("abv", String.class,
            Racecourse.class);
    public static final Field TELEPHONE = new Field("telephone", String.class,
            Racecourse.class);
    public static final Field WEB_ADDRESS = new Field("webAddress",
            String.class, Racecourse.class);
    public static final Field RAILWAY = new Field("railway", String.class,
            Racecourse.class);
    public static final Field DESCRIPTION = new Field("description",
            String.class, Racecourse.class);
    public static final Field FLAT_COURSE = new Field("flatCourse",
            BufferedImage.class, Racecourse.class);
    public static final Field NATIONAL_HUNT_COURSE = new Field(
            "nationalHuntCourse", BufferedImage.class, Racecourse.class);
    public static final Field COUNTRY = new Field("country", String.class,
            Racecourse.class);
    public static final Field TIMEZONE = new Field("timezone", TimeZone.class,
            Racecourse.class);

    private static final Logger LOG = Logger.getLogger(Racecourse.class);
    private String name;
    private String abv;
    private String telephone;
    private String webAddress;
    private String railway;
    private String description;
    private BufferedImage flatCourse;
    private BufferedImage nationalHuntCourse;
    private String country;
    private TimeZone timezone;

    public Racecourse(String name, String abv, String telephone,
            String webAddress, String railway, String description,
            BufferedImage flatCourse, BufferedImage nationalHuntCourse,
            String country, TimeZone timezone) {
        this(0, name, abv, telephone, webAddress, railway, description,
                flatCourse, nationalHuntCourse, country, timezone);
    }

    public Racecourse(int id, String name, String abv, String telephone,
            String webAddress, String railway, String description,
            BufferedImage flatCourse, BufferedImage nationalHuntCourse,
            String country, TimeZone timezone) {
        super(id);
        this.name = name;
        this.abv = abv;
        this.telephone = telephone;
        this.webAddress = webAddress;
        this.railway = railway;
        this.description = description;
        this.flatCourse = flatCourse;
        this.nationalHuntCourse = nationalHuntCourse;
        this.country = country;
        this.timezone = timezone;
    }

    public String getAbv() {
        return abv;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        if (different(country, this.country)) {
            String old = this.country;
            this.country = country;
            fireFieldChangedListener(new FieldChangedEvent(this, COUNTRY, old,
                    country));
            setChanged(true);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (different(description, this.description)) {
            String old = this.description;
            this.description = description;
            fireFieldChangedListener(new FieldChangedEvent(this, DESCRIPTION,
                    old, description));
            setChanged(true);
        }
    }

    public BufferedImage getFlatCourse() {
        return flatCourse;
    }

    public void setFlatCourse(BufferedImage flatCourse) {
        if (different(flatCourse, this.flatCourse)) {
            BufferedImage old = this.flatCourse;
            this.flatCourse = flatCourse;
            fireFieldChangedListener(new FieldChangedEvent(this, FLAT_COURSE,
                    old, flatCourse));
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

    public BufferedImage getNationalHuntCourse() {
        return nationalHuntCourse;
    }

    public void setNationalHuntCourse(BufferedImage nationalHuntCourse) {
        if (different(nationalHuntCourse, this.nationalHuntCourse)) {
            BufferedImage old = this.nationalHuntCourse;
            this.nationalHuntCourse = nationalHuntCourse;
            fireFieldChangedListener(new FieldChangedEvent(this,
                    NATIONAL_HUNT_COURSE, old, nationalHuntCourse));
            setChanged(true);
        }
    }

    public String getRailway() {
        return railway;
    }

    public void setRailway(String railway) {
        if (different(railway, this.railway)) {
            String old = this.railway;
            this.railway = railway;
            fireFieldChangedListener(new FieldChangedEvent(this, RAILWAY, old,
                    railway));
            setChanged(true);
        }
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        if (different(telephone, this.telephone)) {
            String old = this.telephone;
            this.telephone = telephone;
            fireFieldChangedListener(new FieldChangedEvent(this, TELEPHONE,
                    old, telephone));
            setChanged(true);
        }
    }

    public TimeZone getTimezone() {
        return timezone;
    }

    public void setTimezone(TimeZone timezone) {
        if (different(timezone, this.timezone)) {
            TimeZone old = this.timezone;
            this.timezone = timezone;
            fireFieldChangedListener(new FieldChangedEvent(this, TIMEZONE, old,
                    timezone));
            setChanged(true);
        }
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        if (different(webAddress, this.webAddress)) {
            String old = this.webAddress;
            this.webAddress = webAddress;
            fireFieldChangedListener(new FieldChangedEvent(this, WEB_ADDRESS,
                    old, webAddress));
            setChanged(true);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id);
        sb.append(",");
        sb.append(name);
        sb.append(",");
        sb.append(country);
        sb.append(",");
        return super.toString();
    }



}
