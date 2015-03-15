package me.chanjar.weixin.mp.demo;

import java.io.InputStream;

import me.chanjar.weixin.mp.demo.jizhang.JZInitHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.*;

public class WxMpDemoServer {

    private static WxMpConfigStorage wxMpConfigStorage;
    private static WxMpService wxMpService;
    private static WxMpMessageRouter wxMpMessageRouter;

    public static void main(String[] args) throws Exception {
        initWeixin();

        Server server = new Server(80);

        // test change for upload to github.
        ServletHandler servletHandler = new ServletHandler();
        server.setHandler(servletHandler);

        ServletHolder endpointServletHolder = new ServletHolder(new WxMpEndpointServlet(wxMpConfigStorage, wxMpService, wxMpMessageRouter));
        servletHandler.addServletWithMapping(endpointServletHolder, "/*");

        ServletHolder oauthServletHolder = new ServletHolder(new WxMpOAuth2Servlet(wxMpService));
        servletHandler.addServletWithMapping(oauthServletHolder, "/oauth2/*");

        server.start();
        server.join();
    }

    private static void initWeixin() {
        InputStream is1 = ClassLoader.getSystemResourceAsStream("test-config.xml");
        WxMpDemoInMemoryConfigStorage config = WxMpDemoInMemoryConfigStorage.fromXml(is1);

        wxMpConfigStorage = config;
        wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(config);

        WxMpMessageHandler logHandler = new DemoLogHandler();
        WxMpMessageHandler textHandler = new DemoTextHandler();
        WxMpMessageHandler imageHandler = new DemoImageHandler();
        WxMpMessageHandler oauth2handler = new DemoOAuth2Handler();
        DemoGuessNumberHandler guessNumberHandler = new DemoGuessNumberHandler();
        CreatHandler creatHandler = new CreatHandler();
        JoinHandler joinHandler = new JoinHandler();
        ExitHandler exitHandler = new ExitHandler();
        HelpHandler helpHandler = new HelpHandler();

        JZInitHandler jzInitHandler = new JZInitHandler();

        wxMpMessageRouter = new WxMpMessageRouter(wxMpService);
        wxMpMessageRouter
                .rule().handler(logHandler).next()
//                .rule().msgType(WxConsts.XML_MSG_TEXT).matcher(guessNumberHandler).handler(guessNumberHandler).end()
                // async(true) 会导致收不到
                .rule().async(false).matcher(creatHandler).handler(creatHandler).next()
                .rule().async(false).matcher(joinHandler).handler(joinHandler).next()
                .rule().async(false).matcher(exitHandler).handler(exitHandler).next()
                .rule().async(false).matcher(helpHandler).handler(helpHandler).next()

                .rule().async(false).matcher(jzInitHandler).handler(jzInitHandler).next()
//                .rule().async(false).content("测试").handler(textHandler).next()
//                .rule().async(false).rContent(".*").handler(textHandler).next()
//                .rule().async(false).rContent("记账.*").handler(jzHandler).next()
                .rule().async(false).content("图片").handler(imageHandler).end()
                .rule().async(false).content("oauth").handler(oauth2handler).end()
        ;

    }
}
