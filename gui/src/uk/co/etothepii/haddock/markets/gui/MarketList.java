/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.markets.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import uk.co.etothepii.haddock.db.factories.BFEventFactory;
import uk.co.etothepii.haddock.db.factories.BFMarketFactory;
import uk.co.etothepii.haddock.db.tables.BFEvent;
import uk.co.etothepii.haddock.db.tables.BFMarket;
import uk.co.etothepii.haddock.markets.events.MarketChangedEvent;
import uk.co.etothepii.haddock.markets.events.MarketChangedListener;
import uk.co.etothepii.renderers.TextRenderer;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class MarketList extends JPanel implements MarketAndEventProcessor {

    public static final Color BORDER_COLOUR = new Color(133,133,133);

    public static final Color CHILD_BACKGROUND = new Color(255, 255, 255);
    public static final Color CHILD_FOREGROUND = new Color(0, 99, 181);
    public static final Color CHILD_SELECTED_BACKGROUND = new Color(217, 232, 234);
    public static final Color CHILD_SELECTED_FOREGROUND = Color.BLACK;
    public static final Color CHILD_EVENT_BORDER_COLOUR = BORDER_COLOUR;
    public static final Color CHILD_MARKET_BORDER_COLOUR = Color.LIGHT_GRAY;

    public static final Color HIERARCHY_BACKGROUND = new Color(231, 231, 231);
    public static final Color HIERARCHY_FOREGROUND = Color.BLACK;
    public static final Color HIERARCHY_SELECTED_BACKGROUND = Color.WHITE;
    public static final Color HIERARCHY_SELECTED_FOREGROUND = HIERARCHY_FOREGROUND;
    public static final Color HIERARCHY_BORDER_COLOUR = BORDER_COLOUR;

    private static final Logger LOG = Logger.getLogger(MarketList.class);

    private JList hierarchy;
    private JList children;
    private JLabel allMarkets;
    private EventHierarcyModel hierarchyModel;
    private ChildrenModel childrenModel;
    private JScrollPane scroller;
    private ArrayList<MarketChangedListener> marketChangedListeners;
    private final Object marketChangedListenersSync = new Object();
    private BFMarket activeMarket;

    public MarketList() {
        super(new GridBagLayout());
        allMarkets = new JLabel("All Markets");
        activeMarket = null;
        JPanel allMarketsPanel = new JPanel(new BorderLayout());
        allMarketsPanel.add(allMarkets);
        allMarketsPanel.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, BORDER_COLOUR));
        allMarketsPanel.setBackground(new Color(210, 210, 210));
        marketChangedListeners = new ArrayList<MarketChangedListener>();
        hierarchyModel = new EventHierarcyModel(this);
        childrenModel = new ChildrenModel(this);
        hierarchy = new JList(hierarchyModel);
        children = new JList(childrenModel);
        hierarchy.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        children.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hierarchy.setBorder(BorderFactory.createEmptyBorder());
        children.setBorder(BorderFactory.createEmptyBorder());
        scroller = new JScrollPane(children,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setBorder(BorderFactory.createEmptyBorder());
        add(allMarketsPanel, new GridBagConstraints(0, 0, 1, 1, 1d, 0d,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 2, 0, 0), 0, 2));
        setBackground(Color.WHITE);
        add(hierarchy, new GridBagConstraints(0, 1, 1, 1, 1d, 0d,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        add(scroller, new GridBagConstraints(0, 2, 1, 1, 1d, 1d,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        hierarchy.setCellRenderer(new HierarchyCellRenderer());
        children.setCellRenderer(new ChildrenCellRenderer());
        hierarchy.addListSelectionListener(hierarchyModel);
        children.addListSelectionListener(childrenModel);
        allMarkets.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                processEvent((BFEvent)null);
            }
        });
        /**
         * These listeners make sure that only one list selection event can
         * be processed for each button press.
         *
         * The problem is that I can see that if the mouse got clicked twice
         * or something then we'd have a problem.
         */
        hierarchy.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                hierarchyModel.buttonPressed();
            }
        });
        children.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                childrenModel.buttonPressed();
            }
        });
        setPreferredSize(new Dimension(200, 600));
    }

    public void processEvent(BFEvent event) {
        hierarchyModel.select(event);
        childrenModel.setParent(event);
    }

    public void processMarket(BFMarket market) {
        setActiveMarket(market);
    }
    
    private void setActiveMarket(final BFMarket activeMarket) {
        if ((activeMarket == null ^ this.activeMarket == null) || 
                (this.activeMarket != null && activeMarket != null &&
                !this.activeMarket.equals(activeMarket))) {
            this.activeMarket = activeMarket;
            synchronized (marketChangedListenersSync) {
                for (final MarketChangedListener l :
                    marketChangedListeners) {
                    new Thread(new Runnable() {
                        public void run() {
                            l.marketChanged(new MarketChangedEvent(
                                    this, activeMarket));
                        }
                    }).start();
                }
            }
        }
    }

    public void addMarketChangedEvent(MarketChangedListener e) {
        synchronized (marketChangedListenersSync) {
            marketChangedListeners.add(e);
        }
    }
    public void removeMarketChangedEvent(MarketChangedListener e) {
        synchronized (marketChangedListenersSync) {
            marketChangedListeners.remove(e);
        }
    }
}

interface MarketAndEventProcessor {

    public void processMarket(BFMarket market);
    public void processEvent(BFEvent event);

}

class ChildrenCellRenderer extends EventAndMarketCellRenderer {

    public ChildrenCellRenderer() {
        this(DEFAULT_CONSTRAINTS);
    }

    public ChildrenCellRenderer(GridBagConstraints constraints) {
        super(constraints);
        getPanel().setBackground(MarketList.CHILD_BACKGROUND);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof BFEvent) {
            getPanel().setBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0,
                    MarketList.CHILD_EVENT_BORDER_COLOUR));
        }
        else {
            getPanel().setBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0,
                    MarketList.CHILD_MARKET_BORDER_COLOUR));
        }
        if (isSelected) {
            if (!getLabel().getFont().isBold()) {
                Font f = getLabel().getFont();
                getLabel().setFont(new Font(f.getName(), Font.BOLD,
                        f.getSize()));
            }
            getPanel().setBackground(MarketList.CHILD_SELECTED_BACKGROUND);
            getLabel().setForeground(MarketList.CHILD_SELECTED_FOREGROUND);
        }
        else {
            if (getLabel().getFont().isBold()) {
            Font f = getLabel().getFont();
                getLabel().setFont(new Font(f.getName(), Font.PLAIN,
                        f.getSize()));
            }
            getPanel().setBackground(MarketList.CHILD_BACKGROUND);
            getLabel().setForeground(MarketList.CHILD_FOREGROUND);
        }
        return super.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);
    }

    @Override
    protected void colorPanel(int row, boolean isSelected, boolean hasFocus) {}

}

class HierarchyCellRenderer extends EventAndMarketCellRenderer {

    public HierarchyCellRenderer() {
        this(DEFAULT_CONSTRAINTS);
    }

    public HierarchyCellRenderer(GridBagConstraints constraints) {
        super(constraints);
        getLabel().setForeground(MarketList.HIERARCHY_FOREGROUND);
        getPanel().setBackground(MarketList.HIERARCHY_BACKGROUND);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        if (index == list.getModel().getSize() - 1) {
            getPanel().setBackground(MarketList.HIERARCHY_SELECTED_BACKGROUND);
            getLabel().setBackground(MarketList.HIERARCHY_SELECTED_FOREGROUND);
            if (!getLabel().getFont().isBold()) {
                Font f = getLabel().getFont();
                getLabel().setFont(new Font(f.getName(), Font.BOLD,
                        f.getSize()));
            }
            getPanel().setBorder(
                    BorderFactory.createEmptyBorder());
        }
        else {
            getPanel().setBackground(MarketList.HIERARCHY_BACKGROUND);
            getLabel().setBackground(MarketList.HIERARCHY_FOREGROUND);
            if (getLabel().getFont().isBold()) {
                Font f = getLabel().getFont();
                getLabel().setFont(new Font(f.getName(), Font.PLAIN,
                        f.getSize()));
            }
            getPanel().setBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, 
                    MarketList.HIERARCHY_BORDER_COLOUR));
        }
        return super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
    }

    @Override
    protected void colorPanel(int row, boolean isSelected, boolean hasFocus) {}
}

class EventAndMarketCellRenderer extends TextRenderer {

    public EventAndMarketCellRenderer(GridBagConstraints constraints) {
        super(constraints);
    }

    @Override
    public String getText(Object value) {
        if (value instanceof BFEvent) {
            return ((BFEvent)value).getName();
        }
        else if (value instanceof BFMarket) {
            return ((BFMarket)value).getDisplayName();
        }
        else return "";
    }

}

class EventHierarcyModel extends AbstractListModel
        implements ListSelectionListener {

    private static final Logger LOG = Logger.getLogger(MarketList.class);

    private ArrayList<BFEvent> hierarcy;
    private MarketAndEventProcessor processor;

    public EventHierarcyModel(MarketAndEventProcessor processor) {
        hierarcy = new ArrayList<BFEvent>();
        this.processor = processor;
    }

    public synchronized int getSize() {
        return hierarcy.size();
    }

    public synchronized Object getElementAt(int index) {
        return hierarcy.get(index);
    }

    public synchronized void select(BFEvent selected) {
        /**
         * Check that the last in the current hierarchy is not already the
         * chosen level (this is the definition of equal hierarchies).
         * But before that check that the hierarchy isn't empty
         * as if is empty we then the we know they are not the same
         */
        if (selected == null) {
            final int size = hierarcy.size();
            hierarcy.clear();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    LOG.debug("firing interval removed");
                    fireIntervalRemoved(this, 0, size - 1);
                    LOG.debug("fired interval removed");
                }
            });
        }
        else if(hierarcy.isEmpty()
                || !hierarcy.get(hierarcy.size() - 1).equals(selected)) {

            /**
             * If the current hierarchy contains the newly selected level
             * then just delete the levels below it
             */
            if (hierarcy.contains(selected)) {
                final int from = hierarcy.lastIndexOf(selected) + 1;
                final int to = hierarcy.size() - 1;
                while (hierarcy.size() > from)
                    hierarcy.remove(from);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        LOG.debug("firing interval removed");
                        fireIntervalRemoved(this, from, to);
                        LOG.debug("fired interval removed");
                    }
                });
            }

            /**
             * Otherwise remove all levels until finding a common ancestor
             * (with an empty hierarchy being the common ancestor of all)
             * and then add the required children.
             */
            else {
                ArrayList<BFEvent> newElts = new ArrayList<BFEvent>();
                newElts.add(selected);
                BFEvent parent = selected.getParent();
                int from = -1;
                final int oldlastIndex = hierarcy.size() - 1;
                while (parent != null) {
                    if ((from = hierarcy.lastIndexOf(parent)) != -1) {
                        break;
                    }
                    newElts.add(0, parent);
                    parent = parent.getParent();
                }
                from++;
                while (hierarcy.size() > from)
                    hierarcy.remove(from);
                hierarcy.addAll(newElts);
                final int newLastIndex = hierarcy.size() - 1;
                final int start = from;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        LOG.debug("firing contents changed");
                        fireContentsChanged(this, start,
                                Math.min(newLastIndex, oldlastIndex));
                        LOG.debug("fired contents changed");
                    }
                });
                if (oldlastIndex > newLastIndex) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            LOG.debug("firing interval removed");
                            fireIntervalRemoved(this, newLastIndex + 1,
                                    oldlastIndex);
                            LOG.debug("fired interval removed");
                        }
                    });
                }
                else if (oldlastIndex < newLastIndex) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            LOG.debug("firing interval added");
                            fireIntervalAdded(this, oldlastIndex + 1, newLastIndex);
                            LOG.debug("fired interval added");
                        }
                    });
                }
            }
        }
    }

    private boolean buttonPressed = false;

    private final Object buttonPressedSync = new Object();

    public boolean startProcessing() {
        synchronized (buttonPressedSync) {
            if (!buttonPressed)
                return false;
            buttonPressed = false;
            return true;
        }
    }

    public void buttonPressed() {
        synchronized (buttonPressedSync) {
            buttonPressed = true;
        }
    }

    public void valueChanged(final ListSelectionEvent e) {
        final JList source = (JList)e.getSource();
        LOG.debug("firstIndex: " + e.getFirstIndex());
        LOG.debug("lastIndex: " + e.getLastIndex());
        LOG.debug("valueIsAdjusting: " + e.getValueIsAdjusting());
        LOG.debug("source.getSelectedIndex(): " +
                        source.getSelectedIndex());
        if (!e.getValueIsAdjusting() && startProcessing()) {
            new Thread(new Runnable() {
                public void run() {
                    LOG.debug("Thread started");
                    int index = source.getSelectedIndex();
                    LOG.debug("index: " + index);
                    Object val = getElementAt(index);
                    if (val != null) {
                        if (val instanceof BFEvent)
                            processor.processEvent((BFEvent)val);
                    }
                    source.getSelectionModel().setValueIsAdjusting(true);
                    source.getSelectionModel().clearSelection();
                    source.getSelectionModel().setValueIsAdjusting(false);
                }
            }).start();
        }
    }
}

class ChildrenModel extends AbstractListModel implements ListSelectionListener {

    private static final Logger LOG = Logger.getLogger(MarketList.class);

    private ArrayList<BFEvent> subEvents;
    private ArrayList<BFMarket> subMarkets;
    private BFEvent parent;
    private MarketAndEventProcessor processor;

    public ChildrenModel(MarketAndEventProcessor processor) {
        LOG.debug("Constructing Children Model");
        this.processor = processor;
        subEvents = new ArrayList<BFEvent>();
        subMarkets = new ArrayList<BFMarket>();
        processParent(null);
    }

    public synchronized int getSize() {
        return subEvents.size() + subMarkets.size();
    }

    public synchronized Object getElementAt(int index) {
        if (index > getSize())
            throw new IndexOutOfBoundsException(
                    "Index: " + index + ", Size: " + getSize());
        if (index < subEvents.size()) {
            return subEvents.get(index);
        }
        else
            return subMarkets.get(index - subEvents.size());
    }

    public synchronized void setParent(BFEvent parent) {
        if (this.parent == null && parent == null)
            return;
        else if(this.parent == null ^ parent == null)
            processParent(parent);
        else if (this.parent.equals(parent))
            return;
        else
            processParent(parent);
    }

    private void processParent(BFEvent parent) {
        final int oldlastIndex = subEvents.size() + subMarkets.size() - 1;
        LOG.debug("Processing parent: " +
                (parent == null ? "null" : parent.toString()));
        this.parent = parent;
        subEvents.clear();
        subMarkets.clear();
        try {
            List<BFEvent> newEvents = BFEventFactory.getFactory(
                    ).getBFActiveEventChildren(parent);
            List<BFMarket> newMarkets = BFMarketFactory.getFactory(
                    ).getBFEventUnclosedChildren(parent);
            Collections.sort(newEvents, BFEvent.ALPHABETICAL);
            Collections.sort(newMarkets, BFMarket.ALPHABETICAL);
            LOG.debug("newEvents.size(): " + newEvents.size());
            LOG.debug("newMarkets.size(): " + newMarkets.size());
            subEvents.addAll(newEvents);
            subMarkets.addAll(newMarkets);
        }
        catch (SQLException sqle) {
            LOG.error(sqle.getMessage(), sqle);
        }
        final int newLastIndex = subEvents.size() + subMarkets.size() - 1;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LOG.debug("firing contents changed");
                fireContentsChanged(this, 0,
                            Math.min(newLastIndex, oldlastIndex));
                LOG.debug("fired contents changed");
            }
        });
        if (oldlastIndex > newLastIndex) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    LOG.debug("firing interval removed");
                    fireIntervalRemoved(this, newLastIndex + 1, oldlastIndex);
                    LOG.debug("fired interval removed");
                }
            });
        }
        else if (oldlastIndex < newLastIndex) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    LOG.debug("firing interval added");
                    fireIntervalAdded(this, oldlastIndex + 1, newLastIndex);
                    LOG.debug("fired interval added");
                }
            });
        }
    }

    private boolean buttonPressed = false;

    private final Object buttonPressedSync = new Object();

    public boolean startProcessing() {
        synchronized (buttonPressedSync) {
            if (!buttonPressed)
                return false;
            buttonPressed = false;
            return true;
        }
    }

    public void buttonPressed() {
        synchronized (buttonPressedSync) {
            buttonPressed = true;
        }
    }

    public void valueChanged(final ListSelectionEvent e) {
        final JList source = (JList)e.getSource();
        LOG.debug("firstIndex: " + e.getFirstIndex());
        LOG.debug("lastIndex: " + e.getLastIndex());
        LOG.debug("valueIsAdjusting: " + e.getValueIsAdjusting());
        LOG.debug("source.getSelectedIndex(): " +
                        source.getSelectedIndex());
        if (!e.getValueIsAdjusting() && startProcessing()) {
            new Thread(new Runnable() {
                public void run() {
                    LOG.debug("Thread started");
                    int index = source.getSelectedIndex();
                    LOG.debug("index: " + index);
                    Object val = getElementAt(index);
                    if (val != null) {
                        if (val instanceof BFEvent) {
                            processor.processEvent((BFEvent)val);
                            source.getSelectionModel().clearSelection();
                        }
                        else if (val instanceof BFMarket) {
                            processor.processMarket((BFMarket)val);
                        }
                    }
                }
            }).start();
        }
    }

}
