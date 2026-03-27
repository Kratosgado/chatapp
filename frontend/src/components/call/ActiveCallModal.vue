<script setup lang="ts">
import { ref, computed, watch } from "vue";
import { useCallsStore } from "@/stores/calls";
import { useChatsStore } from "@/stores/chats";

const callsStore = useCallsStore();
const chatsStore = useChatsStore();
const remoteVideo = ref<HTMLVideoElement | null>(null);
const localVideo = ref<HTMLVideoElement | null>(null);
const remoteAudio = ref<HTMLAudioElement | null>(null);

const isActive = computed(
  () =>
    callsStore.callState === "calling" || callsStore.callState === "connected",
);
const isMuted = computed(() => !callsStore.isAudioEnabled);
const isVideoOff = computed(() => !callsStore.isVideoEnabled);
const isVideoCall = computed(() => callsStore.isCallWithVideo);

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

const callerAvatar = computed(() => {
  if (!callsStore.remoteUserId) return "";
  for (const chat of chatsStore.chats) {
    const member = chat.members.find((m) => m.id === callsStore.remoteUserId);
    if (member && member.avatarUrl) return member.avatarUrl;
  }
  return `https://ui-avatars.com/api/?name=${encodeURIComponent(callerName.value)}`;
});

// Watch remote stream and attach to video/audio element
watch(
  () => callsStore.remoteStream,
  (stream) => {
    if (isVideoCall.value && remoteVideo.value && stream) {
      remoteVideo.value.srcObject = stream;
    } else if (!isVideoCall.value && remoteAudio.value && stream) {
      remoteAudio.value.srcObject = stream;
    }
  },
  { immediate: true },
);

watch(remoteVideo, (el) => {
  if (el && callsStore.remoteStream && isVideoCall.value) {
    el.srcObject = callsStore.remoteStream;
  }
});

watch(remoteAudio, (el) => {
  if (el && callsStore.remoteStream && !isVideoCall.value) {
    el.srcObject = callsStore.remoteStream;
  }
});

// Watch local stream and attach to local video element
watch(
  () => callsStore.localStream,
  (stream) => {
    if (isVideoCall.value && localVideo.value && stream) {
      localVideo.value.srcObject = stream;
    }
  },
  { immediate: true },
);

watch(localVideo, (el) => {
  if (el && callsStore.localStream && isVideoCall.value) {
    el.srcObject = callsStore.localStream;
  }
});
</script>

<template>
  <div
    v-if="isActive"
    class="fixed inset-0 z-50 flex flex-col items-center justify-center bg-gray-900/95 backdrop-blur-xl text-white transition-all duration-300 overflow-hidden"
  >
    <!-- Background Gradient Orb (Only for audio calls) -->
    <div v-if="!isVideoCall" class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-primary-500/20 rounded-full blur-3xl pointer-events-none"></div>

    <!-- Video Elements -->
    <div v-if="isVideoCall" class="absolute inset-0 w-full h-full bg-black">
      <!-- Remote Video -->
      <video
        ref="remoteVideo"
        class="w-full h-full object-cover"
        autoplay
        playsinline
      ></video>
      
      <!-- Local Video (PiP) -->
      <div class="absolute top-6 right-6 w-32 md:w-48 aspect-video bg-gray-800 rounded-xl overflow-hidden shadow-2xl border border-white/10 z-20">
        <video
          ref="localVideo"
          class="w-full h-full object-cover transform -scale-x-100"
          autoplay
          playsinline
          muted
        ></video>
      </div>

      <!-- Gradient Overlay for controls -->
      <div class="absolute bottom-0 inset-x-0 h-48 bg-gradient-to-t from-gray-900/90 to-transparent pointer-events-none z-10"></div>
    </div>

    <div class="relative z-20 flex flex-col items-center w-full max-w-md p-8 space-y-12" :class="isVideoCall ? 'mt-auto pb-12' : ''">
      <!-- Caller Info -->
      <div class="text-center space-y-4" :class="isVideoCall ? 'drop-shadow-lg' : ''">
        <div v-if="!isVideoCall" class="relative inline-block">
          <UAvatar
            :src="callerAvatar"
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
          <p class="text-primary-200 font-medium mt-1 text-lg" :class="callsStore.callState === 'calling' ? 'animate-pulse' : ''">
            {{ statusText }}
          </p>
        </div>
      </div>

      <!-- Controls -->
      <div class="flex items-center justify-center gap-6 md:gap-8">
        <UButton
          :color="isMuted ? 'white' : 'gray'"
          :variant="isMuted ? 'solid' : 'ghost'"
          :icon="isMuted ? 'i-heroicons-microphone-slash' : 'i-heroicons-microphone'"
          class="rounded-full w-14 h-14 flex items-center justify-center transition-transform active:scale-95 shadow-lg"
          :class="isMuted ? 'bg-white text-gray-900 hover:bg-gray-100' : 'bg-gray-800/80 backdrop-blur text-white hover:bg-gray-700'"
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
        
        <UButton
          v-if="isVideoCall"
          :color="isVideoOff ? 'white' : 'gray'"
          :variant="isVideoOff ? 'solid' : 'ghost'"
          :icon="isVideoOff ? 'i-heroicons-video-camera-slash' : 'i-heroicons-video-camera'"
          class="rounded-full w-14 h-14 flex items-center justify-center transition-transform active:scale-95 shadow-lg"
          :class="isVideoOff ? 'bg-white text-gray-900 hover:bg-gray-100' : 'bg-gray-800/80 backdrop-blur text-white hover:bg-gray-700'"
          size="xl"
          @click="callsStore.toggleVideo()"
        />
        <UButton
          v-else
          color="neutral"
          variant="ghost"
          icon="i-heroicons-speaker-wave"
          class="rounded-full w-14 h-14 flex items-center justify-center bg-gray-800/80 backdrop-blur text-white hover:bg-gray-700 transition-transform active:scale-95 shadow-lg"
          size="xl"
        />
      </div>
    </div>

    <!-- Hidden audio element for remote stream -->
    <audio v-if="!isVideoCall" ref="remoteAudio" autoplay></audio>
  </div>
</template>
