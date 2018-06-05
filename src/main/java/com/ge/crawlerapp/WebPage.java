package com.ge.crawlerapp;

import java.util.ArrayList;
import java.util.List;

public class WebPage {
	private String address;
	private List<String> links = new ArrayList<String>();
	
	public String getAddress() {
		return address;
	}
	
	public List<String> getLinkList() 
	{
		return links;
	}
	
}
