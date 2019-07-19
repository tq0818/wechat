package com.ycya.weixin.util;

import com.ycya.weixin.Constant.Constant;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UserUtil {
    private static  Logger logger = LoggerFactory.getLogger(UserUtil.class);
    private static final String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE&connect_redirect=1#wechat_redirect";
    private static final String getAccessTokenUrl="https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    private static final String getUserInfoUrl="https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
    /**
     * 网页授权认证，重定向到第三方页面（得到的结果可从request里面获取code值）
     * @param redirectUri
     * @param response
     */
    public static void getCode(String redirectUri, HttpServletResponse response){
        String encodeUrl = null;
        try {
            encodeUrl = URLEncoder.encode(redirectUri,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String getCodeUrl = url.replace("APPID", Constant.APP_ID).replace("REDIRECT_URI",encodeUrl).replace("SCOPE",Constant.SCOPE);
        try {
            response.sendRedirect(getCodeUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 网页授权获取access_token
     * @return
     */
    public static JSONObject webOauthAccessToken(String code){
      String url = getAccessTokenUrl.replace("APPID",Constant.APP_ID).replace("SECRET",Constant.APP_SECRET).replace("CODE",code);
      JSONObject jsonObject = WeiXinUtil.doGetStr(url);
      logger.info("accessToken信息："+jsonObject);
      return jsonObject;
    }

    /**
     * 拉取用户信息(需scope为 snsapi_userinfo)
     * @param openId
     * @param accessToken
     * @return
     */
    public static JSONObject userInfo(String openId,String accessToken){
        String url = getUserInfoUrl.replace("ACCESS_TOKEN",accessToken).replace("OPENID",openId);
        JSONObject jsonObject = WeiXinUtil.doGetStr(url);
        logger.info("web授权获取的用户信息："+jsonObject);
        return jsonObject;
    }


}
