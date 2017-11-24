package com.spider.thief;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.spider.entity.Answer;

public class ResultParser {
	
	public static final Logger log = Logger.getLogger(ResultParser.class);
	
	public static final String baseUrl = "https://www.zhihu.com";
	
	public static final String baseQuestionUrl = "https://www.zhihu.com/question/questionNumber";
	
	public static final String baseQuestionJsonUrl = "https://www.zhihu.com/api/v4/questions/questionNumber/answers?include=data[*].is_normal,admin_closed_comment,reward_info,is_collapsed,annotation_action,annotation_detail,collapse_reason,is_sticky,collapsed_by,suggest_edit,comment_count,can_comment,content,editable_content,voteup_count,reshipment_settings,comment_permission,created_time,updated_time,review_info,question,excerpt,relationship.is_authorized,is_author,voting,is_thanked,is_nothelp,upvoted_followees;data[*].mark_infos[*].url;data[*].author.follower_count,badge[?(type=best_answerer)].topics&offset=3&limit=20&sort_by=default";
	
	public static final int standardCount = 5000;
	
	public static final int standardAnswersCount = 500;
	
	public static void main(String[] args) {
		
		try {
			new ResultParser().steal("22467582");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param questionNumber ����ı��
	 * @throws Exception
	 */
	public void steal(String questionNumber) throws Exception{
		ArrayList<Answer> list = new ArrayList<Answer>();
		String questionUrl = baseQuestionUrl.replaceFirst("questionNumber", questionNumber);
		String html = BeanFactory.getParser().startGet(questionUrl);
		Document doc = Jsoup.parse(html);
		//�ش�������,����ĳ���ش�������ѯ����
		int answerCount = Integer.parseInt(doc.getElementsByClass("QuestionPage").first().child(3).attr("content"));
		if(answerCount<standardAnswersCount){
			return;
		}
		//����ı���
		String title = doc.getElementsByClass("QuestionPage").first().child(0).attr("content");
		//���������Ļ���
		String topic = doc.getElementsByClass("QuestionPage").first().child(2).attr("content");
		
		//�õ�List-item����,ÿ��һlist-item����һ���ش�
		Elements listItems = doc.getElementsByClass("List-item");
		log.info("���������ʼҳ��start>>>");
		getInitialData(listItems,title,topic,list);
		log.info("���������ʼҳ��stop<<<");
		
		log.info("�ֶλ�ȡĳ������Ĳ��ִ�json����!start>>>");
		String nextJsonUrl = baseQuestionJsonUrl.replaceFirst("questionNumber", questionNumber);
		getJsonData(nextJsonUrl,questionUrl,topic,title,list);
		log.info("�ֶλ�ȡĳ������Ĳ��ִ�json����!start>>>");
		
		log.info(list);
		
	}
	
	/**
	 * 
	 * @param nextJsonUrl ����ץȡjson����url
	 * @param questionUrl ����ҳ���url
	 * @param list 
	 * @param title 
	 * @param topic 
	 * @throws Exception
	 */
	public void getJsonData(String nextJsonUrl, String questionUrl, String topic, String title, ArrayList<Answer> list) throws Exception{
		boolean isEnd = false;
		Answer answer;
		//����������������
		int inconformity = 0;
		//��ʼѭ����ȡjson����
		while(isEnd == false){
			JSONObject result = BeanFactory.getParser().getUrlReturnJson(nextJsonUrl);
			//����data��������,����������װ��������
			JSONArray answers = JSONArray.fromObject(result.get("data"));
			for(int i = 0;i<answers.size();i++){
				JSONObject simpleAnswer = (JSONObject) answers.get(i);
				int count = simpleAnswer.optInt("voteup_count");
				//��������ĳ��ֵ�ĲŻ��ռ�url
				if(count>standardCount){
					String answerId = simpleAnswer.optString("id");
					String answerUrl = questionUrl+"/answer/"+answerId;
					answer = new Answer();
					answer.topic = topic;
					answer.title = title;
					answer.link = answerUrl;
					answer.count = count;
					list.add(answer);
				}else{
					inconformity++;
				}
			}
			//��������ϵĴ𰸴�������һ��ֵʱ��breakѭ��
			if(inconformity>=50){
				break;
			}
			//����paging������,��Ҫ��nextUrl��isEnd
			JSONObject paging = JSONObject.fromObject(result.get("paging"));
			isEnd = paging.optBoolean("is_end");
			nextJsonUrl = paging.optString("next");
			
			TimeUnit.SECONDS.sleep(2);
		}
	}
	
	
	/**
	 * ������ʼ������ҳ�������,һ��Ϊ����
	 * @param listItems
	 * @param title ������Ŀ
	 * @param list 
	 * @param topic 
	 */
	public void getInitialData(Elements listItems, String title, String topic, ArrayList<Answer> list){
		Answer answer;
		for(Element e : listItems){
			//֪��ҳ���ٴη�װ��һ��,���Ե�ϸ�ڶ�������ڵ�����
			Node answerItem = e.childNode(0);
			//��answerItem�ڵ����²����ӽڵ� ����ͬ���ڵ������url�Ľڵ�
			int count = Integer.parseInt(answerItem.childNode(2).attr("content"));//��ͬ��
			
			if(count>standardCount){
				String url = answerItem.childNode(3).attr("content");//��������
				answer = new Answer();
				answer.topic = topic;
				answer.title = title;
				answer.link = url;
				answer.count = count;
				list.add(answer);
			}
		}
	}
}
