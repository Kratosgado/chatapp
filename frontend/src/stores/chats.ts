import { defineStore } from "pinia";
import { ref, watch } from "vue";
import {
  getChats,
  createChat,
  getMessages,
  sendMessage,
} from "@/services/chats";
import type { ChatRoom, Message } from "@/types";
import {
  connect,
  subscribe,
  disconnect,
  sendMessage as wsSendMessage,
} from "@/services/websocket";
import { useAuthStore } from "./auth";
import { storeToRefs } from "pinia";

export const useChatsStore = defineStore("chats", () => {
  const authStore = useAuthStore();
  const { user } = storeToRefs(authStore);

  const chats = ref<ChatRoom[]>([]);
  const currentChat = ref<ChatRoom | null>(null);
  const messages = ref<Message[]>([]);
  const loading = ref(false);
  const typingUsers = ref<Record<string, boolean>>({});

  // WebSocket subscriptions
  let chatSubscription: any = null;
  let typingSubscription: any = null;

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
    // Send via REST for reliability and immediate feedback (optimistic UI or confirmation)
    // Alternatively, send via WS only.
    // The plan says: "Send message (REST fallback)".
    // If we use REST, we get the message back immediately.
    // However, if we are subscribed to the topic, we will receive the message via WS too.
    // To avoid duplication, we can check ID or just rely on Vue's reactivity/deduplication if implemented.
    // Simplest approach: Use REST to send, and ignore own messages via WS if they have same ID or just append if not present.
    // Or simpler: Send via REST, get response, add to messages.
    // WS will broadcast to everyone.
    // If I receive my own message via WS, I should ignore it if I already added it via REST.

    // Let's try sending via WS primarily for real-time feel if connected.
    // But `sendMessage` service uses REST.
    // Let's stick to REST for sending and use WS for receiving others' messages.

    // const message = await sendMessage(chatId, content);
    // messages.value.push(message);
    wsSendMessage(`/app/chat.sendMessage/${chatId}`, { content });

    // Also notify typing stopped
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
