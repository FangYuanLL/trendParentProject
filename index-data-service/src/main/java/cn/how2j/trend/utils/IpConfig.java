package cn.how2j.trend.utils;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class IpConfig implements ApplicationListener<WebServerInitializedEvent> {
    private int port;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {
        this.port = webServerInitializedEvent.getWebServer().getPort();
    }

    public int getPort() {
        return port;
    }
}
