<script setup lang="ts">
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import { storeToRefs } from "pinia";
import ChatList from "./chat/ChatList.vue";
import FriendList from "./friends/FriendList.vue";
import type { DropdownMenuItem, TabsItem } from "@nuxt/ui";

const authStore = useAuthStore();
const { user } = storeToRefs(authStore);

const items: TabsItem[] = [
  {
    label: "Chats",
    icon: "i-heroicons-chat-alt-2",
    slot: "chats",
  },
  {
    label: "Friends",
    icon: "i-heroicons-users",
    slot: "friends",
  },
];
function handleLogout() {
  authStore.logout();
}
</script>

<template>
  <div
    class="w-80 border-r border-muted flex flex-col bg-white dark:bg-gray-900 h-full"
  >
    <!-- Tabs -->
    <UTabs :items="items" class="flex-1 overflow-y-auto">
      <template #chats>
        <ChatList />
      </template>
      <template #friends>
        <FriendList />
      </template>
    </UTabs>

    <!-- User Profile Footer -->
    <div class="p-4 border-t border-muted">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-3" v-if="user">
          <UAvatar
            :src="
              user.avatarUrl ||
              `https://ui-avatars.com/api/?name=${encodeURIComponent(user.name)}`
            "
            :alt="user.name"
          />
          <div>
            <p class="font-semibold text-gray-900 dark:text-white">
              {{ user.name }}
            </p>
            <p class="text-xs text-gray-500 dark:text-gray-400">Online</p>
          </div>
        </div>
        <UDropdownMenu
          :items="[
            [
              {
                label: 'Settings',
                icon: 'i-heroicons-cog-6-tooth',
                to: '/settings',
              },
            ],
            [
              {
                label: 'Logout',
                icon: 'i-heroicons-arrow-right-on-rectangle',
                onSelect: handleLogout,
              },
            ],
          ]"
        >
          <UButton
            icon="i-heroicons-ellipsis-vertical"
            color="neutral"
            variant="ghost"
          />
        </UDropdownMenu>
      </div>
    </div>
  </div>
</template>
