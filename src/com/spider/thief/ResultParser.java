package com.spider.thief;

import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.junit.Test;

public class ResultParser {
	
	@Test
	public void steal() throws Exception{
		
		Grabber grabber = new Grabber();
//		String html = grabber.startGet("https://www.zhihu.com/topic");
//		String html = grabber.startGet("https://www.zhihu.com/topic/19550994");
		String html = grabber.startGet("https://www.zhihu.com/question/67473950");
		Document doc = null;
		if(html != null && !"".equals(html)){
			doc = Jsoup.parse(html);
		}
		
		//�õ�List-item����,ÿ��һlist-item����һ���ش�
		Elements listItems = doc.getElementsByClass("List-item");
		System.out.println("�ܹ��� ["+listItems.size()+"] ����:");
//		for(Element e : listItems){
//			getData(e);
//		}
		JSONObject result = grabber.getUrlReturnJson("https://www.zhihu.com/api/v4/questions/67473950/answers?include=data[*].is_normal,admin_closed_comment,reward_info,is_collapsed,annotation_action,annotation_detail,collapse_reason,is_sticky,collapsed_by,suggest_edit,comment_count,can_comment,content,editable_content,voteup_count,reshipment_settings,comment_permission,created_time,updated_time,review_info,question,excerpt,relationship.is_authorized,is_author,voting,is_thanked,is_nothelp,upvoted_followees;data[*].mark_infos[*].url;data[*].author.follower_count,badge[?(type=best_answerer)].topics&offset=3&limit=2&sort_by=default");
		JSONObject paging = JSONObject.fromObject(result.get("paging"));
		boolean isEnd = paging.optBoolean("is_end");
		String nextUrl = paging.optString("next");
		System.out.println("isEnd:"+isEnd+" nextUrl:"+nextUrl);
		
		JSONObject result2 = grabber.getUrlReturnJson(nextUrl);
		JSONObject paging2 = JSONObject.fromObject(result.get("paging"));
		boolean isEnd2 = paging.optBoolean("is_end");
		String nextUrl2 = paging.optString("next");
		System.out.println("isEnd2:"+isEnd2+" nextUrl2:"+nextUrl2);
//		result.get("data");
	}
	public void getData(Node listItem){
		//֪��ǰ���ٴη�װ��һ��,���Ե�ϸ�ڶ�������ڵ�����
		Node answerItem = listItem.childNode(0);
		//��answerItem�ڵ����²����ӽڵ� ����ͬ���ڵ������url�Ľڵ�
		String upvoteCount = answerItem.childNode(2).attr("content");//��ͬ��
		String url = answerItem.childNode(3).attr("content");//��������
		System.out.println("url:"+url+"����������:"+upvoteCount);
	}
}
