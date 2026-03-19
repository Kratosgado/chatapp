<script setup lang="ts">
import { ref, computed, onMounted, watch } from "vue";
import { z } from "zod";
import { useFriendsStore } from "@/stores/friends";
import { useChatsStore } from "@/stores/chats";
import { storeToRefs } from "pinia";

const emit = defineEmits<{
  (e: "update:modelValue", value: boolean): void;
}>();

const friendsStore = useFriendsStore();
const chatsStore = useChatsStore();
const { friends } = storeToRefs(friendsStore);

const chatSchema = z.object({
  type: z.enum(["direct", "group"]),
  users: z.array(z.string()).min(1, "Select at least one user"),
  groupName: z.string().optional(),
});

type ChatForm = z.infer<typeof chatSchema>;

const state = ref<ChatForm>({
  type: "direct",
  users: [],
  groupName: "",
});

onMounted(() => {
  friendsStore.fetchFriends();
});

async function createChat() {
  try {
    await chatsStore.create(state.value.users, state.value.groupName);
    state.value = { type: "direct", users: [], groupName: "" };
    // Ideally select the new chat
    chatsStore.fetchChats();
  } catch (error) {
    console.error("Failed to create chat:", error);
  }
}
</script>

<template>
  <UModal>
    <UButton icon="i-heroicons-plus" color="gray" variant="ghost" size="sm" />
    <template #content>
      <UCard title="Start New Chat">
        <UForm
          :schema="chatSchema"
          :state="state"
          @submit="createChat"
          class="space-y-4"
        >
          <UFormField label="Chat Type" name="type">
            <div class="flex gap-4">
              <URadioGroup
                v-model="state.type"
                :items="[
                  { value: 'direct', label: 'Direct Message' },
                  { value: 'group', label: 'Group Chat' },
                ]"
              />
            </div>
          </UFormField>

          <UFormField label="Select Friends" name="users">
            <USelectMenu
              v-model="state.users"
              value-key="id"
              label-key="name"
              multiple
              searchable
              placeholder="Select friends..."
              :items="friends"
              value-attribute="id"
              option-attribute="name"
            >
              <template #content-top>
                <span v-if="state.users.length"
                  >{{ state.users.length }} selected</span
                >
                <span v-else>Select friends...</span>
              </template>
            </USelectMenu>
          </UFormField>

          <UFormField
            v-if="state.type === 'group'"
            label="Group Name"
            name="groupName"
          >
            <UInput v-model="state.groupName" placeholder="My Group" />
          </UFormField>

          <div class="flex justify-end gap-2">
            <UButton color="gray" variant="soft">Cancel</UButton>
            <UButton type="submit" color="primary">Create</UButton>
          </div>
        </UForm>
      </UCard>
    </template>
  </UModal>
</template>
