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
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.factories.BookmakerAccountBalanceFactory;
import uk.co.etothepii.haddock.db.factories.BookmakerAccountFactory;
import uk.co.etothepii.haddock.db.tables.BookmakerAccount;
import uk.co.etothepii.haddock.db.tables.BookmakerAccountBalance;

/**
 *
 * @author jrrpl
 */
public class BookmakerAccountPanel extends JPanel 
        implements Editor<BookmakerAccount> {

    private static final Logger LOG = 
            Logger.getLogger(BookmakerAccountPanel.class);

    private static final Logger LOG_SIZE = 
            Logger.getLogger(BookmakerAccountPanel.class.getName() + ".size");

    private BookmakerAccount account;
    private JLabel bookmakerLabel;
    private JLabel bookmaker;
    private JLabel usernameLabel;
    private JLabel balanceLabel;
    private JTextField usernameTextField;
    private JFormattedTextField balanceTextField;
    private JLabel passwordLabel;
    private JTextField passwordTextField;
    private JSpinner[] dateSpinner;
    private JLabel[] dateLabels;
    private SpinnerDateModel[] dateSpinnerDateModel;
    private JCheckBox[] dateCheckBox;
    private static final Date startDate;
    private static final Date endDate;
    private static final Date today;
    private int bookmakerAccountBalance;

    static {
        Calendar startCal = Calendar.getInstance();
        startCal.clear();
        startCal.set(2009, 0, 1);
        startDate = new Date(startCal.getTimeInMillis());
        Calendar endCal = Calendar.getInstance();
        endCal.add(Calendar.YEAR, 1);
        endDate = new Date(endCal.getTimeInMillis());
        today = new Date(System.currentTimeMillis());
    }

    public BookmakerAccountPanel() {
        super(new GridBagLayout());
        setBackground(Color.WHITE);
        bookmakerLabel = new JLabel("Bookmaker");
        bookmaker = new JLabel();
        add(bookmakerLabel, new GridBagConstraints(0, 0, 1, 1, 0d, 0d,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 5, 5), 0, 5));
        add(bookmaker, new GridBagConstraints(1, 0, 2, 1, 0d, 0d,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 5, 0), 0, 5));
        usernameLabel = new JLabel("Username");
        usernameTextField = new JTextField(20);
        add(usernameLabel, new GridBagConstraints(0, 1, 1, 1, 0d, 0d,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 5, 5), 0, 0));
        add(usernameTextField, new GridBagConstraints(1, 1, 2, 1, 1d, 0d,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 5, 0), 0, 5));
        passwordLabel = new JLabel("Password");
        passwordTextField = new JTextField(20);
        add(passwordLabel, new GridBagConstraints(0, 2, 1, 1, 0d, 0d,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 5, 5), 0, 0));
        add(passwordTextField, new GridBagConstraints(1, 2, 2, 1, 1d, 0d,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 5, 0), 0, 5));
        dateLabels = new JLabel[BookmakerAccount.DateType.values().length];
        dateSpinnerDateModel =
                new SpinnerDateModel[BookmakerAccount.DateType.values().length];
        dateSpinner = new JSpinner[BookmakerAccount.DateType.values().length];
        dateCheckBox = new JCheckBox[BookmakerAccount.DateType.values().length];
        balanceLabel = new JLabel("Balance");
        balanceTextField = new JFormattedTextField(
                NumberFormat.getCurrencyInstance());
        for (final BookmakerAccount.DateType dateType :
                BookmakerAccount.DateType.values()) {
            dateLabels[dateType.index] = new JLabel(dateType.name);
            dateCheckBox[dateType.index] = new JCheckBox();
            dateSpinnerDateModel[dateType.index] = new SpinnerDateModel(today,
                    startDate, endDate, Calendar.DATE);
            dateSpinner[dateType.index] = new JSpinner(
                    dateSpinnerDateModel[dateType.index]);
            add(dateLabels[dateType.index], new GridBagConstraints(
                    0, 3 + dateType.index, 1, 1, 0d, 0d,
                    GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 5, 5), 0, 5));
            add(dateCheckBox[dateType.index], new GridBagConstraints(
                    1, 3 + dateType.index, 1, 1, 0d, 0d,
                    GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 5, 0), 0, 0));
            add(dateSpinner[dateType.index], new GridBagConstraints(
                    2, 3 + dateType.index, 1, 1, 0d, 0d,
                    GridBagConstraints.EAST, GridBagConstraints.NONE,
                    new Insets(0, 0, 5, 0), 0, 5));
            dateSpinner[dateType.index].setEditor(new JSpinner.DateEditor(
                    dateSpinner[dateType.index], "dd MMMM yyyy "));
            dateCheckBox[dateType.index].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dateSpinner[dateType.index].setEnabled(
                            dateCheckBox[dateType.index].isSelected());
                }
            });
        }
        add(balanceLabel, new GridBagConstraints(
                0, 3 + BookmakerAccount.DateType.values().length, 1, 1, 0d, 0d,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 5), 0, 0));
        add(balanceTextField, new GridBagConstraints(
                1, 3 + BookmakerAccount.DateType.values().length, 2, 1, 0d, 0d,
                GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 5));
        validate();
        usernameTextField.setPreferredSize(usernameTextField.getPreferredSize());
        passwordTextField.setPreferredSize(passwordTextField.getPreferredSize());
        balanceTextField.setHorizontalAlignment(JTextField.RIGHT);
    }

    public void set(BookmakerAccount account) {
        this.account = account;
            LOG.debug(usernameTextField.getPreferredSize());
            LOG.debug(usernameTextField.getSize());
        if (account == null) {
            bookmaker.setText("");
            usernameTextField.setText("");
            passwordTextField.setText("");
            bookmakerAccountBalance = 0;
        }
        else {
            bookmaker.setText(account.getBookmaker().getName());
            usernameTextField.setText(account.getUsername());
            passwordTextField.setText(account.getPassword());
            BookmakerAccountBalance balance = null;
            try {
                 balance = BookmakerAccountBalanceFactory.getFactory(
                    ).getMostRecentFromAccount(account);
            }
            catch (SQLException sqle) {
                DatabaseConduit.printSQLException(sqle);
            }
            if (balance == null)
                bookmakerAccountBalance = 0;
            else
                bookmakerAccountBalance = balance.getBalance();
        }
        for (BookmakerAccount.DateType dateType :
                BookmakerAccount.DateType.values())
            setDate(dateType);
        balanceTextField.setValue(bookmakerAccountBalance * .01);
    }

    private void setDate(BookmakerAccount.DateType dateType) {
        Date date = account == null ? null : account.getDate(dateType);
        if (date == null) {
            dateCheckBox[dateType.index].setSelected(false);
            dateSpinner[dateType.index].setEnabled(false);
            dateSpinnerDateModel[dateType.index].setValue(new Date());
        }
        else {
            dateCheckBox[dateType.index].setSelected(true);
            dateSpinner[dateType.index].setEnabled(true);
            dateSpinnerDateModel[dateType.index].setValue(date);
        }
    }

    public void setEditable(boolean editable) {
        usernameTextField.setEnabled(editable);
        passwordTextField.setEnabled(editable);
        balanceTextField.setEnabled(editable);
        for (BookmakerAccount.DateType dateType :
                BookmakerAccount.DateType.values()) {
            dateCheckBox[dateType.index].setEnabled(editable);
            dateSpinner[dateType.index].setEnabled(editable ?
                dateCheckBox[dateType.index].isSelected() : false);
        }
    }

    public void save() {
        if (account != null) {
            account.setUsername(usernameTextField.getText());
            account.setPassword(passwordTextField.getText());
            for (BookmakerAccount.DateType dateType :
                    BookmakerAccount.DateType.values()) {
                if (dateCheckBox[dateType.index].isSelected()) {
                    account.setDate(dateType,
                            dateSpinnerDateModel[dateType.index].getDate());
                }
                else {
                    account.setDate(dateType, null);
                }
            }
            LOG.debug("balance: " + balanceTextField.getValue());
            LOG.debug("balance.getClass(): " +
                    balanceTextField.getValue().getClass());
            int balance = (int)((((Number)balanceTextField.getValue()
                    ).doubleValue() + .005) * 100);
            LOG.debug(account);
            try {
                if (account.hasChangedSinceSnapshot()) {
                    BookmakerAccountFactory.getFactory().save(account);
                    account.snapshot();
                }
                if (balance != bookmakerAccountBalance)
                    BookmakerAccountBalanceFactory.getFactory().save(
                            new BookmakerAccountBalance(account, balance,
                            new Date(System.currentTimeMillis())), false);
            }
            catch (SQLException sqle) {
                DatabaseConduit.printSQLException(sqle);
            }
            setEditable(false);
//            repaint();
        }
    }

    public void edit() {
        setEditable(true);
    }

}
