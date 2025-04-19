package com.example.signaling_server;

public class RegisterPayload {
    private String peerId;
    private String displayName;

    public String getPeerId() {
        return this.peerId;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
