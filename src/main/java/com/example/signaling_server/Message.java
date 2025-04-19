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

    public void setType(String type) {
        this.type = type;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
