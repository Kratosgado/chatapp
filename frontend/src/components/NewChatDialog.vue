<script setup lang="ts">
import { ref } from 'vue'
import { z } from 'zod'

const props = defineProps<{
  modelValue: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

const isOpen = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
})

import { computed } from 'vue'

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

const availableUsers = [
  { id: '2', name: 'Alice Johnson' },
  { id: '3', name: 'Bob Smith' },
  { id: '4', name: 'Charlie Brown' },
  { id: '5', name: 'Diana Prince' },
  { id: '6', name: 'Eve Wilson' },
]

function createChat() {
  // TODO: Implement API call to create chat
  console.log('Creating chat:', state.value)
  isOpen.value = false
  state.value = { type: 'direct', users: [], groupName: '' }
}
</script>

<template>
  <UModal v-model="isOpen" title="Start New Chat">
    <UForm :schema="chatSchema" :state="state" @submit="createChat" class="space-y-4">
      <UFormField label="Chat Type" name="type">
        <USegmentControl
          v-model="state.type"
          :options="[
            { value: 'direct', label: 'Direct Message' },
            { value: 'group', label: 'Group Chat' },
          ]"
        />
      </UFormField>

      <UFormField label="Select Users" name="users">
        <USelectMenu
          v-model="state.users"
          multiple
          :options="availableUsers"
          option-attribute="name"
          searchable
          placeholder="Search users..."
        />
    </UFormField>

      <UFormField v-if="state.type === 'group'" label="Group Name" name="groupName">
        <UInput v-model="state.groupName" placeholder="My Group" />
      </UFormField>

      <template #footer>
        <div class="flex gap-2 justify-end">
          <UButton color="gray" @click="isOpen = false">Cancel</UButton>
          <UButton type="submit">Create</UButton>
        </div>
      </template>
    </UForm>
  </UModal>
</template>
