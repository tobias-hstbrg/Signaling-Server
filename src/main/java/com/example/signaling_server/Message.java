package com.example.signaling_server;

public class Message {
    private String type;
    private String source;
    private String destination;
    private Object payload;

    public String getType() {
        return this.type;
    }

    public String getSource() {
        return this.source;
    }

    public String getDestination() {
        return this.destination;
    }

    public Object getPayload() {
        return this.payload;
    }
}
