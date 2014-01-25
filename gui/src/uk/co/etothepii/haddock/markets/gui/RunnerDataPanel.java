/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.markets.gui;

import com.betfair.publicapi.types.exchange.v5.MultiWinnerOddsLine;
import com.betfair.publicapi.types.exchange.v5.ProfitAndLoss;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import uk.co.etothepii.event.ChangeEvent;
import uk.co.etothepii.haddock.betfair.data.Runner;
import uk.co.etothepii.haddock.betfair.data.event.RunnerChangeListener;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class RunnerDataPanel extends JPanel implements RunnerChangeListener,
        Comparable<RunnerDataPanel> {

    private static final Logger LOG = Logger.getLogger(RunnerDataPanel.class);

    private Runner runner;
    private JLabel name;
    private JLabel selectionId;
    private JLabel totalAmountMatched;
    private JLabel lastPriceMatched;
    private JLabel farSPPrice;
    private JLabel nearSPPrice;
    private JLabel profitAndLossWin;
    private JLabel profitAndLossLose;
    private JLabel actualSPPrice;
    private JLabel totalAmountMatchedLabel;
    private JLabel lastPriceMatchedLabel;
    private JLabel farSPPriceLabel;
    private JLabel nearSPPriceLabel;
    private JLabel actualSPPriceLabel;
    private JPanel superPanel;
    private JPanel SPPanel;
    private JPanel predictedSPPanel;

    private NumberFormat currFormat;
    private NumberFormat priceFormat;
    private boolean spDeclared;

    public RunnerDataPanel(Runner runner) {
//        super(new GridBagLayout());
        super(new BorderLayout());
        superPanel = new JPanel(new GridBagLayout());
        add(superPanel);
        superPanel.
        setBackground(Color.WHITE);
        currFormat = NumberFormat.getCurrencyInstance();
        priceFormat = NumberFormat.getInstance();
        priceFormat.setMaximumFractionDigits(2);
        priceFormat.setMinimumFractionDigits(0);
        name = new JLabel(" ", SwingConstants.LEFT);
        name.setFont(Constants.getDefaultRunnerPriceTitleFont());
        selectionId = new JLabel(" ", SwingConstants.LEFT);
        selectionId.setFont(Constants.getDefaultRunnerPriceFont());
        totalAmountMatched = new JLabel(" ", SwingConstants.RIGHT);
        totalAmountMatched.setFont(Constants.getDefaultRunnerPriceFont());
        lastPriceMatched = new JLabel(" ", SwingConstants.RIGHT);
        lastPriceMatched.setFont(Constants.getDefaultRunnerPriceFont());
        farSPPrice = new JLabel(" ", SwingConstants.RIGHT);
        farSPPrice.setFont(Constants.getDefaultRunnerPriceFont());
        nearSPPrice = new JLabel(" ", SwingConstants.RIGHT);
        nearSPPrice.setFont(Constants.getDefaultRunnerPriceFont());
        profitAndLossWin = new JLabel(" ", SwingConstants.LEFT);
        profitAndLossWin.setFont(Constants.getDefaultRunnerPriceFont());
        profitAndLossWin.setForeground(Constants.getDefaultProfitColour());
        profitAndLossLose = new JLabel(" ", SwingConstants.LEFT);
        profitAndLossLose.setFont(Constants.getDefaultRunnerPriceFont());
        actualSPPrice = new JLabel(" ", SwingConstants.RIGHT);
        actualSPPrice.setFont(Constants.getDefaultRunnerPriceFont());
        totalAmountMatchedLabel = new JLabel("Matched:", SwingConstants.LEFT);
        totalAmountMatchedLabel.setFont(Constants.getDefaultRunnerPriceFont());
        lastPriceMatchedLabel = new JLabel("Last price:", SwingConstants.LEFT);
        lastPriceMatchedLabel.setFont(Constants.getDefaultRunnerPriceFont());
        farSPPriceLabel = new JLabel("Far:", SwingConstants.LEFT);
        farSPPriceLabel.setFont(Constants.getDefaultRunnerPriceFont());
        nearSPPriceLabel = new JLabel("Near:", SwingConstants.LEFT);
        nearSPPriceLabel.setFont(Constants.getDefaultRunnerPriceFont());
        actualSPPriceLabel = new JLabel("SP:", SwingConstants.LEFT);
        actualSPPriceLabel.setFont(Constants.getDefaultRunnerPriceFont());
        SPPanel = new JPanel(new GridBagLayout());
        SPPanel.setBackground(Color.WHITE);
        predictedSPPanel = new JPanel(new GridBagLayout());
        predictedSPPanel.setBackground(Color.WHITE);
        setRunner(runner);
        superPanel.
        add(name, new GridBagConstraints(0, 0, 2, 1, 1d, 1d,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 2));
        superPanel.
        add(selectionId, new GridBagConstraints(0, 1, 2, 1, 1d, 1d,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 2));
        superPanel.
        add(lastPriceMatchedLabel, new GridBagConstraints(
                0, 2, 1, 1, 1d, 1d, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 2));
        superPanel.
        add(lastPriceMatched, new GridBagConstraints(
                1, 2, 1, 1, 1d, 1d, GridBagConstraints.EAST,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 2));
        superPanel.
        add(totalAmountMatchedLabel, new GridBagConstraints(
                0, 3, 1, 1, 1d, 1d, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 2));
        superPanel.
        add(totalAmountMatched, new GridBagConstraints(
                1, 3, 1, 1, 1d, 1d, GridBagConstraints.EAST,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 2));
        superPanel.
        add(profitAndLossWin, new GridBagConstraints(
                0, 5, 1, 1, 1d, 1d, GridBagConstraints.EAST,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 2));
        superPanel.
        add(profitAndLossLose, new GridBagConstraints(
                1, 5, 1, 1, 1d, 1d, GridBagConstraints.EAST,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 2));
        SPPanel.add(farSPPriceLabel, new GridBagConstraints(2, 0, 1, 1, 0d, 0d,
                GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 5), 0, 2));
        SPPanel.add(farSPPrice, new GridBagConstraints(3, 0, 1, 1, 1d, 0d,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 2));
        SPPanel.add(nearSPPriceLabel, new GridBagConstraints(0, 0, 1, 1,
                1d, 0d, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 5), 0, 2));
        SPPanel.add(nearSPPrice, new GridBagConstraints(1, 0, 1, 1,
                0d, 1d, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 5), 0, 2));
        predictedSPPanel.add(actualSPPriceLabel, new GridBagConstraints(0, 0, 1, 1, 0d, 0d,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 2));
        predictedSPPanel.add(actualSPPrice, new GridBagConstraints(1, 0, 1, 1, 0d, 1d,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 2));
        superPanel.
        add(SPPanel, new GridBagConstraints(0, 4, 2, 1, 1d, 1d,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        spDeclared = false;
//        validate();
//        setPreferredSize(new Dimension(150, 80));
        LOG.debug("min: " + getMinimumSize());
        LOG.debug("pref: " + getPreferredSize());
        LOG.debug("max: " + getMaximumSize());
        superPanel.
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
        LOG.debug("validating");
        validate();
        LOG.debug("min: " + getMinimumSize());
        LOG.debug("pref: " + getPreferredSize());
        LOG.debug("max: " + getMaximumSize());
    }
    
    public final void setRunner(Runner runner) {
        if (this.runner != null) {
            runner.removeRunnerChangeListener(this);
        }
        this.runner = runner;
        selectionId.setText(this.runner.selectionId + "");
        String totalAmountMatchedStr = this.runner.getTotalAmountMatched(
                ) == null ? "N/A" : currFormat.format(
                this.runner.getTotalAmountMatched());
        totalAmountMatched.setText(totalAmountMatchedStr);
        String lastPriceMatchedStr = this.runner.getLastPriceMatched() == null ?
            "N/A" : priceFormat.format(this.runner.getLastPriceMatched());
        lastPriceMatched.setText(lastPriceMatchedStr);
        String farSPPriceStr = this.runner.getFarSPPrice() == null ?
            "N/A" : priceFormat.format(this.runner.getFarSPPrice());
        farSPPrice.setText(farSPPriceStr);
        String nearSPPriceStr = this.runner.getNearSPPrice() == null ?
            "N/A" : priceFormat.format(this.runner.getNearSPPrice());
        nearSPPrice.setText(nearSPPriceStr);
        String actualSPPriceStr = this.runner.getActualSPPrice() == null ?
            "N/A" : priceFormat.format(this.runner.getActualSPPrice());
        actualSPPrice.setText(actualSPPriceStr);
        LOG.debug("totalAmountMatchedStr: " + totalAmountMatchedStr);
        LOG.debug("lastPriceMatchedStr: " + lastPriceMatchedStr);
        LOG.debug("farSPPriceStr: " + farSPPriceStr);
        LOG.debug("nearSPPriceStr: " + nearSPPriceStr);
        LOG.debug("actualSPPriceStr: " + actualSPPriceStr);
        superPanel.
        remove(SPPanel);
        superPanel.
        remove(predictedSPPanel);
        if (this.runner.getActualSPPrice() == null) {
            superPanel.
            add(SPPanel, new GridBagConstraints(0, 4, 2, 1, 1d, 1d,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            spDeclared = false;
        }
        else {
            superPanel.
            add(predictedSPPanel, new GridBagConstraints(0, 4, 2, 1, 1d, 1d,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            spDeclared = true;
        }
        if (this.runner != null) {
            runner.addRunnerChangeListener(this);
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                validate();
                LOG.debug(getPreferredSize());
                }
        });
    }

    public void orderIndexChanged(ChangeEvent<Runner, Integer> e) {}

    public void totalAmountMatchedChanged(ChangeEvent<Runner, Double> e) {
        totalAmountMatched.setText(currFormat.format(e.getNewVal()));
    }

    public void lastPriceMatchedChanged(ChangeEvent<Runner, Double> e) {
        lastPriceMatched.setText(priceFormat.format(e.getNewVal()));
    }

    public void handicapChanged(ChangeEvent<Runner, Double> e) {}

    public void reductionFactorChanged(ChangeEvent<Runner, Double> e) {}

    public void vacantChanged(ChangeEvent<Runner, Boolean> e) {}

    public void asianLineIdChanged(ChangeEvent<Runner, Integer> e) {}

    public void nameChanged(ChangeEvent<Runner, String> e) {
        name.setText(e.getNewVal());
    }

    public void profitAndLossChanged(ChangeEvent<Runner, ProfitAndLoss> e) {
        ProfitAndLoss profitAndLoss = e.getNewVal();
        profitAndLossWin.setText(profitAndLoss.getIfWin() == 0 ? " " :
            currFormat.format(profitAndLoss.getIfWin()));
        profitAndLossWin.setForeground(profitAndLoss.getIfWin() < 0 ?
            Constants.getDefaultLossColour() :
            Constants.getDefaultProfitColour());
        if (profitAndLoss instanceof MultiWinnerOddsLine) {
            MultiWinnerOddsLine multiWinnerOddsLine =
                    (MultiWinnerOddsLine)profitAndLoss;
            profitAndLossLose.setText(multiWinnerOddsLine.getIfLoss() == 0 ?
                " " : currFormat.format(
                    multiWinnerOddsLine.getIfLoss()));
            profitAndLossLose.setForeground(multiWinnerOddsLine.getIfLoss() < 0 ?
                Constants.getDefaultLossColour() :
                Constants.getDefaultProfitColour());
        }
        else {
            profitAndLossLose.setText(" ");
        }
    }

    public void farSPPriceChanged(ChangeEvent<Runner, Double> e) {
        if (!spDeclared) {
            if (e.getNewVal().equals(Double.POSITIVE_INFINITY)) {
                farSPPrice.setText("Infinity");
            }
            else {
                farSPPrice.setText(priceFormat.format(e.getNewVal()));
            }
        }
    }

    public void nearSPPriceChanged(ChangeEvent<Runner, Double> e) {
        if (!spDeclared) {
            if (e.getNewVal().equals(Double.POSITIVE_INFINITY)) {
                farSPPrice.setText("Infinity");
            }
            else {
                nearSPPrice.setText(priceFormat.format(e.getNewVal()));
            }
        }
    }

    public void actualSPPriceChanged(ChangeEvent<Runner, Double> e) {
        if (!spDeclared) {
            remove(SPPanel);
            add(predictedSPPanel, new GridBagConstraints(0, 4, 2, 1, 1d, 0d,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));
        }
        actualSPPrice.setText(priceFormat.format(e.getNewVal()));
        spDeclared = true;
    }

    public void totalBSPBackMatchedAmountChanged(ChangeEvent<Runner, Double> e) {}

    public void totalBSPLiabilityMatchedAmount(ChangeEvent<Runner, Double> e) {}

    public int compareTo(RunnerDataPanel o) {
        if (runner == null && o.runner == null) return 0;
        if (o.runner == null) return 1;
        if (runner == null) return -1;
        return runner.getOrderIndex() - o.runner.getOrderIndex();
    }

}
