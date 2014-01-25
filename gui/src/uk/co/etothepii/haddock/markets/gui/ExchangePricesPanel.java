/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.markets.gui;

import com.betfair.publicapi.types.exchange.v5.ProfitAndLoss;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import uk.co.etothepii.event.ChangeEvent;
import uk.co.etothepii.haddock.betfair.data.Exchange;
import uk.co.etothepii.haddock.betfair.data.Runner;
import uk.co.etothepii.haddock.betfair.data.event.RunnerChangeListener;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class ExchangePricesPanel extends JPanel implements RunnerChangeListener,
        Scrollable {

    private static final Logger LOG = Logger.getLogger(ExchangePricesPanel.class);

    private Exchange exchange;
    private ArrayList<RunnerDataPanel> runnerDataPanels;
    private ArrayList<RunnerPricesPanel> runnerPricesPanels;
    private final Object orderChangingSync = new Object();
    private boolean orderChanging = false;
    private long orderChangedAt;

    public ExchangePricesPanel(Exchange exchange) {
        super(new GridBagLayout());
        runnerDataPanels = new ArrayList<RunnerDataPanel>();
        runnerPricesPanels = new ArrayList<RunnerPricesPanel>();
        orderChangedAt = System.currentTimeMillis();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                LOG.debug("ExchangePricesPanel resized");
                LOG.debug(e.getComponent().getSize());
            }
        });
        setExchange(exchange);
    }

    public final void setExchange(Exchange exchange) {
        if (this.exchange != null) {
            for (Runner r : this.exchange.getRunners())
                r.removeRunnerChangeListener(this);
        }
        this.exchange = exchange;
        if (this.exchange != null) {
            ArrayList<Runner> runners = exchange.getRunners();
            Collections.sort(runners, new Comparator<Runner>() {
                public int compare(Runner o1, Runner o2) {
                    return o1.getOrderIndex() - o2.getOrderIndex();
                }
            });
            if (runners.size() > runnerDataPanels.size()) {
                int i = 0;
                for (; i < runnerDataPanels.size(); i++) {
                    runnerDataPanels.get(i).setRunner(runners.get(i));
                    runnerPricesPanels.get(i).setRunner(runners.get(i));
                    LOG.debug("runnerDataPanels.get(" + i + 
                            ").getPreferedSize(): " + 
                            runnerDataPanels.get(i).getPreferredSize());
                    LOG.debug("runnerPricesPanels.get(" + i +
                            ").getPreferedSize(): " +
                            runnerPricesPanels.get(i).getPreferredSize());
                    LOG.debug("changed runner: " + runners.get(i).selectionId);
                }
                ArrayList<RunnerDataPanel> runnerDataPanelsToAdd =
                        new ArrayList<RunnerDataPanel>();
                ArrayList<RunnerPricesPanel> runnerPricesPanelsToAdd =
                        new ArrayList<RunnerPricesPanel>();
                int addingIndex = i;
                for (; i < runners.size(); i++) {
                    RunnerDataPanel runnerDataPanel =
                            new RunnerDataPanel(runners.get(i));
                    runnerDataPanels.add(runnerDataPanel);
                    runnerDataPanelsToAdd.add(runnerDataPanel);
                    RunnerPricesPanel runnerPricesPanel =
                            new RunnerPricesPanel(runners.get(i));
                    runnerPricesPanels.add(runnerPricesPanel);
                    runnerPricesPanelsToAdd.add(runnerPricesPanel);
                    LOG.debug("runnerDataPanels.get(" + i +
                            ").getPreferedSize(): " +
                            runnerDataPanels.get(i).getPreferredSize());
                    LOG.debug("runnerPricesPanels.get(" + i +
                            ").getPreferedSize(): " +
                            runnerPricesPanels.get(i).getPreferredSize());
                    LOG.debug("added runner: " + runners.get(i).selectionId);
                }
                for (int j = 0; j < runnerDataPanelsToAdd.size(); j++) {
                    add(runnerDataPanelsToAdd.get(j), new GridBagConstraints(
                            0, j + addingIndex, 1, 1, 0.001d, 1d,
                            GridBagConstraints.WEST, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0),0, 0));
                    add(runnerPricesPanelsToAdd.get(j), new GridBagConstraints(
                            1, j + addingIndex, 1, 1, 1d, 1d,
                            GridBagConstraints.WEST,GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                }
            }
            else {
                int i = 0;
                for (; i < runners.size(); i++) {
                    runnerDataPanels.get(i).setRunner(runners.get(i));
                    runnerPricesPanels.get(i).setRunner(runners.get(i));
                    LOG.debug("runnerDataPanels.get(" + i +
                            ").getPreferedSize(): " +
                            runnerDataPanels.get(i).getPreferredSize());
                    LOG.debug("runnerPricesPanels.get(" + i +
                            ").getPreferedSize(): " +
                            runnerPricesPanels.get(i).getPreferredSize());
                    LOG.debug("changed runner: " + runners.get(i).selectionId);
                }
                for (; i < runners.size(); i++) {
                    RunnerDataPanel runnerDataPanel = runnerDataPanels.remove(i);
                    runnerDataPanel.setRunner(null);
                    remove(runnerDataPanel);
                    RunnerPricesPanel runnerPricesPanel =
                            runnerPricesPanels.remove(i);
                    runnerPricesPanel.setRunner(null);
                    remove(runnerPricesPanel);
                }
            }
            if (this.exchange != null) {
                for (Runner r : this.exchange.getRunners())
                    r.addRunnerChangeListener(this);
            }
            if (getParent() != null)
                getParent().validate();
        }
    }

    public Exchange getExchange() {
        return exchange;
    }

    public void orderIndexChanged(ChangeEvent<Runner, Integer> e) {
        try {
            orderChanged();
            synchronized (orderChangingSync) {
                if (isOrderChanging()) return;
                setOrderChanging(true);
            }
            long sleep;
            while ((sleep = getOrderChangedAt() + 1000L - 
                    System.currentTimeMillis()) > 0) {
                Thread.sleep(sleep);
            }
            sortRunners();
        }
        catch (InterruptedException ie) {}
    }

    private void sortRunners() {

    }

    private boolean isOrderChanging() {
        synchronized (orderChangingSync) {
            return orderChanging;
        }
    }

    private void setOrderChanging(boolean orderChanging) {
        synchronized (orderChangingSync) {
            this.orderChanging = orderChanging;
        }
    }

    private void orderChanged() {
        synchronized (orderChangingSync) {
            orderChangedAt = System.currentTimeMillis();
        }
    }

    private long getOrderChangedAt() {
        synchronized (orderChangingSync) {
            return orderChangedAt;
        }
    }


    public Dimension getPreferredScrollableViewportSize() {
        Dimension prefSize = getPreferredSize();
        return new Dimension(prefSize.width, 0);
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (orientation == SwingConstants.HORIZONTAL) return 0;
        if (direction > 0) {
            Component c = getComponentAt(new Point(visibleRect.width / 2 +
                    visibleRect.x, visibleRect.height + visibleRect.y));
            if (c == null) return 0;
            if (c instanceof RunnerPricesPanel) {
                RunnerPricesPanel rpp = (RunnerPricesPanel)c;
                Rectangle rppVisibleRect = rpp.getVisibleRect();
                int move = rpp.getHeight() - rppVisibleRect.height;
                if (move <= 0) {
                    throw new RuntimeException("This should not have happened "
                            + "cannot move down");
                }
                return move;
            }
            else {
                throw new RuntimeException("This should not have happened "
                        + "cannot move down");
            }
        }
        else {
            Component c = getComponentAt(new Point(visibleRect.width / 2 +
                    visibleRect.x, visibleRect.y - 1));
            if (c == null) return 0;
            if (c instanceof RunnerPricesPanel) {
                RunnerPricesPanel rpp = (RunnerPricesPanel)c;
                Rectangle rppVisibleRect = rpp.getVisibleRect();
                int move = rpp.getHeight() - rppVisibleRect.height;
                if (move <= 0) {
                    throw new RuntimeException("This should not have happened "
                            + "cannot move up");
                }
                return move;
            }
            else {
                throw new RuntimeException("This should not have happened "
                        + "cannot move up");
            }
        }
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (orientation == SwingConstants.VERTICAL) return 0;
        if (direction > 0) {
            Component c = getComponentAt(new Point(visibleRect.width / 2 +
                    visibleRect.x, visibleRect.height + visibleRect.y));
            if (c == null) return 0;
            if (c instanceof RunnerPricesPanel) {
                RunnerPricesPanel rpp = (RunnerPricesPanel)c;
                Rectangle rppVisibleRect = rpp.getVisibleRect();
                int move = visibleRect.height - rppVisibleRect.height;
                if (visibleRect.y + move + visibleRect.height > getHeight())
                    return getHeight() - visibleRect.height - visibleRect.y;
                if (move <= 0) {
                    throw new RuntimeException("This should not have happened "
                            + "cannot move down");
                }
                return move;
            }
            else {
                throw new RuntimeException("This should not have happened "
                        + "cannot move down");
            }
        }
        else {
            Component c = getComponentAt(new Point(visibleRect.width / 2 +
                    visibleRect.x, visibleRect.y - 1));
            if (c == null) return 0;
            if (c instanceof RunnerPricesPanel) {
                RunnerPricesPanel rpp = (RunnerPricesPanel)c;
                Rectangle rppVisibleRect = rpp.getVisibleRect();
                int move = visibleRect.height - rppVisibleRect.height;
                if (visibleRect.y - move < 0)
                    return visibleRect.y;
                if (move <= 0) {
                    throw new RuntimeException("This should not have happened "
                            + "cannot move up");
                }
                return move;
            }
            else {
                throw new RuntimeException("This should not have happened "
                        + "cannot move up");
            }
        }
    }

    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public void totalAmountMatchedChanged(ChangeEvent<Runner, Double> e) {}
    public void lastPriceMatchedChanged(ChangeEvent<Runner, Double> e) {}
    public void handicapChanged(ChangeEvent<Runner, Double> e) {}
    public void reductionFactorChanged(ChangeEvent<Runner, Double> e) {}
    public void vacantChanged(ChangeEvent<Runner, Boolean> e) {}
    public void asianLineIdChanged(ChangeEvent<Runner, Integer> e) {}
    public void farSPPriceChanged(ChangeEvent<Runner, Double> e) {}
    public void nearSPPriceChanged(ChangeEvent<Runner, Double> e) {}
    public void actualSPPriceChanged(ChangeEvent<Runner, Double> e) {}
    public void totalBSPBackMatchedAmountChanged(ChangeEvent<Runner, Double> e) {}
    public void totalBSPLiabilityMatchedAmount(ChangeEvent<Runner, Double> e) {}
    public void nameChanged(ChangeEvent<Runner, String> e) {}
    public void profitAndLossChanged(ChangeEvent<Runner, ProfitAndLoss> e) {}

}
