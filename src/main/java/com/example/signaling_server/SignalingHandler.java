package com.example.signaling_server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class SignalingHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final PeerRegistryService peerRegistry;

    public SignalingHandler(ObjectMapper objectMapper, PeerRegistryService peerRegistry) {
        this.objectMapper = objectMapper;
        this.peerRegistry = peerRegistry;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // Called when a client connects
        System.out.println("New Connection: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Handle incoming messages here
        Message msg = objectMapper.readValue(message.getPayload(), Message.class);

        switch (msg.getType()) {
            case "register":
                RegisterPayload payload = objectMapper.convertValue(msg.getPayload(), RegisterPayload.class);
                peerRegistry.registerPeer(payload.getPeerId(), payload.getDisplayName(), session);
                System.out.println("Registered: " + payload.getPeerId());

                //Confirmation Message
                Message response = new Message();
                response.setType("register-confirmed");
                response.setSource("server");
                response.setDestination(payload.getPeerId());
                response.setPayload("Registration successful!");

                String jsonResponse = objectMapper.writeValueAsString(response);
                session.sendMessage(new TextMessage(jsonResponse));
                break;
        }

        System.out.println("Message from: " + session.getId() + ": " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        //Cleanup logic
        System.out.println("Connection closed: " + session.getId());
    }


}
