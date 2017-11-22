package com.spider.thief;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;

public class BeanFactory {
	
	public static CloseableHttpClient httpclient;
	
	public static CloseableHttpClient createSSLInsecureClient() {
		if(httpclient!=null){
			return httpclient;
		}
		
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(
					null, new TrustStrategy() {
						// 默认信任所有证书
						public boolean isTrusted(X509Certificate[] arg0,
								String arg1) throws CertificateException {
							return true;
						}
					}).build();
			// AllowAllHostnameVerifier: 这种方式不对主机名进行验证，验证功能被关闭，是个空操作(域名验证)
			SSLConnectionSocketFactory sslcsf = new SSLConnectionSocketFactory(sslContext,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			return HttpClients.custom().setSSLSocketFactory(sslcsf).build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return HttpClients.createDefault();
	}
}
