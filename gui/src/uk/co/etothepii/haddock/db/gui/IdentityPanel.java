/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.factories.BookmakerAccountFactory;
import uk.co.etothepii.haddock.db.factories.BookmakerFactory;
import uk.co.etothepii.haddock.db.factories.IdentityFactory;
import uk.co.etothepii.haddock.db.tables.Bookmaker;
import uk.co.etothepii.haddock.db.tables.BookmakerAccount;
import uk.co.etothepii.haddock.db.tables.Identity;
import uk.co.etothepii.haddock.db.gui.listcellrenderers.ListableCellRenderer;

/**
 *
 * @author jrrpl
 */
public class IdentityPanel extends JPanel implements Editor<Identity> {

    private static final Logger LOG = Logger.getLogger(IdentityPanel.class);

    private JLabel firstNameLabel;
    private JLabel middleNameLabel;
    private JLabel surnameLabel;
    private JTextField firstNameField;
    private JTextField middleNameField;
    private JTextField surnameField;
    private Identity activeIdentity;
    private HaddockListModel<Bookmaker> bookmakersModel;
    private JList bookmakerList;
    private BookmakerAccountPanel bookmakerAccountPanel;
    private final ArrayList<ChangeListener> changeListeners;

    public IdentityPanel() {
        super(new GridBagLayout());
        setBackground(Color.WHITE);
//        setBorder(new LineBorder(Color.BLACK, 1));
        changeListeners = new ArrayList<ChangeListener>();
        firstNameLabel = new JLabel("First name");
        middleNameLabel = new JLabel("Middle name");
        surnameLabel = new JLabel("Surname");
        firstNameField = new JTextField();
        middleNameField = new JTextField();
        surnameField = new JTextField();
        firstNameField.setDisabledTextColor(Color.BLACK);
        middleNameField.setDisabledTextColor(Color.BLACK);
        surnameField.setDisabledTextColor(Color.BLACK);
        bookmakerAccountPanel = new BookmakerAccountPanel();
        add(firstNameLabel, new GridBagConstraints(0, 0, 1, 1, 0d, 0d,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 5, 5), 0, 0));
        add(middleNameLabel, new GridBagConstraints(1, 0, 1, 1, 0d, 0d,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 5, 5), 0, 0));
        add(surnameLabel, new GridBagConstraints(2, 0, 1, 1, 0d, 0d,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 5, 0), 0, 0));
        add(firstNameField, new GridBagConstraints(0, 1, 1, 1, 1d, 0d,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 5, 5), 0, 5));
        add(middleNameField, new GridBagConstraints(1, 1, 1, 1, 1d, 0d,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 5, 5), 0, 5));
        add(surnameField, new GridBagConstraints(2, 1, 1, 1, 1d, 0d,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 5, 0), 0, 5));
        ArrayList<Bookmaker> allBookmakers;
        try {
             allBookmakers = BookmakerFactory.getFactory().getAll();
        }
        catch (SQLException se) {
            DatabaseConduit.printSQLException(se);
            allBookmakers = new ArrayList<Bookmaker>();
        }
        bookmakersModel = new HaddockListModel<Bookmaker>(allBookmakers,
                BookmakerFactory.getFactory());
        bookmakerList = new JList(bookmakersModel);
        bookmakerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookmakerList.setCellRenderer(new ListableCellRenderer());
        bookmakerList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int index = bookmakerList.getSelectedIndex();
                if (index >= 0) {
                    Bookmaker bookmaker = (Bookmaker)bookmakersModel.get(index);
                    try {
                        BookmakerAccount account =
                                BookmakerAccountFactory.getFactory(
                                ).getFromIdentityAndBookmaker(
                                activeIdentity, bookmaker);
                        LOG.debug(account);
                        bookmakerAccountPanel.save();
                        if (account != null)
                            bookmakerAccountPanel.set(account);
                        else {
                            BookmakerAccount newAcc = new BookmakerAccount(
                                    bookmaker, activeIdentity, "", "",
                                    null, null, null, null);
                            newAcc.snapshot();
                            bookmakerAccountPanel.set(newAcc);
                        }
                    }
                    catch (SQLException sqle) {
                        DatabaseConduit.printSQLException(sqle);
                    }
                }
                else
                    bookmakerAccountPanel.set(null);
            }
        });
        JPanel bookmakerPanel = new JPanel(new GridBagLayout());
        bookmakerPanel.add(new JScrollPane(bookmakerList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), new GridBagConstraints(0, 0, 1, 1,
                1d, 1d, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));
        bookmakerPanel.add(bookmakerAccountPanel, new GridBagConstraints(
                1, 0,  1, 1,  1d, 0d, GridBagConstraints.NORTH,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        bookmakerPanel.setBackground(Color.WHITE);
        add(bookmakerPanel, new GridBagConstraints(0, 2, 3, 1, 3d, 1d,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        set(null);
    }

    public final void set(Identity activeIdentity) {
        this.activeIdentity = activeIdentity;
        if (activeIdentity == null) {
            firstNameField.setText("");
            middleNameField.setText("");
            surnameField.setText("");
            bookmakerList.setEnabled(false);
        }
        else {
            firstNameField.setText(activeIdentity.getFirstName());
            middleNameField.setText(activeIdentity.getMiddleNames());
            surnameField.setText(activeIdentity.getSurname());
            bookmakerList.setEnabled(true);
        }
        bookmakerList.clearSelection();
        bookmakerAccountPanel.set(null);
        setEditable(false);
    }

    public void setEditable(boolean editable) {
        firstNameField.setEnabled(editable);
        middleNameField.setEnabled(editable);
        surnameField.setEnabled(editable);
        bookmakerAccountPanel.setEditable(editable);
    }

    public void edit() {
        setEditable(true);
    }

    public void save() {
        if (activeIdentity != null) {
            activeIdentity.setFirstName(firstNameField.getText());
            activeIdentity.setMiddleNames(middleNameField.getText());
            activeIdentity.setSurname(surnameField.getText());
            try {
                if (activeIdentity.hasChangedSinceSnapshot()) {
                    IdentityFactory.getFactory().save(activeIdentity);
                    activeIdentity.snapshot();
                }
            }
            catch (SQLException sqle) {
                DatabaseConduit.printSQLException(sqle);
            }
            bookmakerAccountPanel.save();
            setEditable(false);
            fireChangeEvent(new SavedEvent(this));
//            repaint();
        }
    }

    public void addChangeListener(ChangeListener l) {
        synchronized (changeListeners) {
            changeListeners.add(l);
        }
    }

    public void removeChangeListener(ChangeListener l) {
        synchronized (changeListeners) {
            changeListeners.remove(l);
        }
    }

    public void fireChangeEvent(ChangeEvent e) {
        synchronized (changeListeners) {
            for (ChangeListener l : changeListeners)
                l.stateChanged(e);
        }
    }

}

class SavedEvent extends ChangeEvent {

    public SavedEvent(Object source) {
        super(source);
    }



}
