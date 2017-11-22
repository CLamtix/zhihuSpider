package com.spider.thief;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

public class ResultParser {
	
	@Test
	public void steal() throws Exception{
		
		Grabber grabber = new Grabber();
//		String html = grabber.startGet("https://www.zhihu.com/topic");
//		String html = grabber.startGet("https://www.zhihu.com/topic/19550994");
		String html = grabber.startGet("https://www.zhihu.com/question/63828594");
		Document doc = null;
		if(html != null && !"".equals(html)){
			doc = Jsoup.parse(html);
		}
		System.out.println(doc.outerHtml());
	}
}
