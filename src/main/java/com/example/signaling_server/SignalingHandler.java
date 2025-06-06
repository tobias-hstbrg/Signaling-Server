package com.example.signaling_server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                // Could be written into a log in the future
                boolean successfull = registerPeer(session, msg);
                broadcastRegistry();
                break;

            case "offer":
            case "answer":
            case "ice-candidate":
                Peer target = peerRegistry.getPeer(msg.getDestination());
                if(target != null && target.getSession().isOpen()) {
                    String forward = objectMapper.writeValueAsString(msg);
                    target.getSession().sendMessage(new TextMessage(forward));
                    System.out.println("Forwarded " + msg.getType() + " from " + msg.getSource() + " to " + msg.getDestination());
                }
                else {
                    System.out.println("Failed to forward " + msg.getType() + " - target not found: " + msg.getDestination());
                }
                break;
            case "heartbeat":
                updatePeerHeartbeat(session, msg);
        }

        System.out.println("Message from: " + session.getId() + ": " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {
        //Cleanup logic

        // Remove peer from registry
        String removedPeerId = peerRegistry.removeBySession(session);
        if(removedPeerId != null) {
            broadcastRegistry();
            System.out.println("Removed peer: " + removedPeerId);
        }


        System.out.println("Connection closed: " + session.getId());
    }

    private void  updatePeerHeartbeat(WebSocketSession session, Message msg) throws IOException {
        String peerId = msg.getSource();
        peerRegistry.updateHeartbeat(peerId);

        Map<String, String> response = new HashMap<>();
        response.put("type","heartbeat_ack");
        response.put("peerId", peerId);
        String jsonResponse = objectMapper.writeValueAsString(response);

        //session.sendMessage(new TextMessage("Heartbeat received from " + peerId));
    }
    public void broadcastRegistry() throws IOException {
        // retrieving list of peers
        List<Map<String, String>> peerList = peerRegistry.getPeerSummaryList();

        // generate update response
        Message update = new Message();
        update.setType("peer-list");
        update.setSource("server");
        update.setPayload(peerList);
        String updateJson = objectMapper.writeValueAsString(update);

        // broadcast to all peers the updated list of known peers
        for (Peer peer : peerRegistry.getAllPeers()) {
            peer.getSession().sendMessage(new TextMessage(updateJson));
        }
    }

    private boolean registerPeer(WebSocketSession session, Message msg) throws IOException {
        final int registrySize = peerRegistry.getLength();
        boolean succesfullRegistration = false;
        RegisterPayload payload = objectMapper.convertValue(msg.getPayload(), RegisterPayload.class);
        peerRegistry.registerPeer(payload.getPeerId(), payload.getDisplayName(), session);
        if(peerRegistry.getLength() > registrySize)
        {
            System.out.println("Registered: " + payload.getPeerId());
            succesfullRegistration = true;
        }

        //Confirmation Message
        Message response = new Message();
        response.setType("register-confirmed");
        response.setSource("server");
        response.setDestination(payload.getPeerId());
        response.setPayload("Registration successful!");

        String jsonResponse = objectMapper.writeValueAsString(response);
        session.sendMessage(new TextMessage(jsonResponse));
        return  succesfullRegistration;
    }


}
