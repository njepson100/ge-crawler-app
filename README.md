# ge-crawler-app
Multi-threaded web crawler
How to run

clone  https://github.com/njepson100/ge-crawler-app

This is currently giving me problems when run in command line.  
If you open in eclipse you can select run as -> run configuration and select the crawler project and the com.ge.crawlerapp.Main as the main class.  
Select run and then open your browser and go to http://localhost:8080/gecrawlerapp/spider/Internet.json
or http://localhost:8080/gecrawlerapp/spider/Internet2.json
the 2 json files are the two optional internets.

Here are some design notes
There are currently issues with either the synchronization or blocking and the results of the web crawl are inconsistent.
Due to this the test may fail, commenting out the asserts in CrawlwerTest.java will allow for clean install, 
or you can run with DskipTests
