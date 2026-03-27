<script setup lang="ts">
import { ref, watch, nextTick, onMounted } from "vue";
import { useChatsStore } from "@/stores/chats";
import { useAuthStore } from "@/stores/auth";
import { useCallsStore } from "@/stores/calls";
import { storeToRefs } from "pinia";
import type { ChatRoom, Message } from "@/types";
import TypingIndicator from "./TypingIndicator.vue";

const chatsStore = useChatsStore();
const authStore = useAuthStore();
const callsStore = useCallsStore();
const { currentChat, messages, loading } = storeToRefs(chatsStore);
const { user } = storeToRefs(authStore);

const newMessage = ref("");
const messagesContainer = ref<HTMLElement | null>(null);

async function sendMessage() {
  if (!newMessage.value.trim() || !currentChat.value) return;

  try {
    await chatsStore.send(currentChat.value.id, newMessage.value);
    newMessage.value = "";
    scrollToBottom();
  } catch (error) {
    console.error("Failed to send message:", error);
  }
}

function handleTyping() {
  if (!currentChat.value) return;
  chatsStore.sendTyping(currentChat.value.id, true);

  // Stop typing after delay
  setTimeout(() => {
    chatsStore.sendTyping(currentChat.value!.id, false);
  }, 2000);
}

function startCall(withVideo = false) {
  if (!currentChat.value || !user.value) return;
  // Simple logic for 1-on-1: find the member who is not me
  const otherMember = currentChat.value.members.find(
    (m) => m.id !== user.value?.id,
  );
  if (otherMember) {
    callsStore.startCall(otherMember.id, withVideo);
  } else {
    // Maybe group chat? Just pick first for now or warn
    console.warn("Cannot identify call target");
  }
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    }
  });
}

watch(
  messages,
  () => {
    scrollToBottom();
  },
  { deep: true },
);

watch(currentChat, () => {
  if (currentChat.value) {
    scrollToBottom();
  }
});

function formatTime(dateString: string) {
  const date = new Date(dateString);
  return date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
}

function getAvatar(message: Message) {
  return (
    message.sender.avatarUrl ||
    `https://ui-avatars.com/api/?name=${encodeURIComponent(message.sender.name)}`
  );
}
</script>

<template>
  <div
    v-if="currentChat"
    class="flex-1 flex flex-col h-full bg-white dark:bg-gray-900"
  >
    <!-- Chat Header -->
    <div
      class="h-16 border-b border-muted flex items-center justify-between px-6 bg-white dark:bg-gray-900 shrink-0"
    >
      <div class="flex items-center gap-3">
        <UAvatar
          :src="
            currentChat.avatarUrl ||
            `https://ui-avatars.com/api/?name=${encodeURIComponent(currentChat.name)}`
          "
          :alt="currentChat.name"
          size="md"
        />
        <div>
          <h3 class="font-semibold text-gray-900 dark:text-white">
            {{ currentChat.name }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">
            {{ currentChat.members.length }} members
          </p>
        </div>
      </div>
      <div class="flex items-center gap-2">
        <UButton
          icon="i-heroicons-video-camera"
          color="neutral"
          variant="ghost"
          @click="startCall(true)"
        />
        <UButton
          icon="i-heroicons-phone"
          color="neutral"
          variant="ghost"
          @click="startCall(false)"
        />
        <UButton
          icon="i-heroicons-information-circle"
          color="neutral"
          variant="ghost"
        />
      </div>
    </div>

    <!-- Messages Area -->
    <div ref="messagesContainer" class="flex-1 overflow-y-auto p-6 space-y-4">
      <div v-if="loading" class="flex justify-center p-4">
        <USkeleton class="h-8 w-64" />
      </div>
      <template v-else>
        <div
          v-for="message in messages"
          :key="message.id"
          :class="[
            'flex gap-3',
            message.sender.id === user?.id && 'flex-row-reverse',
          ]"
        >
          <UAvatar
            :src="getAvatar(message)"
            :alt="message.sender.name"
            size="sm"
            class="mt-1"
          />
          <div
            :class="[
              'flex flex-col gap-1 max-w-[70%]',
              message.sender.id === user?.id ? 'items-end' : 'items-start',
            ]"
          >
            <p
              class="text-xs font-semibold text-gray-500 dark:text-gray-400 px-1"
            >
              {{ message.sender.name }}
            </p>
            <div
              :class="[
                'p-3 rounded-lg text-sm break-words',
                message.sender.id === user?.id
                  ? 'bg-primary-500 text-white rounded-tr-none'
                  : 'bg-gray-100 dark:bg-gray-800 text-gray-900 dark:text-white rounded-tl-none',
              ]"
            >
              {{ message.content }}
            </div>
            <p class="text-xs text-gray-400 px-1">
              {{ formatTime(message.sentAt) }}
            </p>
          </div>
        </div>
      </template>
    </div>

    <!-- Typing Indicator -->
    <TypingIndicator />

    <!-- Message Input -->
    <div class="p-4 border-t border-muted shrink-0">
      <div class="flex items-center gap-2">
        <UButton icon="i-heroicons-paper-clip" color="neutral" variant="ghost" />
        <UInput
          v-model="newMessage"
          placeholder="Type a message..."
          @keyup.enter="sendMessage"
          @input="handleTyping"
          class="flex-1"
        >
          <template #trailing>
            <UButton
              v-if="newMessage.trim()"
              icon="i-heroicons-paper-airplane"
              variant="link"
              color="primary"
              :padded="false"
              @click="sendMessage"
              class="pointer-events-auto"
            />
          </template>
        </UInput>
      </div>
    </div>
  </div>

  <UEmpty
    v-else
    icon="lucide:chat-bubble-left-right"
    title="No chats Selected"
    description="Select a chat to start messaging"
  />
</template>
