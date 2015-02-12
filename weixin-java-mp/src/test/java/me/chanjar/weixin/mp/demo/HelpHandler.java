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

public class HelpHandler implements WxMpMessageHandler, WxMpMessageMatcher {

    private Pattern pattern = Pattern.compile("[帮助,help,说明,Help,HELP].*");

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        WxSession session = sessionManager.getSession(wxMessage.getFromUserName());

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
        String s = "help introduce：... To Be Continued.";

        return s;

    }

}
