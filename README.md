# 🌐 Simple WebRTC Signaling Server

This is a lightweight signaling server built to support **WebRTC peer-to-peer communication**. It handles basic peer registration, connection offer/answer forwarding, and detects inactive peers to keep the session clean.

🛠️ **Built with Java + Spring Boot**

> ⚠️ This project is designed for educational/demo purposes. Error handling is minimal and should be improved before any production use.

---

## 🚀 Getting Started

### 1️⃣ Clone the Repository

```bash
git clone git@github.com:tobias-hstbrg/Signaling-Server.git
cd signaling-server
```

---

### 2️⃣ API Overview 📡 (Websocket)

## 🔐 Register Peer with server
In order to register your peer with the server you have to send this json snippet to the server. 
 - type: has to stay in order for the server to know this is a peer registration
 - source: The peer sending the request can be anything really
 - peerId: A randomized Id you generate on the client side and send it to the sever in case somehow another peer has the same displayName
 - displayName: Just so the naming isnt as obscure as it would be just by some randomized Id
```json
{
    "type": "register",
    "source": "peer-A",
    "payload": {
        "peerId": "peer-A",
        "displayName": "PostmanClient1"
    }
}
```

## 📨 Peer offer
Once registered your peer can send an offer to another peer. This happens in the offer where you set source and destination as well as the sdp or Session Description Protocol. Your WebRTC Client Library of choice should create that for you.
The server will provide your peer with information to other connected peers once registred
- source: Your Peer
- destination: The peer you want to connect to
- sdp: The Session Description Protocol offer created by your WebRTC Library of choice
```json
{
    "type": "offer",
    "source": "peer-A",
    "destination": "peer-B",
    "payload": {
        "type": "offer",
        "sdp": "fake-sdp-offer"
    }
}
```

## 📬 Peer answer
This is the anwser to the offer that the server forwarded
- source: The peer that recieved the offer
- destination: The peer that made the offer
- sdp: The Session Description Protocol answer your WebRTC Library of choice created
```json
{
    "type": "answer",
    "source": "peer-B",
    "destination": "peer-A",
    "payload": {
        "type": "answer",
        "sdp": "fake-sdp-awnser"
}
```
## ❤️ Heartbeat
This is a message that has to be send to the server in intervals so the server knows the peer is still active.
The interval at which this heartbeat is required can be set here see: [`cleanupInactivePeers()` in PeerRegistryService.java](https://github.com/tobias-hstbrg/Signaling-Server/blob/master/src/main/java/com/example/signaling_server/PeerRegistryService.java#L57)
```json
{
    "type": "heartbeat",
    "source": "peer-B"
}
```

### 📄 License
This project is licensed under the [MIT License](./LICENSE).
Feel free to use, modify, and share!
