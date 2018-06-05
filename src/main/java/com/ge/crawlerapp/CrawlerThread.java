package com.ge.crawlerapp;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ge.crawlerapp.Crawler.pageStatus;

public class CrawlerThread implements Runnable {

    protected Crawler crawler = null;
    protected String urlToCrawl = null;
    
    public CrawlerThread(String urlToCrawl, Crawler crawler) {
        this.urlToCrawl = urlToCrawl;
        this.crawler = crawler;
    }
    @Override
    public void run(){
        try{
            crawl();
        }catch(Exception ex){
            
        }
    }
    private void crawl() {

        try {
                List<Map<String, Object>> dataList = crawler.theInternet.read("$[?(@.address == '"+ urlToCrawl +"')]");
                
                if (dataList.isEmpty())
                {
                	//url is not found so add to the list of error pages
                	crawler.resultantWebPages.get(pageStatus.error.toString()).add(urlToCrawl);
                }
 
                else
                {
                	//Web page is found so add to successful list and put all links found into the linksQueue
                	crawler.resultantWebPages.get(pageStatus.success.toString()).add(urlToCrawl);
                	crawler.addVisitedPages((String) dataList.get(0).get("address"));
					List<String> linkList = (List<String>) dataList.get(0).get("links");
                	for(String link : linkList)
                	{
                		crawler.addUrlToQueue(link);
                	}
                }	
                if(crawler.barrier.getNumberWaiting()==1){
                    crawler.barrier.await();
                    
                }

            } catch (InterruptedException ex) {
                Logger.getLogger(CrawlerThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BrokenBarrierException ex) {
                Logger.getLogger(CrawlerThread.class.getName()).log(Level.SEVERE, null, ex);
            } 

    }

}
