package com.example.signaling_server;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class PeerRegistryService {

    private final Map<String, Peer> peers = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, String> sessionToPeerId = new ConcurrentHashMap<>();

    public void registerPeer(String id, String name, WebSocketSession session) {
        Peer peer = new Peer(id, name, session);
        peers.put(id, peer);
        sessionToPeerId.put(session, id);
    }

    public String removeBySession(WebSocketSession session) {
        String peerId = sessionToPeerId.remove(session);
        if(peerId != null) {
            peers.remove(peerId);
            return peerId;
        }
        return null;
    }

    public List<Map<String, String>> getPeerSummaryList() {
        return peers.values().stream().map(peer -> Map.of(
                "peerId", peer.getId(),
                "displayName", peer.getDisplayName()
        )).collect(Collectors.toList());
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

    public int getLength() {
        return peers.size();
    }
}
