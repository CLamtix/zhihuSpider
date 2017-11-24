package com.spider.entity;

import java.sql.Date;

public class Answer {
	public int id;
	public String topic;
	public String title;
	public String link;
	public Integer count;
	public Date searchDate;
	public Answer(){
		searchDate = new Date(System.currentTimeMillis());
	}
	
	@Override
	public String toString() {
		return "Answer [topic=" + topic + ", title=" + title + ", link=" + link
				+ ", count=" + count + ", searchDate=" + searchDate + "]\n";
	}
	
}
