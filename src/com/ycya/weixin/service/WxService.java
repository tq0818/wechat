package com.ycya.weixin.service;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.leyicat.annotation.api.Api;
import com.leyicat.annotation.api.ApiPara;
import com.leyicat.annotation.api.ApiConst.DataType;
import com.leyicat.annotation.privilege.Privilege;
import com.leyicat.mvc.DefaultService;
import com.leyicat.mvc.MvcConst;
import com.leyicat.util.HttpUtil;
import com.ycya.util.SysUtil;
import com.ycya.weixin.bean.JsConfig;
import com.ycya.weixin.util.UserUtil;
import com.ycya.weixin.util.WeiXinUtil;

import net.sf.json.JSONObject;

public class WxService extends DefaultService{
		
	@Api(name = "webOauth", group = "微信公众号", comments = "微信网页第三方授权", in = {
				
		}, 
	out = {@ApiPara(name = "response", comments = "XXXXX", dataType = DataType.STRING, required = false) })
	@Privilege(comments = "*")
	public void webOauth(HttpServletRequest request, HttpServletResponse resp, ServletContext servletContext)
		throws Exception {
		
		String redirectUri = HttpUtil.getString(request, "redirectUri");
		UserUtil.getCode(redirectUri,resp);//code值，通过回调url中的request获取
	}
	
	@Api(name = "userInfo", group = "微信公众号", comments = "获取用户信息测试接口", in = {
			
	}, 
	out = {@ApiPara(name = "response", comments = "XXXXX", dataType = DataType.STRING, required = false) })
	@Privilege(comments = "*")
	public void userInfo(HttpServletRequest request, HttpServletResponse resp, ServletContext servletContext)
	throws Exception {
	   String code = request.getParameter("code");
       System.out.println("code值："+code);
       JSONObject token = UserUtil.webOauthAccessToken(code);
       JSONObject userInfo = UserUtil.userInfo(token.getString("openid"),token.getString("access_token"));
       //System.out.println(userInfo);
       SysUtil.doRespOut(resp, MvcConst.RESPCODE_OK, MvcConst.RESPCODE_OK_MSG, userInfo);
	}	
	
	@Api(name = "getJsConfig", group = "微信公众号", comments = "获取用户信息测试接口", in = {
			
	}, 
	out = {@ApiPara(name = "response", comments = "XXXXX", dataType = DataType.STRING, required = false) })
	@Privilege(comments = "*")
	public void getJsConfig(HttpServletRequest request, HttpServletResponse resp, ServletContext servletContext)
	throws Exception {
		 String code = request.getParameter("code");
		 String url = request.getParameter("url");
		 WeiXinUtil wuUtil = new WeiXinUtil();
         JsConfig jc = wuUtil.getJsConfig(code,url);
         System.out.println("AppId:"+jc.getAppId()+" NonceStr:"+jc.getNonceStr()+" Signature:"+jc.getSignature()+" Timestamp:"+jc.getTimestamp()+" ticket:"+jc.getTicket());
         SysUtil.doRespOut(resp, MvcConst.RESPCODE_OK, MvcConst.RESPCODE_OK_MSG, jc);
	}	
	
	
@Api(name = "wgs84ToBd09", group = "微信公众号", comments = "获取用户信息测试接口", in = {
			
	}, 
	out = {@ApiPara(name = "response", comments = "XXXXX", dataType = DataType.STRING, required = false) })
	@Privilege(comments = "*")
	public void wgs84ToBd09(HttpServletRequest request, HttpServletResponse resp, ServletContext servletContext)
	throws Exception {
		 String lng = request.getParameter("lng");
		 String lat = request.getParameter("lat");
		 WeiXinUtil wuUtil = new WeiXinUtil();
         Map<String, Double> resMap = wuUtil.wgs84tobd09(Double.parseDouble(lng), Double.parseDouble(lat));
         SysUtil.doRespOut(resp, MvcConst.RESPCODE_OK, MvcConst.RESPCODE_OK_MSG, resMap);
	}	
		
}
