<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { z } from 'zod'
import { useFriendsStore } from '@/stores/friends'
import { useChatsStore } from '@/stores/chats'
import { storeToRefs } from 'pinia'

const props = defineProps<{
  modelValue: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
}>()

const isOpen = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
})

const friendsStore = useFriendsStore()
const chatsStore = useChatsStore()
const { friends } = storeToRefs(friendsStore)

const chatSchema = z.object({
  type: z.enum(['direct', 'group']),
  users: z.array(z.string()).min(1, 'Select at least one user'),
  groupName: z.string().optional(),
})

type ChatForm = z.infer<typeof chatSchema>

const state = ref<ChatForm>({
  type: 'direct',
  users: [],
  groupName: '',
})

onMounted(() => {
  friendsStore.fetchFriends()
})

async function createChat() {
  try {
    await chatsStore.create(state.value.users, state.value.groupName)
    isOpen.value = false
    state.value = { type: 'direct', users: [], groupName: '' }
    // Ideally select the new chat
    chatsStore.fetchChats()
  } catch (error) {
    console.error('Failed to create chat:', error)
  }
}
</script>

<template>
  <UModal v-model="isOpen">
    <UCard>
      <template #header>
        <div class="flex items-center justify-between">
          <h3 class="text-base font-semibold leading-6 text-gray-900 dark:text-white">Start New Chat</h3>
          <UButton color="gray" variant="ghost" icon="i-heroicons-x-mark-20-solid" class="-my-1" @click="isOpen = false" />
        </div>
      </template>

      <UForm :schema="chatSchema" :state="state" @submit="createChat" class="space-y-4">
        <UFormField label="Chat Type" name="type">
          <div class="flex gap-4">
             <URadioGroup
                v-model="state.type"
                :options="[
                   { value: 'direct', label: 'Direct Message' },
                   { value: 'group', label: 'Group Chat' },
                ]"
             />
          </div>
        </UFormField>

        <UFormField label="Select Friends" name="users">
          <USelectMenu
            v-model="state.users"
            multiple
            searchable
            placeholder="Select friends..."
            :options="friends"
            value-attribute="id"
            option-attribute="name"
          >
             <template #label>
                 <span v-if="state.users.length">{{ state.users.length }} selected</span>
                 <span v-else>Select friends...</span>
             </template>
          </USelectMenu>
        </UFormField>

        <UFormField v-if="state.type === 'group'" label="Group Name" name="groupName">
          <UInput v-model="state.groupName" placeholder="My Group" />
        </UFormField>

        <div class="flex justify-end gap-2">
          <UButton color="gray" variant="soft" @click="isOpen = false">Cancel</UButton>
          <UButton type="submit" color="primary">Create</UButton>
        </div>
      </UForm>
    </UCard>
  </UModal>
</template>
