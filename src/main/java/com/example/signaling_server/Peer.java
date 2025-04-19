package com.example.signaling_server;

import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;

public class Peer {
    private String id;
    private String displayName;
    private WebSocketSession session;
    private Instant lastSeen;

    public Peer(String id, String displayName, WebSocketSession session) {
        this.id = id;
        this.displayName = displayName;
        this.session = session;
        this.lastSeen = Instant.now();
    }

    public String getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public WebSocketSession getSession() {
        return this.session;
    }

    public Instant getLastSeen() {
        return this.lastSeen;
    }
}
