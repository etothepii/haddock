/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.markets.gui;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import org.apache.log4j.Logger;
import uk.co.etothepii.event.ChangeEvent;
import uk.co.etothepii.haddock.betfair.data.BackLay;
import uk.co.etothepii.haddock.betfair.data.Runner;
import uk.co.etothepii.haddock.betfair.data.RunnerPrice;
import uk.co.etothepii.haddock.betfair.data.RunnerPrices;
import uk.co.etothepii.haddock.betfair.data.event.RunnerPriceChangeListener;
import uk.co.etothepii.haddock.markets.gui.renderers.RunnerPricesRenderer;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class RunnerPricesList extends JList {

    private static final Logger LOG = Logger.getLogger(RunnerPricesList.class);

    private Runner runner;
    private RunnerPricesListModel runnerPricesListModel;

    public RunnerPricesList(Runner runner) {
        super();
        if (runner != null) {
            runnerPricesListModel =
                    new RunnerPricesListModel(this, runner.getRunnerPrices());
            setModel(runnerPricesListModel);
        }
        setLayoutOrientation(JList.HORIZONTAL_WRAP);
        setVisibleRowCount(1);
        setRunner(runner);
        setBorder(BorderFactory.createEmptyBorder());
        setCellRenderer(new RunnerPricesRenderer());
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setFixedCellHeight((int)e.getComponent().getSize().getHeight());
                LOG.debug("RunnerPricesList: " + getName() + " resized " +
                        e.getComponent().getSize().toString());
                runnerPricesListModel.allChanged();
            }
        });
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        showingEmpty = false;
    }

    public final void setRunner(Runner runner) {
        if (this.runner != null) {
            if (runnerPricesListModel != null) {
                this.runner.removeRunnerPriceChangeListener(
                        runnerPricesListModel);
            }
        }
        this.runner = runner;
        if (runner != null) {
            if (runnerPricesListModel != null) {
                runnerPricesListModel.setRunnerPrices(runner.getRunnerPrices());
            }
            else {
                runnerPricesListModel =
                        new RunnerPricesListModel(
                        this, runner.getRunnerPrices());
                setModel(runnerPricesListModel);
            }
            runner.addRunnerPriceChangeListener(runnerPricesListModel);
        }
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        Dimension prefSize = getPreferredSize();
        return new Dimension(0, prefSize.height);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return true;
    }

    private boolean showingEmpty;

    public boolean isShowingEmpty() {
        return showingEmpty;
    }

    public void setShowingEmpty(boolean showingEmpty) {
        this.showingEmpty = showingEmpty;
    }
}

class RunnerPricesListModel extends AbstractListModel
        implements RunnerPriceChangeListener {

    private RunnerPrices runnerPrices;
    private RunnerPricesList parent;

    public RunnerPricesListModel(RunnerPricesList parent,
            RunnerPrices runnerPrices) {
        this.parent = parent;
        setRunnerPrices(runnerPrices);
    }

    public final void setRunnerPrices(RunnerPrices runnerPrices) {
        this.runnerPrices = runnerPrices;
        if (runnerPrices != null) {
            fireContentsChanged(parent, 0, 349);
        }
    }

    public int getSize() {
        if (runnerPrices != null) {
            return runnerPrices.getSize();
        }
        else {
            return 0;
        }
    }

    public RunnerPrice getElementAt(int index) {
        if (runnerPrices != null) {
            return runnerPrices.get(index);
        }
        else {
            return null;
        }
    }

    public void amountChanged(ChangeEvent<RunnerPrice, Double> e) {
        int index = e.getSource().index;
        fireContentsChanged(parent, index, index);
    }

    public void directionChanged(ChangeEvent<RunnerPrice, BackLay> e) {
        int index = e.getSource().index;
        fireContentsChanged(parent, index, index);
    }

    public void totalBSPLayLiabilityChanged(ChangeEvent<RunnerPrice, Double> e) {}

    public void totalBSPBackersStakeVolumeChanged(ChangeEvent<RunnerPrice, Double> e) {}

    public void matchedChanged(ChangeEvent<RunnerPrice, Double> e) {
        int index = e.getSource().index;
        fireContentsChanged(parent, index, index);
    }

    public void allChanged() {
        fireContentsChanged(parent, 0, getSize() - 1);
    }

}
