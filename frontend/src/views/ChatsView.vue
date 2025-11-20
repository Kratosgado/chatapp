<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

interface User {
  id: string
  name: string
  avatar: string
}

interface Message {
  id: string
  content: string
  sender: User
  timestamp: string
}

interface Chat {
  id: string
  name: string
  avatar: string
  lastMessage: string
  timestamp: string
  memberCount: number
  messages: Message[]
}

const router = useRouter()
const searchQuery = ref('')
const showNewChatDialog = ref(false)
const selectedChat = ref<Chat | null>(null)
const newMessage = ref('')

const currentUser = ref<User>({
  id: '1',
  name: 'John Doe',
  avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=John',
})

const chats = ref<Chat[]>([
  {
    id: '1',
    name: 'Alice Johnson',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Alice',
    lastMessage: 'Hey, how are you?',
    timestamp: '2:30 PM',
    memberCount: 2,
    messages: [
      {
        id: '1',
        content: 'Hey, how are you?',
        sender: { id: '2', name: 'Alice', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Alice' },
        timestamp: '2:30 PM',
      },
      {
        id: '2',
        content: "I'm doing great! How about you?",
        sender: currentUser.value,
        timestamp: '2:31 PM',
      },
    ],
  },
  {
    id: '2',
    name: 'Development Team',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Team',
    lastMessage: 'Project deadline is next week',
    timestamp: '1:15 PM',
    memberCount: 5,
    messages: [
      {
        id: '1',
        content: 'Project deadline is next week',
        sender: { id: '3', name: 'Bob', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Bob' },
        timestamp: '1:15 PM',
      },
    ],
  },
])

const filteredChats = computed(() => {
  if (!searchQuery.value) return chats.value
  return chats.value.filter((chat) =>
    chat.name.toLowerCase().includes(searchQuery.value.toLowerCase()),
  )
})

function selectChat(chat: Chat) {
  selectedChat.value = chat
}

function sendMessage() {
  if (!newMessage.value.trim() || !selectedChat.value) return

  const message: Message = {
    id: Date.now().toString(),
    content: newMessage.value,
    sender: currentUser.value,
    timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
  }

  selectedChat.value.messages.push(message)
  selectedChat.value.lastMessage = newMessage.value
  newMessage.value = ''
}

async function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  router.push('/login')
}

onMounted(() => {
  if (chats.value.length > 0) {
    selectChat(chats.value[0]!)
  }
})
</script>

<template>
  <div class="flex h-screen bg-white">
    <!-- Sidebar -->
    <div class="w-80 border-r border-gray-200 flex flex-col">
      <!-- Header -->
      <div class="p-4 border-b border-gray-200">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-xl font-bold text-gray-900">Chats</h2>
          <UButton
            icon="i-lucide-plus"
            color="gray"
            variant="ghost"
            size="sm"
            @click="showNewChatDialog = true"
          />
        </div>
        <UInput
          v-model="searchQuery"
          placeholder="Search chats..."
          icon="i-lucide-search"
        />
      </div>

      <!-- Chats List -->
      <div class="flex-1 overflow-y-auto">
        <div
          v-for="chat in filteredChats"
          :key="chat.id"
          @click="selectChat(chat)"
          :class="[
            'p-4 border-b border-gray-100 cursor-pointer hover:bg-gray-50 transition',
            selectedChat?.id === chat.id && 'bg-primary-50 border-l-4 border-l-primary-500',
          ]"
        >
          <div class="flex items-center gap-3">
            <UAvatar :src="chat.avatar" :alt="chat.name" />
            <div class="flex-1 min-w-0">
              <h3 class="font-semibold text-gray-900 truncate">{{ chat.name }}</h3>
              <p class="text-sm text-gray-500 truncate">{{ chat.lastMessage }}</p>
            </div>
            <span class="text-xs text-gray-400">{{ chat.timestamp }}</span>
          </div>
        </div>
      </div>

      <!-- User Profile Section -->
      <div class="p-4 border-t border-gray-200">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <UAvatar :src="currentUser.avatar" :alt="currentUser.name" />
            <div>
              <p class="font-semibold text-gray-900">{{ currentUser.name }}</p>
              <p class="text-xs text-gray-500">Online</p>
            </div>
          </div>
          <UDropdown
            :items="[
              [
                { label: 'Settings', icon: 'i-lucide-settings', click: () => router.push('/settings') },
              ],
              [{ label: 'Logout', icon: 'i-lucide-log-out', click: logout }],
            ]"
          >
            <UButton icon="i-lucide-more-vertical" color="gray" variant="ghost" />
          </UDropdown>
        </div>
      </div>
    </div>

    <!-- Chat Area -->
    <div v-if="selectedChat" class="flex-1 flex flex-col">
      <!-- Chat Header -->
      <div class="h-16 border-b border-gray-200 flex items-center justify-between px-6 bg-white">
        <div class="flex items-center gap-3">
          <UAvatar :src="selectedChat.avatar" :alt="selectedChat.name" size="md" />
          <div>
            <h3 class="font-semibold text-gray-900">{{ selectedChat.name }}</h3>
            <p class="text-xs text-gray-500">{{ selectedChat.memberCount }} members</p>
          </div>
        </div>
        <div class="flex items-center gap-2">
          <UButton icon="i-lucide-phone" color="gray" variant="ghost" />
          <UButton icon="i-lucide-info" color="gray" variant="ghost" />
        </div>
      </div>

      <!-- Messages Area -->
      <div class="flex-1 overflow-y-auto p-6 space-y-4">
        <div
          v-for="message in selectedChat.messages"
          :key="message.id"
          :class="['flex gap-3', message.sender.id === currentUser.id && 'flex-row-reverse']"
        >
          <UAvatar :src="message.sender.avatar" :alt="message.sender.name" size="sm" />
          <div
            :class="[
              'flex flex-col gap-1',
              message.sender.id === currentUser.id && 'items-end',
            ]"
          >
            <p class="text-xs font-semibold text-gray-500">{{ message.sender.name }}</p>
            <UCard
              :ui="{ body: { padding: 'p-3' } }"
              :class="[
                message.sender.id === currentUser.id
                  ? 'bg-primary-500 text-white'
                  : 'bg-gray-100',
              ]"
            >
              <p>{{ message.content }}</p>
            </UCard>
            <p class="text-xs text-gray-400">{{ message.timestamp }}</p>
          </div>
        </div>
      </div>

      <!-- Message Input -->
      <div class="h-20 border-t border-gray-200 p-4 bg-white flex items-center gap-2">
        <UInput
          v-model="newMessage"
          placeholder="Type a message..."
          @keyup.enter="sendMessage"
          class="flex-1"
        />
        <UButton icon="i-lucide-plus" color="gray" variant="ghost" />
        <UButton
          icon="i-lucide-send"
          @click="sendMessage"
          :disabled="!newMessage.trim()"
        />
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="flex-1 flex items-center justify-center bg-gray-50">
      <div class="text-center">
        <UIcon name="i-lucide-message-circle" class="w-16 h-16 text-gray-300 mx-auto mb-4" />
        <p class="text-gray-500">Select a chat to start messaging</p>
      </div>
    </div>

    <!-- New Chat Dialog -->
    <NewChatDialog v-model="showNewChatDialog" />
  </div>
</template>
