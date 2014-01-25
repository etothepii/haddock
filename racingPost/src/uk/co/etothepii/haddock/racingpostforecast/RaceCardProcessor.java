/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.racingpostforecast;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import org.htmlcleaner.TagNode;
import uk.co.etothepii.db.DatabaseConduit;
import uk.co.etothepii.haddock.db.MasterController;
import uk.co.etothepii.haddock.db.factories.AliasFactory;
import uk.co.etothepii.haddock.db.tables.Alias;
import uk.co.etothepii.haddock.db.tables.Race;
import uk.co.etothepii.haddock.db.tables.enumerations.Vendor;
import uk.co.etothepii.util.screenscraping.HtmlEntities;
import uk.co.etothepii.util.screenscraping.TextHtmlProcessor;
import uk.co.etothepii.util.screenscraping.WebPage;

/**
 *
 *
 * @author jrrpl
 */
public class RaceCardProcessor extends TextHtmlProcessor {

    private static final Logger LOG = Logger.getLogger(RaceCardProcessor.class);
    private static final Map<String, String> BETTING_FORECAST_ATTRIBS =
            new TreeMap<String, String>();
    private static final TreeMap <String, String> EMPTY_ATTRIBS =
            new TreeMap<String, String>();
    private NumberFormat nf = NumberFormat.getInstance();

    static {
        BETTING_FORECAST_ATTRIBS.put("class", "info");
    }

    public RaceCardProcessor() {
        nf.setMaximumFractionDigits(4);
    }

    public void process(Race race, Date date, WebPage webPage) {
        LOG.debug(date);
        LOG.debug(race);
        DatabaseConduit conduit = MasterController.takeConduit();
        PreparedStatement ps = null;
        try {
            ps = conduit.prepareStatement(
                    "SELECT * FROM `racingPostForecast` WHERE `race` = ?");
            ps.setLong(1, race.getId());
            if (ps.executeQuery().next()) return;
            TagNode root = getRootNode(webPage);
            TagNode bettingForecast =
                    getFirstTagContains(root, "div", BETTING_FORECAST_ATTRIBS);
            LOG.debug(getSourceCode(bettingForecast, 0));
            String foreCastStr = getContents(bettingForecast).split("&nbsp;")[1].trim();
            foreCastStr = foreCastStr.substring(0, foreCastStr.length() - 1);
            LOG.debug('"' + foreCastStr + '"');
            for (String s : foreCastStr.split(", ")) {
                s = HtmlEntities.decode(s);
                LOG.debug("Processing: " + s);
                String[] parts = s.split(" ", 2);
                String name = parts[1].trim();
                parts = parts[0].split("/");
                double price;
                if (parts.length == 1 && (parts[0].equalsIgnoreCase("EVS") ||
                        parts[0].equalsIgnoreCase("EVENS"))) {
                    price = 2;
                }
                else if (parts.length == 2) {
                    double n = Integer.parseInt(parts[0]);
                    double d = Integer.parseInt(parts[1]);
                    price = 1d + n / d;
                }
                else {
                    LOG.error("FAILED TO PARSE " + s);
                    continue;
                }
                Alias alias = AliasFactory.getFactory(
                        ).getFromAliasAndVendor(name, Vendor.RP);
                if (alias == null) {
                    String bfName = getBestBetfairGuess(name);
                    Alias bfAlias = AliasFactory.getFactory().getFromAliasAndVendor(
                            bfName, Vendor.BETFAIR_NAME);
                    alias = new Alias(bfAlias == null ? null :
                        bfAlias.getSelection(), name, Vendor.RP);
                    try {
                        AliasFactory.getFactory().save(alias);
                    }
                    catch (SQLException sqle) {
                        LOG.error(sqle.getMessage(), sqle);
                    }
                }
                if (alias.getSelection() == null) {
                    System.err.println("Unable to match " + alias.getId() + " (" +
                            alias.getAlias() + ") to a selction");
                }
                try {
                    ps = conduit.prepareStatement("INSERT INTO "
                            + "`racingPostForecast` (`id`, `alias`, `race`, `price`) "
                            + "VALUES (NULL, ?, ?, ?)");
                    ps.setLong(1, alias.getId());
                    ps.setLong(2, race.getId());
                    ps.setDouble(3, price);
                    ps.execute();
                }
                catch (SQLException sqle) {
                    LOG.error(sqle.getMessage(), sqle);
                }
                LOG.debug(race.getId() + ", \"" + name + "\", " + nf.format(price));
            }
        }
        catch (SQLException sqle) {
            LOG.error(sqle.getMessage(), sqle);
        }
        finally {
            if (conduit != null)
                MasterController.releaseConduit(conduit);
            try {
                if (ps != null)
                    ps.close();
            }
            catch (SQLException sqle) {}
        }
    }

    public static String getBestBetfairGuess(String name) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (i < name.length() - 1 && name.charAt(i + 1) == '(')
                return sb.toString();
            else if (Character.isLetter(c) || c == ' ')
                sb.append(c);
        }
        return sb.toString();
    }
}
