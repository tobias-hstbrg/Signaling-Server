package com.example.signaling_server;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;
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

    // inactive peer cleanup
    @Scheduled(fixedRate = 60000)
    public void cleanupInactivePeers() throws IOException {
        Instant currentTime = Instant.now();
        Duration timeout = Duration.ofSeconds(30);

        Iterator<Map.Entry<String, Peer>> iterator = peers.entrySet().iterator();

        while(iterator.hasNext())
        {
            Map.Entry<String, Peer> entry = iterator.next();
            Peer peer = entry.getValue();

            // Check if the peer is inactive for longer then the set duration.
            if (peer.getLastSeen().isBefore(currentTime.minus(timeout))) {
                iterator.remove();
                try {
                    peer.getSession().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Removed inactive peer: " + peer.getId());
            }
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
