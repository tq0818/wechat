package com.ycya.weixin.Constant;

public class Constant {
    public static String APP_ID = "wxf77bb8156fbd446e";
    public static String APP_SECRET = "a9da56f7135f8ca1dfc861deac90b36e";
    /**
     * snsapi_base（不弹出授权页面，直接跳转，只能获取用户openid）
     * snsapi_userinfo （弹出授权页面，可通过openid拿到昵称、性别、所在地。并且，即使在未关注的情况下，只要用户授权，也能获取其信息 ）
     */
    public static String SCOPE = "snsapi_userinfo";//网页授权作用域

}
