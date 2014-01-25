/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.etothepii.haddock.timeform;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jrrpl
 */
public class RaceCardProcessorTest {
	
	public RaceCardProcessorTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	/**
	 * Test of processTimeformPage method, of class RaceCardProcessor.
	 */
	@Test
	public void testProcessTimeformPage() {
		System.out.println("processTimeformPage");
		URL webPageURL;
		try {
			webPageURL = new URL("http://form.horseracing.betfair.com/daypage?date=20120907");
		}
		catch (MalformedURLException mue) {
			throw new RuntimeException(mue);
		}
		TagNode root;
		try {
			InputStream is = getClass().getResourceAsStream("exampleDayPage.html"); 
			root = new HtmlCleaner().clean(is);
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
		RaceCardProcessor instance = new RaceCardProcessor();
		instance.getRootNode(null);
		ArrayList expResult = null;
		ArrayList result = instance.processTimeformPage(webPageURL, root);
		assertEquals(expResult, result);
	}
}
