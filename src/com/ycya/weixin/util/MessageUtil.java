package com.ycya.weixin.util;
import com.thoughtworks.xstream.XStream;
import com.ycya.weixin.bean.MessageText;
import com.ycya.weixin.util.toXmlCdata.MyXppDriver;

import java.util.Date;

/**
 * 消息工具接口
 */
public class MessageUtil extends BaseMessageUtil<MessageText> {


//    public String messageToXml(MessageText messageText){
//        XStream xStream = new XStream();
//        xStream.alias("xml",messageText.getClass());
//        return xStream.toXML(messageText);
//    }
    /**
     * 把massage转化成xml
     */
    @Override
    public String messageToXml(MessageText messageText) {
       // XStream xStream = new XStream();
        XStream xStream = new XStream(new MyXppDriver());
        xStream.alias("xml",messageText.getClass());
        return xStream.toXML(messageText);
    }

    /**
     * 回复文本信息
     * @param fromUserName
     * @param toUserName
     * @param content
     * @return
     */
    @Override
    public String initTextMessage(String fromUserName,String toUserName,String content){
        MessageText text = new MessageText();
        String CDATAPrefix = "<![CDATA[";
        String CDATABackfix = "]]>";
        text.setToUserName(CDATAPrefix+fromUserName+CDATABackfix);
        text.setFromUserName(CDATAPrefix+toUserName+CDATABackfix);
        text.setCreateTime(new Date().getTime());
        text.setMsgType(CDATAPrefix+"text"+CDATABackfix);
        text.setContent(CDATAPrefix+content+CDATABackfix);
        String toXml = messageToXml(text);
        return toXml;
    }

}
