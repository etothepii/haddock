/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.gui.listcellrenderers;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import uk.co.etothepii.haddock.db.tables.Listable;

/**
 *
 * @author jrrpl
 */
public class ListableCellRenderer extends HaddockCellRenderer {

    private static final Logger LOG =
            Logger.getLogger(ListableCellRenderer.class);

    public ListableCellRenderer() {}

    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        Listable listable = (Listable)value;
        JPanel toRet = getPanel(index, isSelected, cellHasFocus);
        JLabel label = getLabel(listable.getIdentifyingLabel());
        LOG.debug(listable.getIdentifyingLabel());
        toRet.add(label, LABEL_CONSTRAINTS);
        return toRet;
    }

}
