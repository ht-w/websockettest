package io.hongting.websockettest.controller;

import io.hongting.websockettest.service.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @author hongting
 * @create 2022 05 30 1:40 AM
 */
@RestController
public class WebSocketController {
    private final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private WebSocketService webSocketService;

    @MessageMapping("/send")
    @SendTo("/sub/chat")
    public String sendMessage(String value) {
        logger.info("Content of sent message：{}", value);
        return value;
    }

    @MessageMapping("/sendToUser")
    public void sendToUser(Principal principal, String body) {
        String srcUser = principal.getName();
        String[] args = body.split(": ");
        String desUser = args[0];
        String message = String.format("【%s】sent you a message：%s", webSocketService.getUser().get(srcUser), args[1]);

        // send to user and listening address
        simpMessagingTemplate.convertAndSendToUser(desUser, "/queue/customer", message);

    }

}