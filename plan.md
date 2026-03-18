# ChatApp Implementation Plan

## Overview

A real-time chat application with WebSocket support, user authentication, direct messages, group chats, and a contacts/friends system.

## Phase 1: Backend Foundation

### 1.1 Complete Authentication System

- [x] **Implement Login Endpoint**: Complete `AuthController.login()` with JWT token generation
- [x] **Create JWT Filter**: `JwtAuthenticationFilter` to validate tokens on protected routes
- [x] **Create Logout Endpoint**: Clear JWT cookie on client
- [x] **Create Auth DTOs**: Add `LoginResponseDto`, `UserDto` for API responses

### 1.2 User Management

- [x] **User Profile Endpoint**: `GET /api/users/me` - Get current user
- [x] **Update Profile Endpoint**: `PUT /api/users/me` - Update name, avatar
- [x] **Get User by ID**: `GET /api/users/{id}` - Search users

### 1.3 Contacts/Friends System

**Entities:**

- `FriendRequest` - sender, receiver, status (PENDING, ACCEPTED, REJECTED)
- `Friendship` - established relationship (optional, or use status on request)

**Endpoints:**

- `POST /api/friends/requests` - Send friend request
- `GET /api/friends/requests` - List incoming requests
- `PUT /api/friends/requests/{id}` - Accept/Reject request
- `GET /api/friends` - List accepted friends
- `DELETE /api/friends/{id}` - Remove friend

### 1.4 Chat System

**Entities:**

- `ChatRoom` - id, name (nullable for DMs), isGroup, createdAt
- `ChatRoomMember` - chatRoom, user, role (OWNER, ADMIN, MEMBER)
- `Message` - id, chatRoom, sender, content, sentAt, isRead

**Endpoints:**

- `POST /api/chats` - Create chat (DM or group)
- `GET /api/chats` - List user's chats
- `GET /api/chats/{id}/messages` - Get messages (paginated)
- `POST /api/chats/{id}/messages` - Send message (REST fallback)

### 1.5 WebSocket Configuration

**STOMP over WebSocket:**

- Endpoint: `/ws`
- Subscribe: `/user/queue/messages` - Personal message queue
- Subscribe: `/topic/chats/{chatId}` - Chat room messages
- Send: `/app/chat.sendMessage` - Send message
- Send: `/app/chat.typing` - Typing indicator

## Phase 2: Frontend Foundation

### 2.1 API Service Layer

- [x] **Create Axios Instance**: Base URL, interceptors for JWT, error handling
- [x] **Auth Service**: Login, register, logout, token refresh
- [x] **User Service**: Profile CRUD
- [x] **Chat Service**: Chat CRUD, messages
- [x] **Friend Service**: Friend requests, list

### 2.2 Pinia Stores

- [x] **auth**: user, token, isAuthenticated, login, logout, fetchProfile
- [x] **chats**: chats[], currentChat, messages[], loadChats, sendMessage, receiveMessage
- [x] **friends**: friends[], requests[], loadFriends, sendRequest, respondToRequest
- [x] **ui**: sidebarOpen, activeModal

### 2.3 Nuxt UI Integration

**Key Components to Use:**

- `UButton`, `UInput`, `UForm`, `UFormField` - Forms
- `UAvatar`, `UBadge` - User display
- `UCard`, `UModal` - Containers
- `UDropdown`, `USelect` - Selection
- `UToast`, `UNotification` - Notifications
- `USlideover` - Sidebar panels
- `UTabs` - Settings tabs
- `USkeleton` - Loading states

## Phase 3: UI Implementation

### 3.1 Login/Register Pages

- [x] Use Nuxt UI form components
- [x] Proper validation feedback
- [x] Loading states
- [x] Error handling with toasts

### 3.2 Main Chat Layout

**Components:**

- [x] `ChatSidebar.vue`: Chat list, new chat button, user profile
- [x] `ChatWindow.vue`: Messages display, input area
- [x] `MessageBubble.vue`: Individual message with avatar, timestamp
- [x] `ChatHeader.vue`: Chat name, participants, actions
- [x] `TypingIndicator.vue`: Shows when others are typing

### 3.3 Contacts/Friends UI

- [x] `FriendList.vue`: List of friends with online status
- [x] `FriendRequestModal.vue`: Incoming requests with accept/reject
- [x] `AddFriendDialog.vue`: Search and send requests

### 3.4 Settings Page

- [x] Profile editing with avatar upload
- [x] Notification preferences
- [x] Privacy settings

## Phase 4: Real-time Integration

### 4.1 WebSocket Client

- [x] Connect on auth
- [x] Reconnect on disconnect
- [x] Subscribe to personal queue and active chat rooms
- [x] Handle incoming messages, typing indicators

### 4.2 Real-time Features

- [x] Live message delivery
- [x] Typing indicators
- [x] Online/offline status (via presence)
- [x] New message notifications

## User Stories

### Authentication

- [x] As a user, I can register with name, email, and password
- [x] As a user, I can login with email and password
- [x] As a user, I remain logged in on page refresh (JWT persistence)
- [x] As a user, I can logout and be redirected to login

### Friends/Contacts

- [x] As a user, I can search for users by name or email
- [x] As a user, I can send a friend request to another user
- [x] As a user, I can accept or reject incoming friend requests
- [x] As a user, I can view my list of friends
- [x] As a user, I can remove a friend

### Chat Rooms

- [x] As a user, I can create a direct message with a friend
- [x] As a user, I can create a group chat with multiple users
- [x] As a user, I can view a list of my chat rooms
- [x] As a user, I can view chat history

### Messaging

- [x] As a user, I can send a text message in a chat
- [x] As a user, I receive messages in real-time
- [x] As a user, I can see when someone is typing
- [x] As a user, I see visual feedback when sending a message

### Profile

- [x] As a user, I can view my profile
- [x] As a user, I can edit my display name
- [x] As a user, I can upload a profile picture
