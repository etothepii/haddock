/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.markets.gui.renderers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import org.apache.log4j.Logger;
import uk.co.etothepii.haddock.betfair.data.RunnerPrice;
import uk.co.etothepii.haddock.markets.gui.Constants;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class RunnerPricesRenderer implements ListCellRenderer {

    private static final Logger LOG = 
            Logger.getLogger(RunnerPricesRenderer.class);

    private JPanel superPanel;
    private JPanel panel;
    private JLabel price;
    private JLabel avail;
    private JLabel matched;

    private NumberFormat priceFormat;
    private NumberFormat currFormat;

    public RunnerPricesRenderer() {
        superPanel = new JPanel(new BorderLayout());
        panel = new JPanel(new GridBagLayout());
        superPanel.add(panel);
        superPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1,
                Constants.getDefaultBorderColor()));
        superPanel.setBackground(Color.BLACK);
        priceFormat = NumberFormat.getInstance();
        priceFormat.setMaximumFractionDigits(2);
        priceFormat.setMinimumFractionDigits(0);
        currFormat = NumberFormat.getCurrencyInstance();
        price = new JLabel(priceFormat.format(1000),
                SwingConstants.CENTER);
        avail = new JLabel(currFormat.format(9999999.99),
                SwingConstants.CENTER);
        matched = new JLabel(currFormat.format(9999999.99), 
                SwingConstants.CENTER);
        price.setFont(Constants.getDefaultRunnerPriceTitleFont());
        avail.setFont(Constants.getDefaultRunnerPriceFont());
        matched.setFont(Constants.getDefaultRunnerPriceFont());
        panel.add(price, new GridBagConstraints(0, 0, 1, 1, 0d, 1d,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 1, 0), 0, 0));
        panel.add(avail, new GridBagConstraints(0, 1, 1, 1, 0d, 1d,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 1, 0), 0, 0));
        panel.add(matched, new GridBagConstraints(0, 2, 1, 1, 0d, 1d,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        superPanel.setPreferredSize(superPanel.getPreferredSize());
    }

    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        if (!(value instanceof RunnerPrice)) return null;
        RunnerPrice rp = (RunnerPrice)value;
        price.setText(priceFormat.format(rp.price));
        avail.setText(currFormat.format(rp.getAmount()));
        matched.setText(currFormat.format(rp.getMatched()));
        switch (rp.getDirection()) {
            case BACK:
                panel.setBackground(isSelected ? 
                    Constants.getDefaultSelectedBackColour() :
                    Constants.getDefaultBackColour());
                break;
            case LAY:
                panel.setBackground(isSelected ? 
                    Constants.getDefaultSelectedLayColour() :
                    Constants.getDefaultLayColour());
                break;
            case NONE:
                panel.setBackground(isSelected ? 
                    Constants.getDefaultSelectedNoneColour() :
                    Constants.getDefaultNoneColour());
                break;
        }
        return superPanel;
    }



}
