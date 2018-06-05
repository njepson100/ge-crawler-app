package com.ge.crawlerapp;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.xml.sax.SAXException;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("spider")
public class Spider {

	
	protected static final String UTF8_CHARSET = "charset=UTF-8";
	
	
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonGenerationException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     * @throws XPathExpressionException 
     * @throws NumberFormatException 
     * @throws URISyntaxException 
     * @throws InterruptedException 
     */
    @GET
    @Path("/{InternetVersion}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getCrawlResults(@PathParam("InternetVersion") String InternetType) throws JsonGenerationException, JsonMappingException, IOException, XPathExpressionException, ParserConfigurationException, SAXException, URISyntaxException, InterruptedException {
    	ResponseBuilder response = Response.ok();
    	ObjectMapper mapper = new ObjectMapper();
    	String startAddress = "http://foo.bar.com/p1";

    	Crawler crawler = new Crawler(InternetType);
    	
    	response.entity(mapper.writeValueAsString(crawler.getCrawlResults(startAddress))).status(Status.OK);

    	return response.build();
    }
}