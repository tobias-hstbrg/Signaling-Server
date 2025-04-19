package com.example.signaling_server;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PeerRegistryService {

    private final Map<String, Peer> peers = new ConcurrentHashMap<>();

    public void registerPeer(String id, String name, WebSocketSession session) {
        peers.put(id, new Peer(id, name, session));
    }

    public void updateHeartbeat(String id) {
        Peer peer = peers.get(id);
        if (peer != null) {
            peer.setLastSeen(Instant.now());
        }
    }

    public void removePeer(String id) {
        peers.remove(id);
    }

    public Collection<Peer> getAllPeers() {
        return peers.values();
    }

    public Peer getPeer(String id) {
        return peers.get(id);
    }
}
