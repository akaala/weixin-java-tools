package me.chanjar.weixin.mp.demo;

import java.util.Map;
import java.util.regex.Pattern;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSession;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpMessageMatcher;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutTextMessage;

public class JZHandler implements WxMpMessageHandler, WxMpMessageMatcher{

    private Pattern pattern = Pattern.compile("记账.*");

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        WxSession session = sessionManager.getSession(wxMessage.getFromUserName());

//        if (session.getAttribute("jz") == null) //new action




//        WxMpXmlOutTextMessage m
//                = WxMpXmlOutMessage.TEXT().content("记账").fromUser(wxMessage.getToUserName())
//                .toUser(wxMessage.getFromUserName()).build();
//        System.out.println("build response msg: " + m.getContent());
//        return m;
        return  null;
    }

    @Override
    public boolean match(WxMpXmlMessage message) {
        return pattern.matcher(message.getContent()).matches();
    }


//    private String buildIntro() {
//        WxMpXmlOutTextMessage m
//                = WxMpXmlOutMessage.TEXT().content("记账").fromUser(wxMessage.getToUserName())
//                .toUser(wxMessage.getFromUserName()).build();
//    }


}
