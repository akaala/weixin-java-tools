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

public class CreatHandler implements WxMpMessageHandler, WxMpMessageMatcher {

    private Pattern pattern = Pattern.compile("新建.*");

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        WxSession session = sessionManager.getSession(wxMessage.getFromUserName());

        Integer playerNum = getPlayerNum(wxMessage.getContent().trim());

        if (null == playerNum) {
            WxMpXmlOutTextMessage m
                    = WxMpXmlOutMessage.TEXT().content(buildRecreatMsg()).fromUser(wxMessage.getToUserName())
                    .toUser(wxMessage.getFromUserName()).build();
            return m;
        } else {

            Integer roomNumber = GameInfoCache.buildGame(playerNum);

            WxMpXmlOutTextMessage m
                    = WxMpXmlOutMessage.TEXT().content(buildCreatSuccessMsg(playerNum, roomNumber)).fromUser(wxMessage
                    .getToUserName())
                    .toUser(wxMessage.getFromUserName()).build();
            session.setAttribute(SessionStatus.ROOM, roomNumber);
            session.setAttribute(SessionStatus.INDEX, 1);
            return m;
        }
    }

    @Override
    public boolean match(WxMpXmlMessage message) {
        return pattern.matcher(message.getContent()).matches();
    }


    private String buildRecreatMsg() {
        return "新建房间失败，请输入例如\"新建5\"来创建5人房间.";
    }

    private String buildCreatSuccessMsg(int value, int roomNum) {
        return String.format("新建%d人房间[%d]成功。\n" +
                "你的朋友可输入\"加入%d\"或\n" +
                "\"加入,序号\"来一起玩。\n" +
                "你是1号，可看到其他人的词为：\n%s", value, roomNum, roomNum, GameInfoCache.getGameInfo(roomNum, 1));
    }


    private Integer getPlayerNum(String s) {
        if (!s.startsWith("新建") || s.substring("新建".length()).length() <= 0) {
            return null;
        } else {
            String number = s.substring("新建".length());
            try {
                return Integer.valueOf(number);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }

}