package com.ycya.weixin.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ycya.weixin.bean.JsConfig;
import com.ycya.weixin.util.CheckUtil;
import com.ycya.weixin.util.MessageUtil;
import com.ycya.weixin.util.WeiXinUtil;
public class LoginAndMessageService extends HttpServlet{

	private static final long serialVersionUID = 1L;
	/**
	 * 与微信公众号对话框发送消息，接收消息
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 response.setCharacterEncoding("utf-8");
	        MessageUtil messageUtil = new MessageUtil();
	        Map<String, String> map = messageUtil.xmlToMap(request);
	        String toUserName = map.get("ToUserName");
	        String fromUserName = map.get("FromUserName");
	        String msgType = map.get("MsgType");
	        String content = map.get("Content");
	        String sendMessage = null;
	        PrintWriter out = null;
	        if ("text".equals(msgType)) {
	            if (content.equals("1")) {
	                String sendContent = "您好，欢迎关注北斗智慧城市物联！";
	                sendMessage = messageUtil.initTextMessage(fromUserName, toUserName, sendContent);
	            }
	        }
	        try {
	            out = response.getWriter();
	            if (sendMessage != null){
	                out.write(sendMessage);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();

	        }
	            out.close();
	}
	/**
	 * 服务器与微信公众号建立连接
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        if (CheckUtil.checkSignature(signature,timestamp,nonce)){
        	resp.getWriter().write(echostr);
        	System.out.println(echostr);
        }
	}

}
