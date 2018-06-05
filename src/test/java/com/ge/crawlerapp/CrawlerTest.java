package com.ge.crawlerapp;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;


import com.ge.crawlerapp.Crawler.pageStatus;

/*This test app doesn't need any mocks at this time since the internet is faked already with a json file.  If this app
 * were actually hitting the internet there would need to be mocks in order to fake found and crawled addresses
 * 
 */

public class CrawlerTest {

	private Crawler webCrawler;
	private List<pageStatus> statusList = new ArrayList<>();
	@Before
	public void setUp()
	{
		
	}
	
	@Test
	public void testWebCrawler() throws IOException, URISyntaxException, InterruptedException
	{
		String startAddress = "http://foo.bar.com/p1";
		String internetFileName = "Internet.json";
		
		webCrawler = new Crawler(internetFileName);
		Map<String, List<String>> results = webCrawler.getCrawlResults(startAddress);
		assertEquals(5, results.get(pageStatus.success.toString()).size());
		assertEquals(2, results.get(pageStatus.error.toString()).size());
		assertEquals(4, results.get(pageStatus.skipped.toString()).size());
	}
	
	
	
}
