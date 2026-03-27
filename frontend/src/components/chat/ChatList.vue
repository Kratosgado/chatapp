<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useChatsStore } from "@/stores/chats";
import { useAuthStore } from "@/stores/auth";
import { storeToRefs } from "pinia";
import type { ChatRoom } from "@/types";
import NewChatDialog from "@/components/NewChatDialog.vue";

const chatsStore = useChatsStore();
const authStore = useAuthStore();
const { chats, currentChat } = storeToRefs(chatsStore);
const { user } = storeToRefs(authStore);

const searchQuery = ref("");

const filteredChats = computed(() => {
  if (!searchQuery.value) return chats.value;
  return chats.value.filter((chat) =>
    chat.name.toLowerCase().includes(searchQuery.value.toLowerCase()),
  );
});

function selectChat(chat: ChatRoom) {
  chatsStore.setCurrentChat(chat);
}

onMounted(() => {
  chatsStore.fetchChats();
});

function getAvatar(chat: ChatRoom) {
  return (
    chat.avatarUrl ||
    `https://ui-avatars.com/api/?name=${encodeURIComponent(chat.name)}&background=random`
  );
}

function getLastMessage(chat: ChatRoom) {
  if (chat.lastMessage) {
    return chat.lastMessage.content;
  }
  return "No messages yet";
}

function formatTime(dateString: string) {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
}
</script>

<template>
  <div class="flex flex-col h-full">
    <!-- Header -->
    <div class="p-4 border-b border-muted">
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-xl font-bold text-gray-900 dark:text-white">Chats</h2>

        <NewChatDialog />
      </div>
      <UInput
        v-model="searchQuery"
        placeholder="Search chats..."
        icon="i-heroicons-magnifying-glass"
      />
    </div>

    <!-- Chats List -->
    <div class="flex-1 overflow-y-auto">
      <div
        v-if="chatsStore.loading && chats.length === 0"
        class="p-4 space-y-4"
      >
        <USkeleton class="h-12 w-full" v-for="i in 5" :key="i" />
      </div>
      <div
        v-else
        v-for="chat in filteredChats"
        :key="chat.id"
        @click="selectChat(chat)"
        :class="[
          'p-4 border-b border-gray-100 dark:border-gray-800 cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-800 transition',
          currentChat?.id === chat.id &&
            'bg-primary-50 dark:bg-primary-900/10 border-l-4 border-l-primary-500',
        ]"
      >
        <div class="flex items-center gap-3">
          <UAvatar :src="getAvatar(chat)" :alt="chat.name" />
          <div class="flex-1 min-w-0">
            <h3 class="font-semibold text-gray-900 dark:text-white truncate">
              {{ chat.name }}
            </h3>
            <p class="text-sm text-muted truncate">
              <span v-if="chat.lastMessage?.sender.id === user?.id">You: </span>
              {{ getLastMessage(chat) }}
              <span class="text-xs text-gray-400" v-if="chat.lastMessage"
                >. {{ formatTime(chat.lastMessage.sentAt) }}</span
              >
            </p>
          </div>
          <div class="flex flex-col items-end">
            <UDropdownMenu
              v-if="chat.members.length > 1"
              placement="bottom-end"
              offset="0, 4"
              :items="[
                {
                  label: 'Delete Chat',
                  onSelect() {
                    chatsStore.deleteChat(chat.id);
                  },
                },
              ]"
            >
              <UButton icon="lucide:ellipsis-vertical" variant="ghost" />
            </UDropdownMenu>
          </div>
        </div>
      </div>
      <div
        v-if="filteredChats.length === 0 && !chatsStore.loading"
        class="p-8 text-center text-gray-500"
      >
        No chats found
      </div>
    </div>
  </div>
</template>
