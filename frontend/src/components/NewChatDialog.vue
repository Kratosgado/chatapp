<script setup lang="ts">
import { ref, computed, onMounted, watch } from "vue";
import { z } from "zod";
import { useFriendsStore } from "@/stores/friends";
import { useChatsStore } from "@/stores/chats";
import { storeToRefs } from "pinia";

const open = ref(false);
// defineShortcuts({
//   shift_n: () => (open.value = true),
// });

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
  groupName: undefined,
});

onMounted(() => {
  friendsStore.fetchFriends();
});

async function createChat() {
  try {
    await chatsStore.create(state.value.users, state.value.groupName);
    state.value = { type: "direct", users: [], groupName: undefined };
    // Ideally select the new chat
    chatsStore.fetchChats();
    open.value = false;
  } catch (error) {
    console.error("Failed to create chat:", error);
  }
}
</script>

<template>
  <div>
    <UButton
      icon="i-heroicons-plus"
      color="primary"
      variant="soft"
      size="sm"
      class="rounded-full transition-all duration-200 hover:scale-105"
      @click="open = true"
    />

    <UModal :open>
      <template #content>
        <UCard
          :ui="{
            ring: '',
            divide: 'divide-y divide-gray-100 dark:divide-gray-800',
          }"
        >
          <template #header>
            <div class="flex items-center justify-between">
              <h2
                class="text-xl font-bold text-gray-900 dark:text-white flex items-center gap-2"
              >
                <UIcon
                  name="i-heroicons-chat-bubble-left-right"
                  class="w-6 h-6 text-primary-500"
                />
                Start New Chat
              </h2>
              <UButton
                color="neutral"
                variant="ghost"
                icon="i-heroicons-x-mark-20-solid"
                class="-my-1"
                @click="open = false"
              />
            </div>
          </template>

          <UForm
            :schema="chatSchema"
            :state="state"
            @submit="createChat"
            class="space-y-6"
          >
            <!-- Chat Type Selection -->
            <div class="grid grid-cols-2 gap-4">
              <div
                class="cursor-pointer border rounded-lg p-4 text-center transition-all duration-200 hover:border-primary-500 hover:bg-primary-50 dark:hover:bg-primary-900/10"
                :class="
                  state.type === 'direct'
                    ? 'border-primary-500 bg-primary-50 dark:bg-primary-900/10 ring-2 ring-primary-500 ring-opacity-50'
                    : 'border-gray-200 dark:border-gray-700'
                "
                @click="state.type = 'direct'"
              >
                <UIcon
                  name="i-heroicons-user"
                  class="w-8 h-8 mx-auto mb-2 text-primary-500"
                />
                <span class="block font-medium text-sm">Direct Message</span>
              </div>

              <div
                class="cursor-pointer border rounded-lg p-4 text-center transition-all duration-200 hover:border-primary-500 hover:bg-primary-50 dark:hover:bg-primary-900/10"
                :class="
                  state.type === 'group'
                    ? 'border-primary-500 bg-primary-50 dark:bg-primary-900/10 ring-2 ring-primary-500 ring-opacity-50'
                    : 'border-gray-200 dark:border-gray-700'
                "
                @click="state.type = 'group'"
              >
                <UIcon
                  name="i-heroicons-users"
                  class="w-8 h-8 mx-auto mb-2 text-primary-500"
                />
                <span class="block font-medium text-sm">Group Chat</span>
              </div>
            </div>

            <!-- Hidden radio for form binding compatibility if needed, but we set state directly above -->
            <div class="hidden">
              <URadioGroup
                v-model="state.type"
                :items="[{ value: 'direct' }, { value: 'group' }]"
              />
            </div>

            <!-- Friend Selection -->
            <UFormField
              label="Select Friends"
              name="users"
              class="font-medium text-gray-700 dark:text-gray-200"
            >
              <USelectMenu
                v-model="state.users"
                value-key="id"
                label-key="name"
                multiple
                searchable
                placeholder="Search friends..."
                :items="friends"
                value-attribute="id"
                option-attribute="name"
              />
            </UFormField>

            <!-- Group Name Input -->
            <transition
              enter-active-class="transition duration-200 ease-out"
              enter-from-class="transform scale-95 opacity-0"
              enter-to-class="transform scale-100 opacity-100"
              leave-active-class="transition duration-150 ease-in"
              leave-from-class="transform scale-100 opacity-100"
              leave-to-class="transform scale-95 opacity-0"
            >
              <UFormField
                v-if="state.type === 'group'"
                label="Group Name"
                name="groupName"
                class="font-medium text-gray-700 dark:text-gray-200"
              >
                <UInput
                  v-model="state.groupName"
                  placeholder="e.g., Weekend Trip Team"
                  icon="i-heroicons-user-group"
                />
              </UFormField>
            </transition>

            <div class="flex justify-end gap-3 mt-6">
              <UButton color="neutral" variant="ghost" @click="open = false"
                >Cancel</UButton
              >
              <UButton
                type="submit"
                color="primary"
                icon="i-heroicons-paper-airplane"
                trailing
                >Create Chat</UButton
              >
            </div>
          </UForm>
        </UCard>
      </template>
    </UModal>
  </div>
</template>
