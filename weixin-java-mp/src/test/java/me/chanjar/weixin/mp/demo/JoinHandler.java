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

public class JoinHandler implements WxMpMessageHandler, WxMpMessageMatcher {

    private Pattern pattern = Pattern.compile("加入.*");

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        WxSession session = sessionManager.getSession(wxMessage.getFromUserName());
        String returnMsg = null;

        Integer roomNum = getRoomNum(wxMessage.getContent().trim());
        Integer index = getIndex(wxMessage.getContent().trim());

        session.setAttribute(SessionStatus.INIT, new Date().toString());
        if (null == roomNum) {
            returnMsg = buildWrongRommMsg();

        } else if (validateRoomNum(roomNum)  ) {  //输入的是存在的房间号
            if (null != session.getAttribute(SessionStatus.ROOM)
                    && roomNum.equals(session.getAttribute(SessionStatus.ROOM)) //检查是否是重复进这个房间
                    && null != session.getAttribute(SessionStatus.INDEX)) {

                String gameInfo = GameInfoCache.getGameInfo(
                        (Integer) session.getAttribute(SessionStatus.ROOM),
                        (Integer) session.getAttribute(SessionStatus.INDEX));
                returnMsg =
                        buildAlreadyHasIndex((Integer) session.getAttribute(SessionStatus.INDEX), gameInfo);
            } else {
                if (null == index) { //错误的index
                    index = GameInfoCache.nextAvailableIndex(roomNum);
                }
                if (null != index) {
                    String gameInfo = GameInfoCache.getGameInfo(roomNum, index);

                    returnMsg =gameInfo;

                } else { // 没有足够的位置了
                    returnMsg =buildNoEnoughIndex(roomNum);
                }
            }
        } else { //输入了无效的房间
            returnMsg =buildWrongRommMsg();
        }


        WxMpXmlOutTextMessage m
                = WxMpXmlOutMessage.TEXT().content(returnMsg).fromUser(wxMessage.getToUserName())
                .toUser(wxMessage.getFromUserName()).build();
        return m;
    }

    private boolean validateRoomNum(Integer roomNum) {
        return GameInfoCache.validateRoomNum(roomNum);
    }

    private String buildNoEnoughIndex(Integer roomNum) {
        return String.format("房间[%d]位置已满。", roomNum);
    }

    private String buildAlreadyHasIndex(Integer index, String gameInfo) {
        return String.format("你已经加入了该房间！\n%s", gameInfo);

    }

    private String buildWrongRommMsg() {
        return "房间号错误。\n如需新建房间，请输入\"新建+人数\"，如\"新建6\"。\n" +
                "或如\"加入1234\"来加入[1234]房间，\n" +
                "或如\"加入1234，3\"来加入[1234]房间并成为3号。";
    }

    private Integer getIndex(String content) {
        String[] strings = content.split("[,,，]");
        if (strings.length < 2) {
            return null;
        } else {
            try {
                return Integer.valueOf(strings[1].trim());
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }

    @Override
    public boolean match(WxMpXmlMessage message) {
        return pattern.matcher(message.getContent()).matches();
    }


    private Integer getRoomNum(String s) {
        if (!s.startsWith("加入") || s.substring("加入".length()).length() <= 0) {
            return null;
        } else {
            String roomNum;
            String[] strings = s.split("[,,，]");
            if (strings.length > 1) {// mean s is "加入1234，5"
                roomNum = strings[0].substring("加入".length());
            } else {
                roomNum = s.substring("加入".length());
            }

            try {
                return Integer.valueOf(roomNum.trim());
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }

}