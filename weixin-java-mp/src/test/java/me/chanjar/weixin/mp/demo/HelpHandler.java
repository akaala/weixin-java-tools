package me.chanjar.weixin.mp.demo;

import java.util.Date;
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

public class HelpHandler implements WxMpMessageHandler, WxMpMessageMatcher {

    private Pattern pattern = Pattern.compile("[帮助,help,说明,Help,HELP].*");

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        WxSession session = sessionManager.getSession(wxMessage.getFromUserName());
        session.setAttribute(SessionStatus.INIT, null);

        WxMpXmlOutTextMessage m
                = WxMpXmlOutMessage.TEXT().content(buildHelpIntro()).fromUser(wxMessage.getToUserName())
                .toUser(wxMessage.getFromUserName()).build();
        return m;
    }

    @Override
    public boolean match(WxMpXmlMessage message) {
        return pattern.matcher(message.getContent()).matches();
    }

    private String buildHelpIntro() {
        String s = "游戏说明：\n" +
                "在设定的场景中，每个人分配到一个词。自己看不到自己的词，但是能够看到其他人的词。" +
                "游戏目的即是诱使对方说出他的词语或作出相应的动作，对方即失败接受惩罚。\n" +
                "规则：必须正面回答问题，不能故意回避。";

        return s;

    }

}
