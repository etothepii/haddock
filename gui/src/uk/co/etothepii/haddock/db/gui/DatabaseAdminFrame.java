/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.HaddockStatement;
import uk.co.etothepii.haddock.db.MasterController;
import uk.co.etothepii.haddock.db.factories.IdentityFactory;
import uk.co.etothepii.haddock.db.tables.Identity;
import uk.co.etothepii.haddock.db.gui.listcellrenderers.ListableCellRenderer;

/**
 *
 * @author jrrpl
 */
public final class DatabaseAdminFrame extends JFrame {

    private static final Logger LOG =
            Logger.getLogger(DatabaseAdminFrame.class);

    private IdentityPanel identityPanel;
    private HaddockListModel<Identity> identitiesModel;
    private JList identitiesList;
    private JPanel buttonPanel;
    private JButton newButton;
    private JButton editButton;
    private JButton saveButton;
    private int liveAccounts = 0;
    private int totalOnDeposit = 0;
    private final HaddockStatement getBalanceQuery;

    public DatabaseAdminFrame() {
        getBalanceQuery = new HaddockStatement("SELECT count(*) as "
                + "`liveAccounts`, sum(`balance`) as `totalOnDeposit` "
                + "FROM (SELECT b.`balance` AS balance FROM ("
                + "SELECT `bookmakerAccount` , MAX( `time` ) AS time FROM "
                + "`bookmakerAccountBalances` GROUP BY `bookmakerAccount` ) "
                + "AS a, `bookmakerAccountBalances` AS b "
                + "WHERE a.`bookmakerAccount` = b.`bookmakerAccount` "
                + "AND a.`time` = b.`time` AND b.`balance` > 0) AS x ", false);
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.WHITE);
        buttonPanel = new JPanel(new GridBagLayout());
        identityPanel = new IdentityPanel();
        identityPanel.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                if (e instanceof SavedEvent)
                    updateBalance();
            }
        });
        ArrayList<Identity> allIdentities;
        try {
            allIdentities = IdentityFactory.getFactory().getAll();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Number of Identities: " + allIdentities.size());
                for (Identity identity : allIdentities)
                    LOG.debug(identity.getSurname());
            }
        }
        catch (SQLException sqle) {
            DatabaseConduit.printSQLException(sqle);
            allIdentities = new ArrayList<Identity>();
            LOG.debug("No Identities Loaded");
        }
        identitiesModel = new HaddockListModel<Identity>(
                allIdentities, IdentityFactory.getFactory());
        identitiesList = new JList(identitiesModel);
        getContentPane().add(new JScrollPane(identitiesList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
                new GridBagConstraints(0, 0, 1, 1, 1d, 1d,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 0), 0, 0));
        getContentPane().add(identityPanel, new GridBagConstraints(
                1, 0, 1, 1, 1d, 1d, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        newButton = new JButton("New");
        editButton = new JButton("Edit");
        saveButton = new JButton("Save");
        buttonPanel.add(newButton, new GridBagConstraints(0, 0, 1, 1, 0d, 0d,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 5), 0, 0));
        buttonPanel.add(editButton, new GridBagConstraints(1, 0, 1, 1, 0d, 0d,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 5), 0, 0));
        buttonPanel.add(saveButton, new GridBagConstraints(2, 0, 1, 1, 0d, 0d,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        getContentPane().add(buttonPanel, new GridBagConstraints(
                0, 1, 2, 1, 1d, 1d, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        identitiesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        identitiesList.setCellRenderer(new ListableCellRenderer());
        identitiesList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int index = identitiesList.getSelectedIndex();
                if (index >= 0) {
                    Identity identity = identitiesModel.get(index);
                    identityPanel.set(identity);
                }
            }
        });
        newButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                identityPanel.save();
                Identity identity = new Identity("", "", "");
                identity.snapshot();
                identityPanel.set(identity);
            }
        });
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                identityPanel.edit();
            }
        });
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                identityPanel.save();
//                repaint();
            }
        });
        updateBalance();
        pack();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LOG.debug("Window Closing");
                identityPanel.save();
            }
        });
    }

    public void updateBalance() {
        DatabaseConduit conduit = MasterController.takeConduit();
        ResultSet rs = null;
        try {
            synchronized (getBalanceQuery) {
                PreparedStatement ps = getBalanceQuery.getStatement(conduit);
                rs = ps.executeQuery();
                if (rs.next()) {
                    int tempLiveAccounts = rs.getInt("liveAccounts");
                    int tempTotalOnDeposit = rs.getInt("totalOnDeposit");
                    if (tempLiveAccounts != liveAccounts ||
                             tempTotalOnDeposit != totalOnDeposit) {
                        liveAccounts = tempLiveAccounts;
                        totalOnDeposit = tempTotalOnDeposit;
                        updateTitle();
                    }
                }
            }
        }
        catch (SQLException sqle) {
            DatabaseConduit.printSQLException(sqle);
        }
        finally {
            MasterController.releaseConduit(conduit);
            if (rs != null) {
                try {
                    while (rs.next()) {}
                    rs.close();
                }
                catch (SQLException sqle) {
                    DatabaseConduit.printSQLException(sqle);
                }
            }
        }
    }

    private NumberFormat nf = NumberFormat.getCurrencyInstance();

    private void updateTitle() {
        StringBuilder sb = new StringBuilder(1000);
        sb.append("Funded Accounts: ");
        sb.append(liveAccounts);
        sb.append(" Total Funds on Deposit: ");
        sb.append(nf.format(totalOnDeposit / 100d));
        setTitle(sb.toString());
    }
}
