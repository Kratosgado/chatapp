<script setup lang="ts">
import { computed } from 'vue'
import { useChatsStore } from '@/stores/chats'
import { storeToRefs } from 'pinia'

const chatsStore = useChatsStore()
const { typingUsers, currentChat } = storeToRefs(chatsStore)

const typingText = computed(() => {
    if (!currentChat.value) return ''
    const userIds = Object.keys(typingUsers.value)
    if (userIds.length === 0) return ''

    // Map IDs to names (need to find member in currentChat)
    const names = userIds.map(id => {
        const member = currentChat.value?.members.find(m => m.id === id)
        return member ? member.name : 'Someone'
    })

    if (names.length === 1) return `${names[0]} is typing...`
    if (names.length === 2) return `${names[0]} and ${names[1]} are typing...`
    return `${names.length} people are typing...`
})
</script>

<template>
  <div v-if="typingText" class="text-xs text-gray-500 italic px-4 py-2">
      {{ typingText }}
  </div>
</template>
