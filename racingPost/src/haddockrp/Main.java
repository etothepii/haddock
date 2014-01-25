/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package haddockrp;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.htmlcleaner.TagNode;
import uk.co.etothepii.haddock.racingpostforecast.MainPageProcessor;
import uk.co.etothepii.haddock.racingpostforecast.RaceCardProcessor;
import uk.co.etothepii.util.screenscraping.TextHtmlProcessor;
import uk.co.etothepii.util.screenscraping.WebPage;
import uk.co.etothepii.util.screenscraping.WebPageDownloader;

/**
 *
 * @author jrrpl
 */
public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        if (args.length > 0 && args[0].equals("LOGIN")) {
            WebClient wc = new WebClient(BrowserVersion.FIREFOX_3_6);
            wc.setThrowExceptionOnScriptError(false);
            try {
                URL url = new URL(
                        "http://www.racingpost.com/horses2/cards/home.sd");
                HtmlPage hp = wc.getPage(url);
                HtmlAnchor a = hp.getAnchorByText("Racing Post Log in or Register");
                LOG.debug(a.click());
//                HtmlAnchor ha = hp.getAnchorByText("Racing Post Log in or Register");
//                HtmlPage loginPage = ha.click();
//                LOG.debug(loginPage.toString());
//                for (HtmlForm f : loginPage.getForms()) {
//                    LOG.debug(f.getNameAttribute());
//                }
            }
            catch (Exception ioe) {
                LOG.error(ioe.getMessage(), ioe);
                LOG.error("There was an error!");
            }
        }
        else {
            try {
                URL startPage = new URL(
                        "http://www.racingpost.com/horses2/cards/home.sd");
                WebPageDownloader wpd = new WebPageDownloader();
                WebPage wp = wpd.download(startPage);
                LOG.debug("Downloaded");
                MainPageProcessor mpp = new MainPageProcessor();
                LOG.debug("Creater Race Card Processor");
                ArrayList<MainPageProcessor.Result> mainPageResults =
                        mpp.process(wp);
                RaceCardProcessor rcp = new RaceCardProcessor();
                for (int i = 0; i < mainPageResults.size(); i++) {
                    MainPageProcessor.Result mpr = mainPageResults.get(i);
                    WebPage page = wpd.download(mpr.url);
                    rcp.process(mpr.race, mpr.date, page);
                }
                System.exit(0);
            }
            catch (MalformedURLException mue) {}
        }
    }

}
