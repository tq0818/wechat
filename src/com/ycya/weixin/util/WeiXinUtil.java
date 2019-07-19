package com.ycya.weixin.util;

import com.leyicat.security.Encode;
import com.leyicat.util.JedisUtil;
import com.sun.org.apache.xerces.internal.dom.PSVIDOMImplementationImpl;
import com.ycya.weixin.Constant.Constant;
import com.ycya.weixin.bean.AccessToken;
import com.ycya.weixin.bean.JsConfig;
import com.ycya.weixin.bean.JsapiTicket;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 用于请求为您url，get、post请求接口
 * 用于获取access_token的接口
 */
public class WeiXinUtil {
   private static Logger logger = LoggerFactory.getLogger(WeiXinUtil.class);
    //测试账号
    private static final String APPID = Constant.APP_ID;
    private static final String APPSECRET = Constant.APP_SECRET;

    private static final String ACCESS_TOKEN_URL ="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    private static final String JSAPI_TICKET_URL ="https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
    /**
     * 处理doget请求
     * @param url
     * @return
     */
    public static JSONObject doGetStr(String url){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
       JSONObject jsonObject = null;
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null){
                String result = EntityUtils.toString(entity);
                jsonObject = JSONObject.fromObject(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 处理post请求
     * @param url
     * @param outStr
     * @return
     */
    public static JSONObject doPostStr(String url,String outStr){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        JSONObject jsonObject = null;

        httpPost.setEntity(new StringEntity(outStr,"utf-8"));
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity(),"utf-8");
            jsonObject = JSONObject.fromObject(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 从微信接口获取token
     * @return
     */
    public AccessToken getAccessToken(){
        logger.info("从微信接口获取AccessToken");
        AccessToken token = new AccessToken();
        String url = ACCESS_TOKEN_URL.replace("APPID",APPID ).replace("APPSECRET",APPSECRET);
        JSONObject jsonObject = doGetStr(url);
        if (jsonObject != null){
            String accessToken = jsonObject.getString("access_token");
            int expiresIn = jsonObject.getInt("expires_in");
            token.setAccess_token(accessToken);
            token.setExpires_in(expiresIn);
            JedisUtil.getJedis().set("access_token", jsonObject.getString("access_token"));
            JedisUtil.getJedis().expire("access_token", expiresIn);
        }
        return token;
    }

    /**
     * 从redis获取AccessToken
     * @return
     */
    public String getAccessTokenRedis(){
        logger.info("从缓存中获取AccessToken");
        Object accessToken = JedisUtil.getJedis().get("access_token");
        if (accessToken != null){
            return accessToken.toString();
        }
        AccessToken token = getAccessToken();
        return token.getAccess_token();
    }
    /**
     * 获取jsapi_ticket
     */
    public JsapiTicket getJsapiTicket(){
    	JsapiTicket jTicket = new JsapiTicket();
    	String accessToken = getAccessTokenRedis();
    	String url = JSAPI_TICKET_URL.replace("ACCESS_TOKEN", accessToken);
    	 JSONObject jsonObject = doGetStr(url);
         if (jsonObject != null){
             String ticket = jsonObject.getString("ticket");
             int expiresIn = jsonObject.getInt("expires_in");
             jTicket.setExpires_in(expiresIn);
             jTicket.setTicket(ticket);
             JedisUtil.getJedis().set("ticket",ticket);
             JedisUtil.getJedis().expire("ticket", expiresIn);
         }
		return jTicket;	
    }
    /**
     * 从redis获取jsapi_ticket
     * @return
     */
    public String getJsapiTicketRedis(){
        logger.info("从缓存中获取jsapi_ticket");
        Object ticket = JedisUtil.getJedis().get("ticket");
        if (ticket != null){
            return ticket.toString();
        }
        JsapiTicket jTicket = getJsapiTicket();
        return jTicket.getTicket();
    }
    /**
     *获取引用js的配置
     * @return
     */
    public JsConfig getJsConfig(String code,String url){
    	String ticket= getJsapiTicketRedis();
    	String timestamp = String.valueOf(System.currentTimeMillis()/1000);//时间戳
//    	String url = "http://2270m4b234.51mypc.cn/wechat/toiletWeb/index.html";
//    	String lastUrl = "?code="+code+"&state=STATE";
    	String noncestr =  UUID.randomUUID().toString();
    	String signature = "";
    	//将参数排序并拼接字符串
    	String str="";
		try {
			str = "jsapi_ticket="+ticket+"&noncestr="+noncestr+"&timestamp="+timestamp+"&url="+URLDecoder.decode(url,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	//将字符串进行sha1加密
    	MessageDigest crypt;
		try {
			crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
	        crypt.update(str.getBytes("UTF-8"));
	        signature = byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}   
		JsConfig jsConfig = new JsConfig();
		jsConfig.setAppId(APPID);
		jsConfig.setNonceStr(noncestr);
		jsConfig.setSignature(signature);
		jsConfig.setTimestamp(timestamp);
		jsConfig.setTicket(ticket);
		try {
			System.out.println("url:"+URLDecoder.decode(url,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return jsConfig;
    }
    
    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
    /**
     * WGS84 转换为 BD-09
     * @param lng
     * @param lat
     * @returns {*[]}
     * 
     */
    public Map<String, Double> wgs84tobd09(double lng, double lat){
        //第一次转换
        double dlat = transformlat(lng - 105.0, lat - 35.0);
        double dlng = transformlng(lng - 105.0, lat - 35.0);
        double radlat = lat / 180.0 * PI;
        double magic = Math.sin(radlat);
        magic = 1 - ee * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
        double mglat = lat + dlat;
        double mglng = lng + dlng;

        //第二次转换
        double z = Math.sqrt(mglng * mglng + mglat * mglat) + 0.00002 * Math.sin(mglat * x_PI);
        double theta = Math.atan2(mglat, mglng) + 0.000003 * Math.cos(mglng * x_PI);
        double bd_lng = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        Map<String, Double> map = new HashMap<>();
        map.put("lng", bd_lng);
        map.put("lat", bd_lat);
        return map;
//        return bd_lng+","+bd_lat;
  }
    private double x_PI = 3.14159265358979324 * 3000.0 / 180.0;
    private double PI = 3.1415926535897932384626;
    private double a = 6378245.0;
    private double ee = 0.00669342162296594323;
    private double transformlat(double lng,double lat){
        double ret= -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private double transformlng(double lng,double lat){
        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }
}
