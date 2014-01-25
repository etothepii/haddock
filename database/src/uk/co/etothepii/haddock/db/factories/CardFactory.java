/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.db.factories;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.tables.BankAccount;
import uk.co.etothepii.haddock.db.tables.Card;
import uk.co.etothepii.haddock.db.tables.Identity;
import uk.co.etothepii.haddock.db.tables.enumerations.CardMonth;
import uk.co.etothepii.haddock.db.tables.enumerations.YN;

/**
 *
 * @author jrrpl
 */
public class CardFactory extends HaddockFactory<Card> {

    @Override
    protected String getTablename() {
        return "cards";
    }

    private static final Logger LOG = Logger.getLogger(CardFactory.class);
    
    private static CardFactory factory;
    
    public static CardFactory getFactgory() {
        if (factory == null)
            factory = new CardFactory();
        return factory;
    }
    
    private CardFactory() {}

    @Override
    protected Card build(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            int identityId = rs.getInt("identity");
            int bankAccountId = rs.getInt("bankAccount");
            String type = rs.getString("type");
            String number = rs.getString("number");
            String fromMonthStr = rs.getString("fromMonth");
            String fromYear = rs.getString("fromYear");
            String endMonthStr = rs.getString("endMonth");
            String endYear = rs.getString("endYear");
            String issueNumber = rs.getString("issueNumber");
            String name = rs.getString("name");
            String cvv = rs.getString("cvv");
            String password = rs.getString("password");
            String activeStr = rs.getString("active");
            Blob frontBlob = rs.getBlob("front");
            Blob backBlob = rs.getBlob("back");
            Identity identity =
                    IdentityFactory.getFactory().getFromId(identityId);
            BankAccount bankAccount =
                    BankAccountFactory.getFactory().getFromId(bankAccountId);
            CardMonth fromMonth = CardMonth.getCardMonth(fromMonthStr);
            CardMonth endMonth = CardMonth.getCardMonth(endMonthStr);
            YN active = YN.getYN(activeStr);
            BufferedImage front = null;
            BufferedImage back = null;
            if (frontBlob != null) {
                try {
                    front = ImageIO.read(frontBlob.getBinaryStream());
                }
                catch (IOException ioe) {}
            }
            if (backBlob != null) {
                try {
                    back = ImageIO.read(backBlob.getBinaryStream());
                }
                catch (IOException ioe) {}
            }
            return new Card(id, identity, bankAccount, type, number,
                    fromMonth, fromYear, endMonth, endYear, issueNumber, name,
                    cvv, password, active, front, back);
        }
        catch (SQLException sqle) {
			throw new RuntimeException(sqle);
        }
    }

    @Override
    protected String getInsertQueryStr() {
        return "INSERT INTO `" + getDatabasename() + "`.`" + getTablename()
                + "` (`id`, `identity`, "
                + "`bankAccount`, `type`, `number`, `fromMonth`, `fromYear`, "
                + "`endMonth`, `endYear`, `issueNumber`, `name`, `cvv`, "
                + "`password`, `active`, `front`, `back`) VALUES (NULL, "
                + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    }

    @Override
    protected PreparedStatement prepareInsertStatement(Card t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledInsertStatement(conduit);
        toRet.setInt(1, (int)t.getIdentity().getId());
        toRet.setInt(2, (int)t.getBankAccount().getId());
        toRet.setString(3, t.getType());
        toRet.setString(4, t.getNumber());
        toRet.setString(5, t.getFromMonth().numeric);
        toRet.setString(6, t.getFromYear());
        toRet.setString(7, t.getEndMonth().numeric);
        toRet.setString(8, t.getEndYear());
        toRet.setString(9, t.getIssueNumber());
        toRet.setString(10, t.getName());
        toRet.setString(11, t.getCvv());
        toRet.setString(12, t.getPassword());
        toRet.setString(13, t.getActive().shortText);
        if (t.getFront() == null) {
            toRet.setNull(14, Types.BLOB);
        }
        else {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(t.getFront(), "jpeg", os);
                ByteArrayInputStream is = new ByteArrayInputStream(
                        os.toByteArray());
                toRet.setBlob(14, is);
            }
            catch (IOException ioe) {
                LOG.error(ioe.getMessage(), ioe);
            }
        }
        if (t.getBack() == null) {
            toRet.setNull(15, Types.BLOB);
        }
        else {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(t.getBack(), "jpeg", os);
                ByteArrayInputStream is = new ByteArrayInputStream(
                        os.toByteArray());
                toRet.setBlob(15, is);
            }
            catch (IOException ioe) {
                LOG.error(ioe.getMessage(), ioe);
            }
        }
        return toRet;
    }

    @Override
    protected String getUpdateQueryStr() {
        return "UPDATE `" + getDatabasename() + "`.`" + getTablename()
                + "` SET `identity` = ?, "
                + "`bankAccount` = ?, `type` = ?, `number` = ? `fromMonth` = ?, "
                + "`fromYear` = ?, `endMonth` = ?, `endYear` = ?, "
                + "`issueNumber` = ?, `name` = ?, `cvv` = ?, `password` = ?, "
                + "`active` = ?, `front` = ?, `back` = ? WHERE `id` = ? "
                + "LIMIT 1;";
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(Card t, 
            DatabaseConduit conduit) throws SQLException {
        PreparedStatement toRet = getUnfilledUpdateStatement(conduit);
        toRet.setInt(1, (int)t.getIdentity().getId());
        toRet.setInt(2, (int)t.getBankAccount().getId());
        toRet.setString(3, t.getType());
        toRet.setString(4, t.getNumber());
        toRet.setString(5, t.getFromMonth().numeric);
        toRet.setString(6, t.getFromYear());
        toRet.setString(7, t.getEndMonth().numeric);
        toRet.setString(8, t.getEndYear());
        toRet.setString(9, t.getIssueNumber());
        toRet.setString(10, t.getName());
        toRet.setString(11, t.getCvv());
        toRet.setString(12, t.getPassword());
        toRet.setString(13, t.getActive().shortText);
        if (t.getFront() == null) {
            toRet.setNull(14, Types.BLOB);
        }
        else {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(t.getFront(), "jpeg", os);
                ByteArrayInputStream is = new ByteArrayInputStream(
                        os.toByteArray());
                toRet.setBlob(14, is);
            }
            catch (IOException ioe) {
                LOG.error(ioe.getMessage(), ioe);
            }
        }
        if (t.getBack() == null) {
            toRet.setNull(15, Types.BLOB);
        }
        else {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(t.getBack(), "jpeg", os);
                ByteArrayInputStream is = new ByteArrayInputStream(
                        os.toByteArray());
                toRet.setBlob(15, is);
            }
            catch (IOException ioe) {
                LOG.error(ioe.getMessage(), ioe);
            }
        }
        toRet.setInt(16, (int)t.getId());
        return toRet;
    }
}
