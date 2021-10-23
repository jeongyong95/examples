package com.joojeongyong.controller;

import com.joojeongyong.model.Message;
import com.joojeongyong.model.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import javax.annotation.Resource;

@Component
public class WebSocketEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Resource
    private SimpMessageSendingOperations operations;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        LOGGER.info("새로운 웹 소켓 커넥션 확보!");
    }

    public void handleWebSocketDisconnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) accessor.getSessionAttributes().get("username");
        if (username != null) {
            LOGGER.info(username + "님이 퇴장하셨습니다.");
            Message message = new Message();
            message.setMessageType(MessageType.LEAVE);
            message.setSender(username);

            operations.convertAndSend("/topic/public", message);
        }
    }
}
