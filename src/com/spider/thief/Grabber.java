package com.spider.thief;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class Grabber {
	
	
	/**
	 * 通过get方式请求服务器
	 * @throws Exception
	 */
	public String startGet(String url) throws Exception {
		CloseableHttpClient httpclient = BeanFactory.createSSLInsecureClient();
		String result = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response = httpclient.execute(httpGet);
			
			try {
				if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
					result = EntityUtils.toString(response.getEntity(),"UTF-8");
				}
				return result;
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
	}
	/**
	 * 发起浏览器异步请求,返回json格式的数据,包含的是某一问题下的多条回答
	 * @param url 
	 */
	public JSONObject getUrlReturnJson(String url) throws Exception {
		CloseableHttpClient httpclient = BeanFactory.createSSLInsecureClient();
		JSONObject result = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("Accept", "application/json, text/plain, */*");
			httpGet.setHeader("Accept-Encoding", "gzip, deflate, br");
			httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			httpGet.setHeader("authorization", "oauth c3cef7c66a1843f8b3a9e6a1e3160e20");
			httpGet.setHeader("Cache-Control", "max-age=0");
			httpGet.setHeader("Connection", "keep-alive");
			httpGet.setHeader("Host", "www.zhihu.com");
			httpGet.setHeader("origin", "https://www.zhihu.com");
			httpGet.setHeader("Referer","https://www.zhihu.com/question/67473950");
			httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");
			httpGet.setHeader("x-udid", "AEDCjr5RZgyPTmuvD_z1FvOGdAxqqKf6n7A=");
			CloseableHttpResponse response = httpclient.execute(httpGet);

			try {
				if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
					result = JSONObject.fromObject(EntityUtils.toString(response.getEntity()));
				}else{
					System.out.println("请求未成功,状态码:"+response.getStatusLine().getStatusCode());
				}
				return result;
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
	}
	
	
	public void startPost(Map<String,String> reqMap) throws Exception {
		CloseableHttpClient httpclient = BeanFactory.createSSLInsecureClient();
		
		try {
			HttpPost httpPost = new HttpPost("https://www.zhihu.com/topics");
			List<NameValuePair> params = null;
			if(reqMap!=null && reqMap.size()>0){
				Iterator<Entry<String, String>> it = reqMap.entrySet().iterator();
				params = new ArrayList<NameValuePair>();
				while(it.hasNext()){
					Entry<String, String> entry = it.next();
					params.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
				}
			}
			if(params!=null){
				httpPost.setEntity(new UrlEncodedFormEntity(params));
			}
			
			CloseableHttpResponse response = httpclient.execute(httpPost);

			try {
				System.out.println(response.getStatusLine());
				HttpEntity entity2 = response.getEntity();
				EntityUtils.consume(entity2);
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
	}


	

	
}
