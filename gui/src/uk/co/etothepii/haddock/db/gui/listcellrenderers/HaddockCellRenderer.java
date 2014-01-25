/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.gui.listcellrenderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 *
 * @author jrrpl
 */
public abstract class HaddockCellRenderer implements ListCellRenderer {

    public static final Color BACKGROUND = new Color(255, 255, 255);
    public static final Color FOREGROUND = new Color(0, 0, 0);
    public static final Color SELECTED_NO_FOCUS = new Color(192, 192, 192);
    public static final Color SELECTED_FOCUSED = new Color(192, 192, 255);
    protected static final GridBagConstraints LABEL_CONSTRAINTS =
            new GridBagConstraints(0, 0, 1, 1, 1d, 1d,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(1, 1, 1, 1), 0, 2);

    public abstract Component getListCellRendererComponent(JList list, 
            Object value, int index, boolean isSelected, boolean cellHasFocus);

    protected JPanel getPanel(int index, boolean isSelected,
            boolean cellHasFocus) {
        JPanel toRet = new JPanel(new GridBagLayout());
        toRet.setForeground(FOREGROUND);
        if (isSelected && cellHasFocus)
            toRet.setBackground(SELECTED_FOCUSED);
        else if(isSelected)
            toRet.setBackground(SELECTED_NO_FOCUS);
        else
            toRet.setBackground(BACKGROUND);
        return toRet;
    }

    protected JLabel getLabel(String string) {
        JLabel label = new JLabel(string);
        label.setForeground(FOREGROUND);
        return label;
    }
}
