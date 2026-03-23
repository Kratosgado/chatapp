<template>
  <UModal v-model="isOpen" :prevent-close="true">
    <UCard>
      <template #header>
        <div class="flex items-center justify-between">
          <h3 class="text-base font-semibold leading-6 text-gray-900 dark:text-white">
            Incoming Call
          </h3>
        </div>
      </template>
      <div class="flex flex-col items-center gap-4 py-4">
        <UAvatar
          :alt="callerName"
          size="xl"
        />
        <p class="text-lg font-medium">{{ callerName }}</p>
        <div class="flex gap-4 mt-4">
          <UButton
            color="red"
            variant="solid"
            icon="i-heroicons-phone-x-mark"
            @click="callsStore.declineCall()"
            label="Decline"
          />
          <UButton
            color="green"
            variant="solid"
            icon="i-heroicons-phone"
            @click="callsStore.acceptCall()"
            label="Accept"
          />
        </div>
      </div>
    </UCard>
  </UModal>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useCallsStore } from '@/stores/calls';
import { useChatsStore } from '@/stores/chats';

const callsStore = useCallsStore();
const chatsStore = useChatsStore();

const isOpen = computed({
  get: () => callsStore.callState === 'incoming',
  set: (val) => {
    // Cannot close directly without action
  }
});

const callerName = computed(() => {
  if (!callsStore.remoteUserId) return 'Unknown';
  // Try to find user in chats
  for (const chat of chatsStore.chats) {
    const member = chat.members.find(m => m.id === callsStore.remoteUserId);
    if (member) return member.name;
  }
  return callsStore.remoteUserId;
});
</script>
