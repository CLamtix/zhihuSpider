package com.spider.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller("safeguardController")
@RequestMapping("/safeguard")
public class SafeguardAction {
	
	private static Log log = LogFactory.getLog(SafeguardAction.class);
	
	@RequestMapping(value="admin", method = RequestMethod.GET)
	public String list(String videoName,HttpServletRequest request,ModelMap modelMap){
		//TODO 查询数据库,已爬下来的视频
		return "admin";
	}
	
	
	/**
	 * 获取ip
	 * @param request
	 * @return
	 */
	private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");      
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {      
            ip = request.getHeader("Proxy-Client-IP");      
        }      
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {      
            ip = request.getHeader("WL-Proxy-Client-IP");      
         }      
       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {      
             ip = request.getRemoteAddr();      
        }      
       return ip;      
  }
}
