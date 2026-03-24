# Demo Chat App (Spring Boot + Vue)

![Backend](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)
![Frontend](https://img.shields.io/badge/Vue-3-green.svg)

This is a full-featured real-time chat application built with Spring Boot (Kotlin) and Vue 3 (TypeScript). It supports user authentication, private/group messaging, typing indicators, and WebRTC audio calls.

## Features

### Backend
- **Authentication**: JWT-based login/registration.
- **Real-time Messaging**: WebSocket (STOMP) for instant message delivery.
- **WebRTC Signaling**: Handling call offers, answers, and ICE candidates.
- **Persistence**: PostgreSQL database with JPA/Hibernate.
- **Security**: Spring Security + JWT.

### Frontend
- **Modern UI**: Built with Nuxt UI and Tailwind CSS.
- **State Management**: Pinia for reactive data handling.
- **Real-time Updates**: Live chat updates and typing status.
- **Voice Calls**: WebRTC integration for peer-to-peer audio calls.

## Documentation

- [Setup Guide](docs/setup.md): Instructions to run the project locally.
- [Architecture Overview](docs/architecture.md): High-level system design.
- [WebSocket Protocol](docs/websocket.md): Details on real-time communication.
- [WebRTC Implementation](docs/webrtc.md): How voice calls work.

## Quick Start

### Backend

```bash
cd backend
./gradlew bootRun
```

### Frontend

```bash
cd frontend
pnpm install
pnpm dev
```

Visit `http://localhost:5173` to access the application.

## License

MIT
