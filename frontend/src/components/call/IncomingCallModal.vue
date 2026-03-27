<script setup lang="ts">
import { computed } from "vue";
import { useCallsStore } from "@/stores/calls";
import { useChatsStore } from "@/stores/chats";

const callsStore = useCallsStore();
const chatsStore = useChatsStore();

const isOpen = computed({
  get: () => callsStore.callState === "incoming",
  set: (val) => {
    // Cannot close directly without action
  },
});

const callerName = computed(() => {
  if (!callsStore.remoteUserId) return "Unknown";
  // Try to find user in chats
  for (const chat of chatsStore.chats) {
    const member = chat.members.find((m) => m.id === callsStore.remoteUserId);
    if (member) return member.name;
  }
  return callsStore.remoteUserId;
});
</script>
<template>
  <UModal :open="isOpen" :prevent-close="true">
    <template #content>
      <div
        class="relative overflow-hidden bg-white dark:bg-gray-900 rounded-xl shadow-2xl border border-gray-100 dark:border-gray-800"
      >
        <!-- Background Effect -->
        <div
          class="absolute inset-0 opacity-10 bg-gradient-to-br from-primary-500 to-indigo-600 dark:opacity-20 pointer-events-none"
        ></div>

        <div class="relative z-10 flex flex-col items-center p-8 space-y-6">
          <!-- Status Indicator -->
          <div
            class="flex items-center gap-2 px-3 py-1 rounded-full bg-primary-100 dark:bg-primary-900/30 text-primary-600 dark:text-primary-300 text-xs font-semibold uppercase tracking-wider"
          >
            <span class="relative flex h-2 w-2">
              <span
                class="animate-ping absolute inline-flex h-full w-full rounded-full bg-primary-400 opacity-75"
              ></span>
              <span
                class="relative inline-flex rounded-full h-2 w-2 bg-primary-500"
              ></span>
            </span>
            Incoming Call
          </div>

          <!-- Caller Info -->
          <div class="flex flex-col items-center text-center space-y-3">
            <div class="relative">
              <UAvatar
                :alt="callerName"
                size="3xl"
                class="mx-auto ring-4 ring-white dark:ring-gray-800 shadow-lg"
              />
              <div
                class="absolute -bottom-1 -right-1 bg-green-500 rounded-full p-1 border-2 border-white dark:border-gray-900"
              >
                <UIcon name="i-heroicons-phone" class="w-4 h-4 text-white" />
              </div>
            </div>

            <div>
              <h3 class="text-2xl font-bold text-gray-900 dark:text-white">
                {{ callerName }}
              </h3>
              <p class="text-sm text-gray-500 dark:text-gray-400">
                is calling you...
              </p>
            </div>
          </div>

          <!-- Actions -->
          <div class="flex items-center justify-center gap-6 w-full mt-4">
            <div class="flex flex-col items-center gap-2">
              <UButton
                color="error"
                variant="solid"
                icon="i-heroicons-phone-x-mark"
                class="w-16 h-16 rounded-full flex items-center justify-center shadow-lg hover:scale-105 active:scale-95 transition-all duration-200"
                size="xl"
                :ui="{ rounded: 'rounded-full' }"
                @click="callsStore.declineCall()"
              />
              <span class="text-xs font-medium text-gray-500">Decline</span>
            </div>

            <div class="flex flex-col items-center gap-2">
              <UButton
                color="success"
                variant="solid"
                icon="i-heroicons-phone"
                class="w-16 h-16 rounded-full flex items-center justify-center shadow-lg hover:scale-105 active:scale-95 transition-all duration-200 animate-pulse"
                size="xl"
                :ui="{ rounded: 'rounded-full' }"
                @click="callsStore.acceptCall()"
              />
              <span class="text-xs font-medium text-gray-500">Accept</span>
            </div>
          </div>
        </div>
      </div>
    </template>
  </UModal>
</template>
