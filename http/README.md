# HTTP Request Collection

This directory contains HTTP request files for testing the backend API using the Kulala extension or similar HTTP clients (like IntelliJ HTTP Client).

## Setup
1. Ensure the backend server is running at `http://localhost:8080`.
2. The requests use environment variables (`authToken`, `userId`, etc.) that are set automatically by scripts within the `.http` files.

## Usage Order
To effectively test the application flow, run the requests in the following order:

1. **`auth.http`**:
   - Run **Register User** first to create a user.
   - Run **Login** to authenticate and store the `authToken` and `userId` globally.

2. **`users.http`**:
   - Run **Search Users** to find another user (you might need to register a second user first) and store their ID as `otherUserId`.
   - Run other user-related endpoints as needed.

3. **`friends.http`**:
   - Requires `otherUserId` to be set (from `users.http`).
   - Run **Send Friend Request**.
   - Login as the second user to **Get Pending Requests** and **Respond to Request**.

4. **`chats.http`**:
   - Requires `otherUserId` to be set.
   - Run **Create Chat** to start a conversation.
   - Run **Send Message** and **Get Messages**.

5. **`ws.http`**:
   - Provides WebSocket connection details and STOMP frame examples for real-time chat testing.
   - Note: The server uses STOMP over SockJS, so raw WebSocket clients need to handle the STOMP protocol manually.
