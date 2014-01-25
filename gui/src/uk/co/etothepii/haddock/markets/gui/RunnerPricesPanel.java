/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.markets.gui;

import com.betfair.publicapi.types.exchange.v5.ProfitAndLoss;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.Visibility;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import org.apache.log4j.Logger;
import uk.co.etothepii.components.DirectionButton;
import uk.co.etothepii.components.DirectionEnum;
import uk.co.etothepii.event.ChangeEvent;
import uk.co.etothepii.haddock.betfair.data.Runner;
import uk.co.etothepii.haddock.betfair.data.RunnerPrice;
import uk.co.etothepii.haddock.betfair.data.event.RunnerChangeListener;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class RunnerPricesPanel extends JPanel 
        implements Comparable<RunnerPricesPanel>, RunnerChangeListener {

    private static final Logger LOG = Logger.getLogger(RunnerPricesPanel.class);

    private RunnerPricesList runnerPricesList;
    private Runner runner;
    private JButton right;
    private JButton left;
    private JButton farRight;
    private JButton farLeft;
    private JScrollPane scroller;
    private JPanel superPanel;
    private boolean scrollToOnResize;

    public RunnerPricesPanel(Runner runner) {
        super(new BorderLayout());
        superPanel = new JPanel(new GridBagLayout());
        add(superPanel);
        runnerPricesList = new RunnerPricesList(null);
        scroller = new JScrollPane(runnerPricesList,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setBorder(BorderFactory.createEmptyBorder());
        right = new DirectionButton(DirectionEnum.RIGHT);
        left = new DirectionButton(DirectionEnum.LEFT);
        farRight = new DirectionButton(DirectionEnum.FAR_RIGHT);
        farLeft = new DirectionButton(DirectionEnum.FAR_LEFT);
        superPanel.add(farLeft, new GridBagConstraints(0, 0, 1, 1, 0d, 1d,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 0), 0, 0));
        superPanel.add(left, new GridBagConstraints(1, 0, 1, 1, 0d, 1d,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 0), 0, 0));
        superPanel.add(scroller, new GridBagConstraints(2, 0, 1, 1, 1d, 1d,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        superPanel.add(right, new GridBagConstraints(3, 0, 1, 1, 0d, 1d,
                GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 0), 0, 0));
        superPanel.add(farRight, new GridBagConstraints(4, 0, 1, 1, 0d, 1d,
                GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 0), 0, 0));
        left.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Rectangle visibleRect = runnerPricesList.getVisibleRect();
                int left = runnerPricesList.getScrollableUnitIncrement(
                        visibleRect, SwingConstants.HORIZONTAL, -1);
                LOG.debug("left: " + left);
                Rectangle scrollTo = new Rectangle(visibleRect.x - left,
                        visibleRect.y, visibleRect.width, visibleRect.height);
                runnerPricesList.scrollRectToVisible(scrollTo);
                LOG.debug("visibleRect: " + visibleRect.toString());
                LOG.debug("scrollTo: " + scrollTo.toString());
            }
        });
        farLeft.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Rectangle visibleRect = runnerPricesList.getVisibleRect();
                int left = runnerPricesList.getScrollableBlockIncrement(
                        visibleRect, SwingConstants.HORIZONTAL, -1);
                LOG.debug("left: " + left);
                Rectangle scrollTo = new Rectangle(visibleRect.x - left,
                        visibleRect.y, visibleRect.width, visibleRect.height);
                runnerPricesList.scrollRectToVisible(scrollTo);
                LOG.debug("visibleRect: " + visibleRect.toString());
                LOG.debug("scrollTo: " + scrollTo.toString());
            }
        });
        right.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Rectangle visibleRect = runnerPricesList.getVisibleRect();
                int right = runnerPricesList.getScrollableUnitIncrement(
                        visibleRect, SwingConstants.HORIZONTAL, 1);
                LOG.debug("right: " + right);
                Rectangle scrollTo = new Rectangle(visibleRect.x + right,
                        visibleRect.y, visibleRect.width,
                        visibleRect.height);
                runnerPricesList.scrollRectToVisible(scrollTo);
                LOG.debug("visibleRect: " + visibleRect.toString());
                LOG.debug("scrollTo: " + scrollTo.toString());
            }
        });
        farRight.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Rectangle visibleRect = runnerPricesList.getVisibleRect();
                int right = runnerPricesList.getScrollableBlockIncrement(
                        visibleRect, SwingConstants.HORIZONTAL, 1);
                LOG.debug("right: " + right);
                Rectangle scrollTo = new Rectangle(visibleRect.x +
                        right, visibleRect.y, visibleRect.width,
                        visibleRect.height);
                runnerPricesList.scrollRectToVisible(scrollTo);
                LOG.debug("visibleRect: " + visibleRect.toString());
                LOG.debug("scrollTo: " + scrollTo.toString());
            }
        });
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                if (isScrollToOnResize()) {
                    setScrollToOnResize(false);
                    scrollToLastPriceMatched();
                }
            }

        });
        setRunner(runner);
        superPanel.setBorder(BorderFactory.createEmptyBorder());
        setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, Constants.getDefaultBorderColor()));
//        LOG.debug("Constructor");
//        LOG.debug("min:  " + getMinimumSize().toString());
//        LOG.debug("pref: " + getPreferredSize().toString());
//        LOG.debug("max:  " + getMaximumSize().toString());
//        LOG.debug("validating");
//        validate();
//        LOG.debug("min:  " + getMinimumSize().toString());
//        LOG.debug("pref: " + getPreferredSize().toString());
//        LOG.debug("max:  " + getMaximumSize().toString());
    }

    public final void setRunner(Runner runner) {
        if (this.runner != null) {
            this.runner.removeRunnerChangeListener(this);
        }
        this.runner = runner;
        if (runner != null) {
            this.runner.addRunnerChangeListener(this);
        }
        runnerPricesList.setRunner(runner);
//        LOG.debug("Set runner");
//        LOG.debug("min:  " + getMinimumSize().toString());
//        LOG.debug("pref: " + getPreferredSize().toString());
//        LOG.debug("max:  " + getMaximumSize().toString());
//        LOG.debug("validating");
        if (getParent() != null) {
            getParent().validate();
        }
        scrollToLastPriceMatched();
//        LOG.debug("min:  " + getMinimumSize().toString());
//        LOG.debug("pref: " + getPreferredSize().toString());
//        LOG.debug("max:  " + getMaximumSize().toString());
    }

    public synchronized void setScrollToOnResize(boolean scrollToOnResize) {
        this.scrollToOnResize = scrollToOnResize;
    }

    public synchronized boolean isScrollToOnResize() {
        return scrollToOnResize;
    }

    public void scrollToLastPriceMatched() {
        Rectangle vis = getVisibleRect();
        if (vis.width > 0 && vis.height > 0) {
            if (getParent() != null) {
                Double lpm = this.runner.getLastPriceMatched();
                if (lpm != null)
                    scrollToPrice(lpm);
            }
        }
        else {
            setScrollToOnResize(true);
        }
    }



    public void orderIndexChanged(ChangeEvent<Runner, Integer> e) {}
    public void totalAmountMatchedChanged(ChangeEvent<Runner, Double> e) {}
    public void lastPriceMatchedChanged(ChangeEvent<Runner, Double> e) {
            LOG.debug("Last price matched changed");
            Double d = e.getNewVal();
            if (d != null) {
                scrollToPrice(d);
            }
    }

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
    
    public int compareTo(RunnerPricesPanel o) {
        if (o.runner == null && runner == null) return 0;
        if (o.runner == null) return -1;
        if (runner == null) return 1;
        return runner.compareTo(o.runner);
    }

    public void scrollToPrice(double price) {
        if (runnerPricesList.getModel() != null) {
            int index = RunnerPrice.getIndex(price);
            LOG.debug("index: " + index);
            Point p = runnerPricesList.indexToLocation(index);
            LOG.debug("p: " + p.toString());
            Rectangle vis = runnerPricesList.getVisibleRect();
            LOG.debug("vis: " + vis.toString());
            int x = p.x - vis.width / 2;
            LOG.debug("x: " + x);
            LOG.debug("vis: " + vis.toString());
            if (x < 0) x = 0;
            else if(x + vis.width > runnerPricesList.getWidth())
                x = runnerPricesList.getWidth() - vis.width;
            LOG.debug("x: " + x);
            Rectangle scrollTo = new Rectangle(x, 0, vis.width, vis.height);
            LOG.debug("scrollTo: " + scrollTo.toString());
            runnerPricesList.scrollRectToVisible(scrollTo);
        }
    }

}
