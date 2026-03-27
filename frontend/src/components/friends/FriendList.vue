<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useFriendsStore } from "@/stores/friends";
import { storeToRefs } from "pinia";
import AddFriendDialog from "@/components/ui/AddFriendDialog.vue";

const friendsStore = useFriendsStore();
const { friends, requests } = storeToRefs(friendsStore);

onMounted(() => {
  friendsStore.fetchFriends();
  friendsStore.fetchRequests();
});

function getAvatar(user: any) {
  return (
    user.avatarUrl ||
    `https://ui-avatars.com/api/?name=${encodeURIComponent(user.name)}&background=random`
  );
}

function handleAccept(requestId: string) {
  friendsStore.acceptRequest(requestId);
}

function handleReject(requestId: string) {
  friendsStore.rejectRequest(requestId);
}
</script>

<template>
  <div class="flex flex-col h-full">
    <!-- Header -->
    <div
      class="p-4 border-b border-muted flex items-center justify-between"
    >
      <h2 class="text-xl font-bold text-gray-900 dark:text-white">Friends</h2>

      <AddFriendDialog />
    </div>

    <!-- Requests -->
    <div
      v-if="requests.length > 0"
      class="p-4 border-b border-muted"
    >
      <h3 class="text-sm font-semibold text-gray-500 mb-2">Requests</h3>
      <div class="space-y-2">
        <div
          v-for="req in requests"
          :key="req.id"
          class="flex items-center justify-between"
        >
          <div class="flex items-center gap-2">
            <UAvatar
              :src="getAvatar(req.sender)"
              :alt="req.sender.name"
              size="xs"
            />
            <span class="text-sm font-medium">{{ req.sender.name }}</span>
          </div>
          <div class="flex gap-1">
            <UButton
              icon="i-heroicons-check"
              size="xs"
              color="success"
              variant="ghost"
              @click="handleAccept(req.id)"
            />
            <UButton
              icon="i-heroicons-x-mark"
              size="xs"
              color="error"
              variant="ghost"
              @click="handleReject(req.id)"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- Friends List -->
    <div class="flex-1 overflow-y-auto p-4">
      <div
        v-if="friends.length === 0"
        class="text-center text-gray-500 text-sm mt-4"
      >
        No friends yet. Add someone!
      </div>
      <div v-else class="space-y-3">
        <div
          v-for="friend in friends"
          :key="friend.id"
          class="flex items-center gap-3"
        >
          <UAvatar :src="getAvatar(friend)" :alt="friend.name" />
          <div>
            <p class="font-medium text-gray-900 dark:text-white">
              {{ friend.name }}
            </p>
            <p class="text-xs text-gray-500">Online</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
