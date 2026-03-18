<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'
import ChatList from './chat/ChatList.vue'
import FriendList from './friends/FriendList.vue'

const router = useRouter()
const authStore = useAuthStore()
const { user } = storeToRefs(authStore)

const activeTab = ref<'chats' | 'friends'>('chats')

function handleLogout() {
  authStore.logout()
}
</script>

<template>
  <div class="w-80 border-r border-gray-200 dark:border-gray-800 flex flex-col bg-white dark:bg-gray-900 h-full">
    <!-- Tabs -->
    <div class="grid grid-cols-2 border-b border-gray-200 dark:border-gray-800">
      <button
        @click="activeTab = 'chats'"
        :class="[
          'py-3 text-sm font-medium transition-colors',
          activeTab === 'chats'
            ? 'text-primary-600 dark:text-primary-400 border-b-2 border-primary-600 dark:border-primary-400'
            : 'text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200'
        ]"
      >
        Chats
      </button>
      <button
        @click="activeTab = 'friends'"
        :class="[
          'py-3 text-sm font-medium transition-colors',
          activeTab === 'friends'
            ? 'text-primary-600 dark:text-primary-400 border-b-2 border-primary-600 dark:border-primary-400'
            : 'text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200'
        ]"
      >
        Friends
      </button>
    </div>

    <!-- Content -->
    <div class="flex-1 overflow-hidden">
      <ChatList v-if="activeTab === 'chats'" />
      <FriendList v-else-if="activeTab === 'friends'" />
    </div>

    <!-- User Profile Footer -->
    <div class="p-4 border-t border-gray-200 dark:border-gray-800">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-3" v-if="user">
          <UAvatar :src="user.avatarUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(user.name)}`" :alt="user.name" />
          <div>
            <p class="font-semibold text-gray-900 dark:text-white">{{ user.name }}</p>
            <p class="text-xs text-gray-500 dark:text-gray-400">Online</p>
          </div>
        </div>
        <UDropdown
          :items="[
            [
              { label: 'Settings', icon: 'i-heroicons-cog-6-tooth', click: () => router.push('/settings') },
            ],
            [{ label: 'Logout', icon: 'i-heroicons-arrow-right-on-rectangle', click: handleLogout }],
          ]"
        >
          <UButton icon="i-heroicons-ellipsis-vertical" color="gray" variant="ghost" />
        </UDropdown>
      </div>
    </div>
  </div>
</template>
