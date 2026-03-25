# WebRTC Implementation

The application uses WebRTC for real-time audio/video calls.

## Signaling Flow

1. **Start Call**:
    - Caller creates `RTCPeerConnection`.
    - Gets local media stream (`navigator.mediaDevices.getUserMedia`).
    - Creates Offer (`pc.createOffer()`).
    - Sends Offer via WebSocket (`/app/call.offer`).

2. **Incoming Call**:
    - Receiver subscribes to `/user/queue/calls`.
    - Receives `CallOfferDto`.
    - Opens `IncomingCallModal`.
    - Stores `sdp` (temporarily).

3. **Accept Call**:
    - Receiver accepts call.
    - Gets local media stream.
    - Creates `RTCPeerConnection`.
    - Sets Remote Description (from offer).
    - Creates Answer (`pc.createAnswer()`).
    - Sends Answer via WebSocket (`/app/call.answer`).

4. **Answer Handling**:
    - Caller receives `CallAnswerDto`.
    - Sets Remote Description (from answer).
    - Connection established.

5. **ICE Candidates**:
    - Peers exchange ICE candidates via `/app/call.ice`.
    - Candidates are added via `pc.addIceCandidate()`.
    - NAT traversal uses Google's STUN server (`stun:stun.l.google.com:19302`).

## Data Structures

### CallOfferDto

```json
{
  "callId": "unique-call-id",
  "callerId": "user-uuid",
  "targetUserId": "target-uuid",
  "sdp": "session-description-protocol-string",
  "type": "offer"
}
```

### CallAnswerDto

```json
{
  "callId": "unique-call-id",
  "callerId": "caller-uuid",
  "responderId": "user-uuid",
  "sdp": "session-description-protocol-string",
  "type": "answer"
}
```

### IceCandidateDto

```json
{
  "callId": "unique-call-id",
  "targetUserId": "target-uuid",
  "candidate": "candidate-string",
  "sdpMid": "audio",
  "sdpMLineIndex": 0,
  "type": "ice-candidate"
}
```

### CallActionDto

```json
{
  "callId": "unique-call-id",
  "targetUserId": "target-uuid",
  "action": "REJECT | BUSY | END",
  "type": "call-action"
}
```
