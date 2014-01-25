/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.timeform;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import org.htmlcleaner.TagNode;
import uk.co.etothepii.util.screenscraping.TextHtmlProcessor;
import uk.co.etothepii.util.screenscraping.WebPage;

/**
 *
 * @author jrrpl
 */
public class RaceCardProcessor extends TextHtmlProcessor {

    public static final int HREF = 0;
    public static final int INSIDE = 1;

    private static final Logger LOG = Logger.getLogger(RaceCardProcessor.class);

    private final SimpleDateFormat scheduledOffFormat;

    public RaceCardProcessor() {
        scheduledOffFormat = new SimpleDateFormat("ddMMyyHHmm");
    }

    public ArrayList<TimeformRaceCard> process(WebPage webPage) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("processing");
            Class c = webPage.content.getClass();
            LOG.debug(c);
            while ((c = c.getSuperclass()) != null)
                LOG.debug(c);
        }
        if (webPage.contentType.startsWith("text/html") &&
                webPage.content instanceof InputStream) {
        }
        return null;
    }

	public ArrayList<TimeformRaceCard> processTimeformPage(URL webPageURL, TagNode root) {
		return null;
	}

	private ArrayList<TimeformRaceCard> processOldTimeformPage(URL webPageURL, TagNode root) {
		LOG.debug(getSourceCode(root, 0));
		Map<String, String> locationAttributes =
				new TreeMap<String, String>();
		locationAttributes.put("class", "location");
		ArrayList<TagNode> nodes = getTagsEquals(root, "div", locationAttributes);
		ArrayList<TimeformRaceCard> timeformRaceCards =
				new ArrayList<TimeformRaceCard>(nodes.size());
		for (TagNode tn : nodes) {
				ArrayList<TagNode> anchors =
						getTagsEquals(tn, "a", new TreeMap<String, String>());
			Map<String, String> goingAttributes =
					new TreeMap<String, String>();
			locationAttributes.put("class", "going");
			ArrayList<TagNode> goingTags =
					getTagsEquals(tn, "span", goingAttributes);
			String goingStr = null;
			if (goingTags.size() > 0) {
				TagNode goingTag = goingTags.get(0);
				StringBuilder goingSB = new StringBuilder();
				for (Object o : goingTag.getChildren()) {
					goingSB.append(stripWhitespace(o.toString()));
				}
				goingStr = goingSB.toString();
			}
			ArrayList<TimeformRace> timeformRaces =
					new ArrayList<TimeformRace>();
			for (int i = 1; i < anchors.size(); i++) {
				String href = anchors.get(i).getAttributeByName("href");
				URL url = null;
				try {
					url = new URL(webPageURL.getProtocol(),
						webPageURL.getHost(), href);
				}
				catch (MalformedURLException mue) {
					LOG.error(mue.getMessage(), mue);
				}
				String[] parts = href.split("/");
				Date date = null;
				try {
					date = scheduledOffFormat.parse(parts[2] + parts[4]);
				}
				catch (ParseException pe) {
					LOG.error(pe.getMessage(), pe);
				}
				if (url != null && date != null)
					timeformRaces.add(new TimeformRace(date, url));
			}
			String racecourse = null;
			String href = null;
			if (anchors.size() > 0) {
				racecourse = getContents(anchors.get(0));
				href = anchors.get(0).getAttributeByName("href");
			}
			URL meetingURL = null;
			try {
				meetingURL = new URL(webPageURL.getProtocol(), 
						webPageURL.getHost(), href);
			}
			catch (MalformedURLException mue) {
				LOG.error(mue.getMessage(), mue);
			}
			if (href.length() <= 1) continue;
			String[] parts = href.split("/");
			LOG.debug(href);
			parts = parts[parts.length - 1].split("-");
			String countryCode = parts[parts.length - 2];
			if (meetingURL != null)
				timeformRaceCards.add(new TimeformRaceCard(
						racecourse, goingStr, countryCode,
						meetingURL,
						timeformRaces.toArray(new TimeformRace[0])));
		}
		return timeformRaceCards;
	}

}
