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
	 * @param questionNumber 问题的编号
	 * @throws Exception
	 */
	public void steal(String questionNumber) throws Exception{
		ArrayList<Answer> list = new ArrayList<Answer>();
		String questionUrl = baseQuestionUrl.replaceFirst("questionNumber", questionNumber);
		String html = BeanFactory.getParser().startGet(questionUrl);
		Document doc = Jsoup.parse(html);
		//回答数限制,超过某个回答数才轮询分析
		int answerCount = Integer.parseInt(doc.getElementsByClass("QuestionPage").first().child(3).attr("content"));
		if(answerCount<standardAnswersCount){
			return;
		}
		//问题的标题
		String title = doc.getElementsByClass("QuestionPage").first().child(0).attr("content");
		//问题所属的话题
		String topic = doc.getElementsByClass("QuestionPage").first().child(2).attr("content");
		
		//得到List-item集合,每隔一list-item都是一个回答
		Elements listItems = doc.getElementsByClass("List-item");
		log.info("分析问题初始页面start>>>");
		getInitialData(listItems,title,topic,list);
		log.info("分析问题初始页面stop<<<");
		
		log.info("分段获取某个问题的部分答案json数据!start>>>");
		String nextJsonUrl = baseQuestionJsonUrl.replaceFirst("questionNumber", questionNumber);
		getJsonData(nextJsonUrl,questionUrl,topic,title,list);
		log.info("分段获取某个问题的部分答案json数据!start>>>");
		
		log.info(list);
		
	}
	
	/**
	 * 
	 * @param nextJsonUrl 用来抓取json数据url
	 * @param questionUrl 问题页面的url
	 * @param list 
	 * @param title 
	 * @param topic 
	 * @throws Exception
	 */
	public void getJsonData(String nextJsonUrl, String questionUrl, String topic, String title, ArrayList<Answer> list) throws Exception{
		boolean isEnd = false;
		Answer answer;
		//不符合条件的条数
		int inconformity = 0;
		//开始循环获取json数据
		while(isEnd == false){
			JSONObject result = BeanFactory.getParser().getUrlReturnJson(nextJsonUrl);
			//解析data数组数据,符合条件的装进容器中
			JSONArray answers = JSONArray.fromObject(result.get("data"));
			for(int i = 0;i<answers.size();i++){
				JSONObject simpleAnswer = (JSONObject) answers.get(i);
				int count = simpleAnswer.optInt("voteup_count");
				//赞数超过某个值的才会收集url
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
			//如果不符合的答案次数超过一定值时则break循环
			if(inconformity>=50){
				break;
			}
			//解析paging的数据,主要是nextUrl和isEnd
			JSONObject paging = JSONObject.fromObject(result.get("paging"));
			isEnd = paging.optBoolean("is_end");
			nextJsonUrl = paging.optString("next");
			
			TimeUnit.SECONDS.sleep(2);
		}
	}
	
	
	/**
	 * 遍历初始的请求页面的数据,一般为两条
	 * @param listItems
	 * @param title 问题题目
	 * @param list 
	 * @param topic 
	 */
	public void getInitialData(Elements listItems, String title, String topic, ArrayList<Answer> list){
		Answer answer;
		for(Element e : listItems){
			//知乎页面再次封装了一层,所以的细节都在这个节点里面
			Node answerItem = e.childNode(0);
			//从answerItem节点向下查找子节点 即赞同数节点和文章url的节点
			int count = Integer.parseInt(answerItem.childNode(2).attr("content"));//赞同数
			
			if(count>standardCount){
				String url = answerItem.childNode(3).attr("content");//文章链接
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
