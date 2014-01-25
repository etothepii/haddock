/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables;

import java.util.Date;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.Field;
import uk.co.etothepii.db.FieldChangedEvent;
import uk.co.etothepii.haddock.db.tables.enumerations.NonrunnerResponse;

/**
 *
 * @author jrrpl
 */
public class Nonrunner extends HaddockAccessObject {

    public static final Field ALIAS = new Field("alias", Alias.class,
            Nonrunner.class);
    public static final Field RACE = new Field("race", Race.class,
            Nonrunner.class);
    public static final Field FOUND = new Field("found", Date.class,
            Nonrunner.class);
    public static final Field BF_FOUND = new Field("bfFound", Date.class,
            Nonrunner.class);
    public static final Field DECLARED = new Field("declared", Date.class,
            Nonrunner.class);
    public static final Field BF_DECLARED = new Field("bfDeclared", Date.class,
            Nonrunner.class);
    public static final Field NONRUNNER_RESPONSE = new Field(
            "nonrunnerResponse", NonrunnerResponse.class, Nonrunner.class);

    private static final Logger LOG = Logger.getLogger(Nonrunner.class);
    
    private Alias alias;
    private Race race;
    private Date found;
    private Date bfFound;
    private Date declared;
    private Date bfDeclared;
    private NonrunnerResponse nonrunnerResponse;

    public Nonrunner(Alias alias, Race race, Date found, Date bfFound,
            Date declared, Date bfDeclared,
            NonrunnerResponse nonrunnerResponse) {
        this(0, alias, race, found, bfFound, declared, bfDeclared,
                nonrunnerResponse);
    }

    public Nonrunner(int id, Alias alias, Race race, Date found, Date bfFound,
            Date declared, Date bfDeclared, NonrunnerResponse nonrunnerResponse) {
        super(id);
        this.alias = alias;
        this.race = race;
        this.found = found;
        this.bfFound = bfFound;
        this.declared = declared;
        this.bfDeclared = bfDeclared;
        this.nonrunnerResponse = nonrunnerResponse;
    }

    public Alias getAlias() {
        return alias;
    }

    public Race getRace() {
        return race;
    }

    public Date getFound() {
        return found;
    }

    public Date getBFFound() {
        return bfFound;
    }

    public void setBFFound(Date bfFound) {
        if (different(this.bfFound, bfFound)) {
            Date old = this.bfFound;
            this.bfFound = bfFound;
            fireFieldChangedListener(new FieldChangedEvent(this, BF_FOUND, old,
                    bfFound));
            setChanged(true);
        }
    }

    public void setBFDeclared(Date bfDeclared) {
        if (different(this.bfFound, bfDeclared)) {
            Date old = this.bfDeclared;
            this.bfDeclared = bfDeclared;
            fireFieldChangedListener(new FieldChangedEvent(this, BF_DECLARED,
                    old, bfDeclared));
            setChanged(true);
        }
    }

    public Date getDeclared() {
        return declared;
    }

    public Date getBFDeclared() {
        return bfDeclared;
    }



    public NonrunnerResponse getNonrunnerResponse() {
        return nonrunnerResponse;
    }

    public void setNonrunnerResponse(NonrunnerResponse nonrunnerResponse) {
        if (different(this.nonrunnerResponse, nonrunnerResponse)) {
            NonrunnerResponse old = this.nonrunnerResponse;
            this.nonrunnerResponse = nonrunnerResponse;
            fireFieldChangedListener(new FieldChangedEvent(this,
                    NONRUNNER_RESPONSE, old, nonrunnerResponse));
            setChanged(true);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getId());
        sb.append(",");
        if (getAlias() != null)
            sb.append(getAlias().getId());
        else
            sb.append("null");
        sb.append(",");
        if (getRace() != null)
            sb.append(getRace().getId());
        else
            sb.append("null");
        sb.append(",");
        sb.append(found);
        sb.append(",");
        sb.append(declared);
        sb.append(",");
        if (bfFound != null) {
            sb.append(bfFound);
            sb.append(",");
            sb.append(bfDeclared);
        }
        else if (nonrunnerResponse != null)
            sb.append(nonrunnerResponse.toString());
        else
            sb.append("null");
        sb.append(",");
        return sb.toString();
    }

}

class FoundType {



}