package com.spider.thief;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
