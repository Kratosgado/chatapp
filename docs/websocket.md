# WebSocket Protocol

The application uses the STOMP protocol over WebSocket (SockJS fallback).

## Endpoint

-   **URL**: `/ws/websocket` (or `/ws` for SockJS)
-   **Security**: Requires `Authorization: Bearer <JWT>` in `CONNECT` frame.

## Topics

### Public

-   `/topic/chats/{chatId}`: Messages for a specific chat room.
    -   Payload: `MessageDto` (id, content, senderId, timestamp, ...)

-   `/topic/chats/{chatId}/typing`: User typing status.
    -   Payload: `{ userId: string, isTyping: boolean }`

### Private (User-specific)

-   `/user/queue/calls`: Incoming call signaling messages (offers, answers, ICE candidates).
    -   Payload: `CallOfferDto | CallAnswerDto | IceCandidateDto | CallActionDto`

## Sending Messages

-   `/app/chat.sendMessage/{chatId}`: Send a message to a chat.
    -   Payload: `{ content: string }`

-   `/app/chat.typing/{chatId}`: Update typing status.
    -   Payload: `{ isTyping: boolean }`

-   `/app/call.offer`: Send a call offer (SDP).
    -   Payload: `CallOfferDto`

-   `/app/call.answer`: Send a call answer (SDP).
    -   Payload: `CallAnswerDto`

-   `/app/call.ice`: Send ICE candidate.
    -   Payload: `IceCandidateDto`

-   `/app/call.action`: Send call action (e.g., REJECT, END, BUSY).
    -   Payload: `CallActionDto`

## Client Example

```typescript
import { Client } from "@stomp/stompjs";

const client = new Client({
  brokerURL: "ws://localhost:8080/ws/websocket",
  connectHeaders: {
    Authorization: "Bearer <YOUR_JWT_TOKEN>",
  },
});

client.onConnect = () => {
  client.subscribe("/user/queue/calls", (message) => {
    console.log("Incoming call:", JSON.parse(message.body));
  });
};

client.activate();
```
