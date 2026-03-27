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
    class="fixed inset-0 z-50 flex flex-col items-center justify-center bg-gray-900/95 backdrop-blur-xl text-white transition-all duration-300"
  >
    <!-- Background Gradient Orb -->
    <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-primary-500/20 rounded-full blur-3xl pointer-events-none"></div>

    <div class="relative z-10 flex flex-col items-center w-full max-w-md p-8 space-y-12">
      <!-- Caller Info -->
      <div class="text-center space-y-4">
        <div class="relative inline-block">
          <UAvatar
            :alt="callerName"
            size="3xl"
            class="mx-auto ring-4 ring-white/10 shadow-2xl"
            :ui="{ root: 'h-32 w-32 text-3xl' }"
          />
          <!-- Pulse Animation for Calling State -->
          <div
            v-if="callsStore.callState === 'calling'"
            class="absolute inset-0 rounded-full ring-4 ring-primary-500 animate-ping opacity-75"
          ></div>
        </div>
        
        <div>
          <h3 class="text-3xl font-bold tracking-tight">{{ callerName }}</h3>
          <p class="text-primary-200 font-medium mt-1 text-lg animate-pulse">
            {{ statusText }}
          </p>
        </div>
      </div>

      <!-- Controls -->
      <div class="flex items-center justify-center gap-8">
        <UButton
          :color="isMuted ? 'white' : 'gray'"
          :variant="isMuted ? 'solid' : 'ghost'"
          :icon="isMuted ? 'i-heroicons-microphone-slash' : 'i-heroicons-microphone'"
          class="rounded-full w-14 h-14 flex items-center justify-center transition-transform active:scale-95"
          :class="isMuted ? 'bg-white text-gray-900 hover:bg-gray-100' : 'bg-white/10 text-white hover:bg-white/20'"
          size="xl"
          @click="callsStore.toggleAudio()"
        />
        
        <UButton
          color="error"
          variant="solid"
          icon="i-heroicons-phone-x-mark"
          class="rounded-full w-20 h-20 flex items-center justify-center shadow-lg shadow-red-500/30 transition-transform hover:scale-105 active:scale-95"
          size="xl"
          :ui="{ rounded: 'rounded-full' }"
          @click="callsStore.endCall()"
        />
        
        <!-- Placeholder for future Video/Speaker toggle -->
        <UButton
          color="neutral"
          variant="ghost"
          icon="i-heroicons-speaker-wave"
          class="rounded-full w-14 h-14 flex items-center justify-center bg-white/10 text-white hover:bg-white/20 transition-transform active:scale-95"
          size="xl"
        />
      </div>
    </div>

    <!-- Hidden audio element for remote stream -->
    <audio ref="remoteAudio" autoplay></audio>
  </div>
</template>
