package com.ycya.weixin.util;

import com.ycya.weixin.bean.Button;
import com.ycya.weixin.bean.ClickButton;
import com.ycya.weixin.bean.Menu;
import com.ycya.weixin.bean.ViewButton;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 创建微信菜单的工具
 */
public class MenuUtil {
    static  Logger logger = LoggerFactory.getLogger(MenuUtil.class);
    private static final String CREATE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";

    /**
     * 创建菜单，返回0则创建成功
     * @param accessToken
     * @param menu
     * @return
     */
    public static int createMenu(String accessToken,String menu){
        String url = CREATE_MENU_URL.replace("ACCESS_TOKEN",accessToken);
        JSONObject jsonObject = WeiXinUtil.doPostStr(url,menu);
        int errcode = Integer.MIN_VALUE;
        if (jsonObject != null){
            errcode =  jsonObject.getInt("errcode");
        }
       return  errcode;
    }

    /**
     * 初始化菜单
     * @return
     */
    public static String initMenu(){
        ClickButton button10 = new ClickButton();
        button10.setName("公司介绍");
        button10.setKey("11");
        button10.setType("click");

        ViewButton button21 = new ViewButton();
        button21.setName("井盖");
        //需要微信认证（公司未开通）
        //button20.setUrl("http://2270m4b234.51mypc.cn/iot/service/wx/webOauth?redirectUri=http://2270m4b234.51mypc.cn/iot/service/wx/userInfoTest");    
        button21.setUrl("http://192.168.0.160:8001");//不需要微信认证（改为自己的前端页面网址即可）
        button21.setType("view");
        
        ViewButton button22 = new ViewButton();
        button22.setName("路灯");
        button22.setUrl("https://www.baidu.com/");//不需要微信认证（改为自己的前端页面网址即可）
        button22.setType("view");
        
        ViewButton button23 = new ViewButton();
        button23.setName("停车场");
        button23.setUrl("https://www.baidu.com/");//不需要微信认证（改为自己的前端页面网址即可）
        button23.setType("view");
        
        ViewButton button24 = new ViewButton();
        button24.setName("公厕");
        button24.setUrl("http://2270m4b234.51mypc.cn/wechat/service/wx/webOauth?redirectUri=http://2270m4b234.51mypc.cn/wechat/toiletWeb/index.html");//不需要微信认证（改为自己的前端页面网址即可）
        button24.setType("view");
        
        
        ClickButton button20 = new ClickButton();
        button20.setName("系统");
        button20.setType("click");
        button20.setSub_button(new Button[]{button21,button22,button23,button24});

        ClickButton button31 = new ClickButton();
        button31.setType("pic_photo_or_album");
        button31.setName("拍照");
        button31.setKey("31");

        ClickButton button32 = new ClickButton();
        button32.setType("scancode_push");
        button32.setName("发送位置");
        button32.setKey("32");

        ClickButton button30 = new ClickButton();
        button30.setName("菜单");
        button30.setType("click");
        button30.setSub_button(new Button[]{button31,button32});

        Menu menu = new Menu();
        menu.setButton(new Button[]{button10,button20,button30});
        logger.info(JSONObject.fromObject(menu).toString());
        return JSONObject.fromObject(menu).toString();

    }


}
