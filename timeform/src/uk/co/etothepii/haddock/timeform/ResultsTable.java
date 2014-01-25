/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.timeform;

import org.apache.log4j.Logger;
import org.htmlcleaner.TagNode;
import uk.co.etothepii.haddock.db.tables.Race;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class ResultsTable {

    private static final Logger LOG = Logger.getLogger(ResultsTable.class);

    public final Race race;
    public final TagNode root;

    public ResultsTable(Race race, TagNode root) {
        this.race = race;
        this.root = root;
    }

}
