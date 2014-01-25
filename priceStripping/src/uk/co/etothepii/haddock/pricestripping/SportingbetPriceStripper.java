/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.pricestripping;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import javax.jws.WebParam;
import org.apache.log4j.Logger;
import org.htmlcleaner.TagNode;
import uk.co.etothepii.util.screenscraping.WebPage;
import uk.co.etothepii.util.screenscraping.WebPageDownloader;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class SportingbetPriceStripper extends PricesStripper {

    private static final Logger LOG =
            Logger.getLogger(SportingbetPriceStripper.class);

    private static final String BASE_URL = "http://www.sportingbet.com";
    private static final URL HOME_PAGE_URL;
    
    static {
        URL tempUrl;
        try {
            tempUrl = new URL(BASE_URL);
        }
        catch (MalformedURLException mue) {
            tempUrl = null;
        }
        HOME_PAGE_URL = tempUrl;
    }

    private WebPageDownloader webPageDownloader;
    private Map<String, URL> links;

    public SportingbetPriceStripper() {
        webPageDownloader = new WebPageDownloader();
        WebPage homePage = webPageDownloader.download(HOME_PAGE_URL);
        TagNode homePageNode = getRootNode(homePage);
        links = getLinks(homePageNode, BASE_URL);
        for (String key : links.keySet()) {
            LOG.debug("\"" + key + "\" ---> " + links.get(key).toString());
        }
        URL football = links.get("Football");
        WebPage footballHomePage = webPageDownloader.download(football);
        TagNode footballNode = getRootNode(homePage);
        LOG.debug(getSourceCode(footballNode, 0));
    }







}
