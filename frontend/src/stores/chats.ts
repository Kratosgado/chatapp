import { defineStore } from "pinia";
import { ref, watch } from "vue";
import { getChats, createChat, getMessages } from "@/services/chats";
import type { ChatRoom, Message } from "@/types";
import {
  connect,
  subscribe,
  disconnect,
  sendMessage as wsSendMessage,
} from "@/services/websocket";
import { useAuthStore } from "./auth";
import { storeToRefs } from "pinia";
import type { StompSubscription } from "@stomp/stompjs";
import api from "@/services/api";

export const useChatsStore = defineStore("chats", () => {
  const authStore = useAuthStore();
  const { user } = storeToRefs(authStore);

  const chats = ref<ChatRoom[]>([]);
  const currentChat = ref<ChatRoom | null>(null);
  const messages = ref<Message[]>([]);
  const loading = ref(false);
  const typingUsers = ref<Record<string, boolean>>({});

  // WebSocket subscriptions
  let chatSubscription: StompSubscription | null = null;
  let typingSubscription: StompSubscription | null = null;

  // Initialize WebSocket connection when user is logged in
  watch(
    user,
    (newUser) => {
      if (newUser) {
        connect();
      } else {
        disconnect();
      }
    },
    { immediate: true },
  );

  const fetchChats = async () => {
    loading.value = true;
    try {
      chats.value = await getChats();
    } finally {
      loading.value = false;
    }
  };

  const deleteChat = async (chatId: string) => {
    const res = await api.delete(`/chats/${chatId}`);
    if (res.status === 200) {
      chats.value = chats.value.filter((chat) => chat.id !== chatId);
    }
  };

  const create = async (participantIds: string[], name?: string) => {
    const newChat = await createChat(participantIds, name);
    chats.value.push(newChat);
    return newChat;
  };

  const loadMessages = async (chatId: string) => {
    loading.value = true;
    try {
      const msgs = await getMessages(chatId);
      // Backend returns DESC (newest first). Reverse to show oldest first (chronological)
      messages.value = msgs.reverse();
    } finally {
      loading.value = false;
    }
  };

  const send = async (chatId: string, content: string) => {
    wsSendMessage(`/app/chat.sendMessage/${chatId}`, { content });
    sendTyping(chatId, false);
  };

  const sendTyping = (chatId: string, isTyping: boolean) => {
    wsSendMessage(`/app/chat.typing/${chatId}`, { isTyping });
  };

  const setCurrentChat = (chat: ChatRoom) => {
    if (currentChat.value?.id === chat.id) return;

    // Unsubscribe from previous chat
    if (chatSubscription) {
      chatSubscription.unsubscribe();
      chatSubscription = null;
    }
    if (typingSubscription) {
      typingSubscription.unsubscribe();
      typingSubscription = null;
    }

    currentChat.value = chat;
    messages.value = [];
    typingUsers.value = {};
    loadMessages(chat.id);

    // Subscribe to new chat
    // We need to wait for connection if not active, but `subscribe` handles check.
    // Better to retry or queue.
    // For now assume connected if logged in.

    // Subscribe to messages
    chatSubscription = subscribe(
      `/topic/chats/${chat.id}`,
      (message: Message) => {
        // Only add if not already present (to avoid dupe from REST response)
        if (!messages.value.find((m) => m.id === message.id)) {
          messages.value.push(message);
          // Also update last message in chat list
          const chatInList = chats.value.find((c) => c.id === chat.id);
          if (chatInList) {
            chatInList.lastMessage = message;
          }
        }
      },
    );

    // Subscribe to typing
    typingSubscription = subscribe(
      `/topic/chats/${chat.id}/typing`,
      (payload: { userId: string; isTyping: boolean }) => {
        if (payload.userId !== user.value?.id) {
          if (payload.isTyping) {
            typingUsers.value[payload.userId] = true;
          } else {
            delete typingUsers.value[payload.userId];
          }
        }
      },
    );
  };

  return {
    chats,
    currentChat,
    messages,
    deleteChat,
    loading,
    typingUsers,
    fetchChats,
    create,
    loadMessages,
    send,
    setCurrentChat,
    sendTyping,
  };
});
