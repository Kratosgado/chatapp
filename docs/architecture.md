# Architecture Overview

The Chat App is a full-stack real-time communication platform built with a modern technology stack.

## Tech Stack

### Backend
- **Framework**: Spring Boot 3.2+ (Kotlin)
- **Database**: PostgreSQL (Hibernate/JPA)
- **Security**: Spring Security + JWT
- **Real-time**: Spring WebSocket (STOMP + SockJS)
- **Build Tool**: Gradle (Kotlin DSL)

### Frontend
- **Framework**: Vue 3 (Composition API, `<script setup>`)
- **State Management**: Pinia
- **UI Framework**: Nuxt UI / Tailwind CSS
- **Build Tool**: Vite
- **Package Manager**: pnpm

## High-Level Architecture

The system consists of a monolithic backend API and a single-page application (SPA) frontend. Communication happens over REST APIs for standard CRUD operations (Users, Chats, Auth) and WebSocket for real-time features (Messaging, Typing indicators, WebRTC signaling).

### Key Components

1.  **Authentication**:
    -   Users register/login via REST endpoints (`/api/auth/*`).
    -   JWT tokens are issued upon successful login.
    -   Tokens are sent in the `Authorization` header for protected API calls.
    -   WebSocket connections are secured by validating the JWT token during the STOMP `CONNECT` phase.

2.  **Messaging**:
    -   Chat history is persisted in PostgreSQL.
    -   Real-time messages are broadcast via WebSocket topics (`/topic/chats/{chatId}`).
    -   Typing indicators are ephemeral events broadcast to chat participants.

3.  **WebRTC Calls**:
    -   Signaling (SDP offer/answer, ICE candidates) is handled via WebSocket using STOMP queues (`/queue/calls`).
    -   Media streams are established directly between peers (P2P).
    -   STUN server (Google's public STUN) is used for NAT traversal.

## Database Schema (Simplified)

-   **Users**: stores user credentials and profile info.
-   **ChatRooms**: represents a conversation (group or 1-on-1).
-   **Messages**: stores content, sender, timestamp, and chat association.
-   **ChatRoomMembers**: join table linking Users to ChatRooms.
