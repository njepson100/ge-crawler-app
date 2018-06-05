package com.ge.crawlerapp;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

/*This class is the entry point of the functionality for the crawler.  This class will spawn off threads by calling the crawler thread.
 *A new thread will be created as needed for each call to the crawler thread as a new address is added to the linksQueue.
 *This is implemented as a multi-threaded web-crawler, however there is very inconsistent behaviour as the websites returned are very inconsistent.
 *There needs to either be some additional locking or some other concurrency in place to ensure that all addresses are being visited and correctly crawled.
 */

public class Crawler {
	ObjectMapper mapper = new ObjectMapper();
	public Map<String, List<String>> resultantWebPages = new ConcurrentHashMap<String, List<String>>();
	
	public Set<String> pagesVisited = new HashSet<String>();
	private String internetFileName;
	
	//This is the object that represents the json string which is the list of websites
	//since this is faking the internet it is public now in order to be accessible by other classes
	public DocumentContext theInternet;
	
    private ExecutorService crawlService;
    protected final LinkedBlockingQueue<String> linksToCrawlQueue = new LinkedBlockingQueue<>();
    protected CyclicBarrier barrier = new CyclicBarrier(2);
	
	public Crawler(String fileName) {
		internetFileName = fileName;
		for (pageStatus status : pageStatus.values())
		{
			resultantWebPages.put(status.toString(), Collections.synchronizedList(new ArrayList<String>()));
		}
	}
	
    public void addUrlToQueue(String url) throws InterruptedException {
        linksToCrawlQueue.put(url);
    }
    
    public void addVisitedPages(String url) {
    	
    }
	
    //this list of enums enforces a set of statuses for pages, they can be successfully visited, in error since they don't exist
    //or skipped since they have already been visited.
	public enum pageStatus {
		success ("Success"),
		skipped ("Skipped"),
		error ("Error");
	    private final String label;       

	    private pageStatus(String s) {
	    	label = s;
	    }
	    public String toString() {
	        return this.label;
	     }
	}
	
	public synchronized Map<String, List<String>> getCrawlResults (String startURL) throws IOException, URISyntaxException, InterruptedException
	{
        //I created the internet!  Well just initializing the fake list of web pages with the json file.
		String theInternetString = new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(internetFileName).toURI())), StandardCharsets.UTF_8);
        theInternet = JsonPath.parse(theInternetString);
		
        //create thread pool
        crawlService = Executors.newCachedThreadPool();
        
        addUrlToQueue(startURL);
        
        while(!linksToCrawlQueue.isEmpty()) {
            String nextUrl = null;
            try {
                nextUrl = linksToCrawlQueue.take();
            } catch (InterruptedException ex) {
                Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if (!shouldCrawl(nextUrl)) 
            {
            	continue; // skip this URL.
            }
            
            //Adding to the pagesVisited so we don't visit the same page twice
            this.pagesVisited.add(nextUrl);
            
            try
            {
                CrawlerThread crawlerThread = new CrawlerThread(nextUrl, this);
                crawlService.submit(crawlerThread);
                
                //When there are no more links in queue, wait for other threads to complete.  The linksQueue may still have more links added while threads run
                if(linksToCrawlQueue.isEmpty()){
                      barrier.await();
                }
            
        } catch (InterruptedException | BrokenBarrierException e) {
            System.out.println("Error crawling URL: " + nextUrl);
        }
            
        }
        crawlService.shutdown();
		
        try {
            //wait until all threads have ended
            crawlService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
		return resultantWebPages;
	}
	
	private boolean shouldCrawl(String urlToCrawl)
	{
        //If the page has been skipped or is a doesn't exist then there is no need to perform any actions on it
        if ((resultantWebPages.get(pageStatus.error.toString()).contains(urlToCrawl) || resultantWebPages.get(pageStatus.skipped.toString()).contains(urlToCrawl)))
        {
        	return false;
        }

        if(pagesVisited.contains(urlToCrawl)) {
            resultantWebPages.get(pageStatus.skipped.toString()).add(urlToCrawl);
            return false;
        }
        return true;

	}
	
}
