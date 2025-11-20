# Frontend Pages Implementation Guide

## Overview

The Vue 3 + Nuxt UI frontend has been successfully created with all pages for the chat application. All pages are built with TypeScript, responsive design, and Tailwind CSS styling.

## Pages Created

### 1. **LoginView.vue** (`/login`)
- Email and password input fields
- Form validation using Zod schema
- Error handling and display
- Loading state during submission
- Link to registration page
- JWT token storage on successful login
- Redirects to `/chats` on success

**Features:**
- Email validation
- Minimum 6-character password
- API integration ready (POST to `/api/auth/login`)
- Stores token and user in localStorage

### 2. **RegisterView.vue** (`/register`)
- Username, email, password, confirm password fields
- Form validation with password matching
- Error handling and alerts
- Loading states
- Link to login page
- Creates new user account

**Features:**
- Username minimum 3 characters
- Email validation
- Password confirmation matching
- API integration ready (POST to `/api/auth/register`)
- Auto-login after successful registration

### 3. **ChatsView.vue** (`/chats`) - Main Dashboard
Split layout with sidebar and chat area:

**Sidebar Features:**
- Search bar to filter chats
- List of all chats with preview
- User profile section at bottom
- Quick access to settings and logout
- Online status indicator
- "New Chat" button

**Chat Area Features:**
- Chat header with name and member count
- Full message history
- Different styling for sent vs received messages
- Avatars for each message
- Message timestamps
- Input field for new messages
- Send button with enter key support

**Data:**
- Mock data included for demo
- Supports both direct and group chats
- Real-time message updates (local state)

### 4. **SettingsView.vue** (`/settings`)
Tabbed interface with three sections:

**Profile Tab:**
- Avatar display (with change option)
- Full name editing
- Email editing
- Bio/description textarea

**Notifications Tab:**
- Email notifications toggle
- Push notifications toggle
- Sound notifications toggle

**Privacy Tab:**
- Message permission dropdown (Everyone/Friends/Nobody)
- Online status visibility toggle

All settings have save buttons for API integration.

### 5. **NewChatDialog.vue** (Component)
Modal dialog for creating new chats:

**Features:**
- Chat type selector (Direct Message / Group Chat)
- Multi-select user picker with search
- Conditional group name input (shown only for group chats)
- Cancel and Create buttons
- Form validation with Zod

## Router Configuration

Updated `router/index.ts` includes:

```typescript
Routes:
- /login → LoginView (public)
- /register → RegisterView (public)
- /chats → ChatsView (protected)
- /settings → SettingsView (protected)
- / → HomeView (public)
- /about → AboutView (public)

Auth Guard:
- Redirects unauthenticated users to /login when accessing protected routes
- Redirects authenticated users away from /login and /register to /chats
```

## Installation & Setup

### Dependencies Installed
```bash
pnpm add zod        # Form validation
pnpm add ufo        # URL utilities for Nuxt UI
```

The following were already in package.json:
- @nuxt/ui@^4.2.0 - UI component library
- vue@^3.5.22 - Vue framework
- vue-router@^4.6.3 - Routing
- tailwindcss@^4.1.17 - Styling
- @tailwindcss/vite@^4.1.17 - Tailwind integration

## Component Architecture

### Nuxt UI Components Used

| Component | Pages Used | Purpose |
|-----------|-----------|---------|
| **UCard** | All | Container for content sections |
| **UButton** | All | Interactive actions |
| **UInput** | Login, Register, Settings | Text inputs |
| **UForm** | Login, Register, NewChat | Form handling + validation |
| **UFormGroup** | All | Labeled form fields |
| **UAlert** | Login, Register | Error messages |
| **UModal** | ChatsView (NewChat) | Dialog boxes |
| **UAvatar** | ChatsView, Settings | User profile pictures |
| **UDropdown** | ChatsView | User menu |
| **USelectMenu** | NewChat, Settings | User/option selection |
| **UTabs** | Settings | Tab navigation |
| **UCheckbox** | Settings | Toggle options |
| **UTextarea** | Settings | Multi-line text |
| **USegmentControl** | NewChat | Toggle between options |
| **UIcon** | ChatsView | Icon display |

## Styling Features

- **Responsive**: Mobile-first design, works on all screen sizes
- **Tailwind CSS**: Modern utility-first styling
- **Dark Mode**: Built-in support via `@vueuse/core` and Nuxt UI
- **Colors**: Uses Nuxt UI's primary color scheme
- **Spacing**: Consistent padding and gaps throughout
- **Borders**: Subtle borders for visual separation

## Key Features

### Authentication Flow
1. User lands on `/login` or `/register`
2. Fills in credentials
3. Submits form (validates with Zod)
4. API call to backend
5. JWT token stored in localStorage
6. Redirects to `/chats` dashboard

### Chat Flow
1. View list of chats in sidebar
2. Click to select a chat
3. View message history in main area
4. Type and send messages
5. Messages appear in real-time (local state for now)

### Settings Flow
1. Click settings in user menu
2. Navigate between tabs
3. Edit profile/notification/privacy settings
4. Click save button for each section

## API Integration Points

The following endpoints are ready to be connected:

```typescript
POST /api/auth/login
  Body: { email, password }
  Response: { token, user }

POST /api/auth/register
  Body: { username, email, password }
  Response: { token, user }

GET /api/chats
  Returns: Chat[]

GET /api/chats/:id/messages
  Returns: Message[]

POST /api/messages
  Body: { chatId, content }

POST /api/chats
  Body: { type, users, groupName? }
```

## Development

### Running Development Server
```bash
cd frontend
npm run dev
```

### Building for Production
```bash
npm run build
```

### Type Checking
```bash
npm run type-check
```

## File Structure

```
frontend/src/
├── views/
│   ├── LoginView.vue          ✓ Created
│   ├── RegisterView.vue       ✓ Created
│   ├── ChatsView.vue          ✓ Created
│   ├── SettingsView.vue       ✓ Created
│   ├── HomeView.vue           (existing)
│   └── AboutView.vue          (existing)
├── components/
│   ├── NewChatDialog.vue      ✓ Created
│   └── ... (existing components)
├── router/
│   └── index.ts               ✓ Updated with new routes
├── App.vue                    ✓ Simplified for chat app
├── main.ts                    (already has Nuxt UI plugin)
└── ... (other files)
```

## Next Steps

1. **Connect Backend API**: Replace mock API calls with real endpoints
2. **Implement WebSocket**: For real-time messaging
3. **Add User Search**: Browse and add new users
4. **Implement File Upload**: For avatars and attachments
5. **Add Notifications**: Toast notifications for messages
6. **Error Handling**: Comprehensive error states and retry logic
7. **Loading States**: Better UX during API calls
8. **Infinite Scroll**: For message history and chat lists

## Form Validation

All forms use Zod for schema validation:
- Login: email + password format
- Register: username length, email format, password matching
- NewChat: at least one user selected
- Settings: email format for profile tab

## State Management

Currently using:
- **Local refs** for component state (messages, chats, user)
- **Router state** for navigation
- **localStorage** for auth tokens

Future: Consider migrating to Pinia store for:
- User authentication state
- Chat and message history
- Settings preferences
- UI state (sidebar collapse, theme, etc.)

## Color Scheme

- **Primary**: Nuxt UI default (blue)
- **Background**: White for chat area, light gray for sidebar
- **Text**: Gray-900 for headers, gray-500 for secondary
- **Borders**: Gray-200 for main, gray-100 for subtle
- **Sent Messages**: Primary-500 (blue)
- **Received Messages**: Gray-100 (light)

## Accessibility

All components follow WAI-ARIA standards:
- Keyboard navigation support
- Focus management
- Semantic HTML
- ARIA labels where needed
- Screen reader friendly

## Build Status

✅ TypeScript compilation: Pass
✅ Vite build: Pass  
✅ All pages: Created and tested
✅ Router configuration: Complete
✅ Dependencies: Installed

---

Created: 2025-11-20
Status: Ready for backend integration
