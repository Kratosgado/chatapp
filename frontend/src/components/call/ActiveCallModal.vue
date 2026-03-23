<script setup lang="ts">
import { ref, computed, watch } from "vue";
import { useCallsStore } from "@/stores/calls";
import { useChatsStore } from "@/stores/chats";

const callsStore = useCallsStore();
const chatsStore = useChatsStore();
const remoteAudio = ref<HTMLAudioElement | null>(null);

const isActive = computed(
  () =>
    callsStore.callState === "calling" || callsStore.callState === "connected",
);
const isMuted = computed(() => !callsStore.isAudioEnabled);

const statusText = computed(() => {
  if (callsStore.callState === "calling") return "Calling...";
  if (callsStore.callState === "connected") return "Connected";
  return "";
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

// Watch remote stream and attach to audio element
watch(
  () => callsStore.remoteStream,
  (stream) => {
    if (remoteAudio.value && stream) {
      remoteAudio.value.srcObject = stream;
    }
  },
  { immediate: true },
);

// Also check when component mounts if stream already exists (though v-if handles mount)
watch(remoteAudio, (el) => {
  if (el && callsStore.remoteStream) {
    el.srcObject = callsStore.remoteStream;
  }
});
</script>

<template>
  <div
    v-if="isActive"
    class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm"
  >
    <div
      class="bg-white dark:bg-gray-900 rounded-lg p-6 shadow-xl w-80 text-center"
    >
      <div class="mb-4">
        <UAvatar :alt="callerName" size="2xl" class="mx-auto" />
        <h3 class="mt-2 text-xl font-semibold">{{ callerName }}</h3>
        <p class="text-gray-500">{{ statusText }}</p>
      </div>

      <div class="flex justify-center gap-4 mt-6">
        <UButton
          :color="isMuted ? 'red' : 'gray'"
          variant="solid"
          :icon="
            isMuted ? 'i-heroicons-microphone-slash' : 'i-heroicons-microphone'
          "
          @click="callsStore.toggleAudio()"
        />
        <UButton
          color="red"
          variant="solid"
          icon="i-heroicons-phone-x-mark"
          @click="callsStore.endCall()"
        />
      </div>

      <!-- Hidden audio element for remote stream -->
      <audio ref="remoteAudio" autoplay></audio>
    </div>
  </div>
</template>
