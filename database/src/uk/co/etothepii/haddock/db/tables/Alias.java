/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.tables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.Field;
import uk.co.etothepii.db.FieldChangedEvent;
import uk.co.etothepii.haddock.db.tables.enumerations.Vendor;

/**
 *
 * @author jrrpl
 */
public class Alias extends HaddockAccessObject {

    public static final Field SELECTION = new Field("selection",
            Selection.class, Alias.class);
    public static final Field ALIAS = new Field("alias", String.class,
            Alias.class);
    public static final Field VENDOR = new Field("vendor", Vendor.class,
            Alias.class);

    private static final Logger LOG = Logger.getLogger(Alias.class);

    private Selection selection;
    private String alias;
    private Vendor vendor;

    public Alias(int id, Selection selection, String alias, Vendor vendor) {
        super(id);
        this.selection = selection;
        this.alias = alias;
        this.vendor = vendor;
    }

    public Alias(Selection selection, String alias, Vendor vendor) {
        this(0, selection, alias, vendor);
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        if (different(alias, this.alias)) {
            String old = this.alias;
            this.alias = alias;
            fireFieldChangedListener(
                    new FieldChangedEvent(this, ALIAS, old, alias));
            setChanged(true);
        }
    }

    public Selection getSelection() {
        return selection;
    }

    public void setSelection(Selection selection) {
        if (different(selection, this.selection)) {
            Selection old = this.selection;
            this.selection = selection;
            fireFieldChangedListener(new FieldChangedEvent(this,
                    SELECTION, old, selection));
            setChanged(true);
        }
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        if (different(vendor, this.vendor)) {
            Vendor old = this.vendor;
            this.vendor = vendor;
            fireFieldChangedListener(
                    new FieldChangedEvent(this, VENDOR, old, vendor));
            setChanged(true);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append(getId());
        sb.append(",");
        sb.append(selection == null ? "" : selection.getId());
        sb.append(",");
        sb.append(alias);
        sb.append(",");
        sb.append(vendor);
        return sb.toString();
    }

    public static Alias getBestMatch(Collection<Alias> aliases, Alias toMatch) {
        for (Alias a : aliases) {
            if (a.getId() == toMatch.getId()) return a;
        }
        for (Alias a : aliases) {
            if (a.getAlias().equals(toMatch.getAlias())) return a;
        }
        String bfString = getBFString(toMatch.alias);
        for (Alias a : aliases) {
            if (a.getAlias().equals(bfString)) return a;
        }
        bfString = bfString.toUpperCase();
        String bfStringLO = lettersOnly(bfString.toUpperCase());
        ArrayList<Alias> candidates  = new ArrayList<Alias>();
        ArrayList<Alias> lettersOnlyCandidates  = new ArrayList<Alias>();
        for (Alias a : aliases) {
            String upper = a.alias.toUpperCase();
            if (upper.equals(bfString)) return a;
            else if (upper.startsWith(bfString)) candidates.add(a);
            else if (upper.endsWith(bfString)) candidates.add(a);
            else if (bfString.startsWith(upper)) candidates.add(a);
            else if (bfString.endsWith(upper)) candidates.add(a);
            String lo = lettersOnly(upper);
            if (lo.equals(bfStringLO)) lettersOnlyCandidates.add(a);
            else if (lo.startsWith(bfStringLO)) lettersOnlyCandidates.add(a);
            else if (lo.endsWith(bfStringLO)) lettersOnlyCandidates.add(a);
            else if (bfStringLO.startsWith(lo)) lettersOnlyCandidates.add(a);
            else if (bfStringLO.endsWith(lo)) lettersOnlyCandidates.add(a);
        }
        if (candidates.size() == 1) return candidates.get(0);
        if (lettersOnlyCandidates.size() == 1)
            return lettersOnlyCandidates.get(0);
        return null;
    }

    private static String lettersOnly(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray())
            if (Character.isLetter(c))
                sb.append(c);
        return sb.toString();
    }

    private static String getBFString(String alias) {
        String[] parts = alias.split("\\(");
        StringBuilder sb = new StringBuilder(alias.length());
        for (char c : parts[0].toCharArray()) {
            if (Character.isWhitespace(c))
                sb.append(' ');
            else if (Character.isLetterOrDigit(c))
                sb.append(c);
        }
        return sb.toString();
    }
}
