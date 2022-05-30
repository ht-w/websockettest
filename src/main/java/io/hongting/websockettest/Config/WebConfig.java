package io.hongting.websockettest.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author hongting
 * @create 2022 05 26 3:33 PM
 */

@Configuration
public class WebConfig  implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/ws").setViewName("websocket");
        registry.addViewController("/ws-stomp").setViewName("websocket-stomp");
        registry.addViewController("/ws-send-user").setViewName("websocket-send-user");
        registry.addViewController("/ws-receive-user").setViewName("websocket-receive-user");
        registry.addViewController("/ws-stomp-receive").setViewName("websocket-stomp-receive");

    }
}
