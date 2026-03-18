export interface User {
  id: string;
  name: string;
  email: string;
  avatarUrl?: string;
}

export interface LoginResponse {
  token: string;
  user: User;
}

export interface FriendRequest {
  id: string;
  sender: User;
  createdAt: string;
}

export interface ChatRoom {
  id: string;
  name: string;
  isGroup: boolean;
  avatarUrl?: string;
  lastMessage?: Message;
  members: User[];
  createdAt: string;
}

export interface Message {
  id: string;
  content: string;
  sender: User;
  sentAt: string;
}
