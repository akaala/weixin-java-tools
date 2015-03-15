package demo.jizhang;

import demo.SessionStatus;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSession;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpMessageMatcher;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutTextMessage;

import java.util.Map;
import java.util.regex.Pattern;

public class JZInitHandler implements WxMpMessageHandler, WxMpMessageMatcher {

    private Pattern pattern = Pattern.compile("记账.*");
    private Pattern editingPattern = Pattern.compile("记账 \\d* \\d*.*");
    private Pattern ackPattern = Pattern.compile("记账 [1,确认].*");
    private Pattern reEditPattern = Pattern.compile("记账 [2,修改]");

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        WxSession session = sessionManager.getSession(wxMessage.getFromUserName());

        String editInfo = (String) session.getAttribute(SessionStatus.EDIT_ACK);

        if (editingPattern.matcher(wxMessage.getContent()).matches()) { // is editing
            session.setAttribute(SessionStatus.EDIT_ACK, wxMessage.getContent());
            return ackMessage(wxMessage);
        }

        if (null == editInfo) {
            return startNewRecord(wxMessage);
        } else {
            String ackMsg = wxMessage.getContent();
            if (ackPattern.matcher(ackMsg).matches()) {
                saveToStorage();
                sendMail();
                return outputAcked(wxMessage);
            } else {
                session.removeAttribute(SessionStatus.EDIT_ACK);
                return startNewRecord(wxMessage);
            }
        }
    }

    private WxMpXmlOutMessage ackMessage(WxMpXmlMessage wxMessage) {
        WxMpXmlOutTextMessage m
                = WxMpXmlOutMessage.TEXT().content(handleMsg()).fromUser(wxMessage.getToUserName())
                .toUser(wxMessage.getFromUserName()).build();
        return m;
    }

    private String handleMsg() {
        return "解析记账格式，返回记账结果";
    }

    private void sendMail() {

    }

    private void saveToStorage() {

    }

    private WxMpXmlOutTextMessage outputAcked(WxMpXmlMessage wxMessage) {
        WxMpXmlOutTextMessage m
                = WxMpXmlOutMessage.TEXT().content(buidlAckMsg()).fromUser(wxMessage.getToUserName())
                .toUser(wxMessage.getFromUserName()).build();
        return m;
    }

    private String buidlAckMsg() {
        return "记账完成，结果会发送到邮件中。(会再次展示总计)";
    }

    private WxMpXmlOutTextMessage startNewRecord(WxMpXmlMessage wxMessage) {
        String peopleList = JZHelper.getPeopleIdList();
        String history = JZHelper.getHistory();

        WxMpXmlOutTextMessage m
                = WxMpXmlOutMessage.TEXT().content(buildStartMsg() + peopleList + history)
                .fromUser(wxMessage.getToUserName())
                .toUser(wxMessage.getFromUserName()).build();
        return m;
    }

    @Override
    public boolean match(WxMpXmlMessage message) {
        return pattern.matcher(message.getContent()).matches();
    }

    private String buildStartMsg() {
        return "记账格式：\n" +
                "记账 金额 付款人序号 其他人序号\n" +
                "例如：\"记账 90 1 2 3\"";
    }
}