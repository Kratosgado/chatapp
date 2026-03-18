<script setup lang="ts">
import { ref } from "vue";
import { z } from "zod";
import { useFriendsStore } from "@/stores/friends";
import { useAuthStore } from "@/stores/auth";
import { searchUsers } from "@/services/user";

const props = defineProps<{
  modelValue: boolean;
}>();

const emit = defineEmits<{
  (e: "update:modelValue", value: boolean): void;
}>();

const isOpen = computed({
  get: () => props.modelValue,
  set: (value) => emit("update:modelValue", value),
});

import { computed } from "vue";

const friendsStore = useFriendsStore();
const authStore = useAuthStore();

const query = ref("");
const results = ref<any[]>([]);
const loading = ref(false);

async function handleSearch() {
  if (!query.value.trim()) return;
  loading.value = true;
  try {
    results.value = await searchUsers(query.value);
  } catch (error) {
    console.error("Search failed:", error);
  } finally {
    loading.value = false;
  }
}

async function handleAdd(user: any) {
  try {
    await friendsStore.sendRequest(user.id);
    isOpen.value = false;
  } catch (error) {
    console.error("Failed to add friend:", error);
  }
}
</script>

<template>
  <UModal v-model="isOpen">
    <UButton
      icon="i-heroicons-user-plus"
      color="gray"
      variant="ghost"
      size="sm"
    />
    <template #content>
      <UCard>
        <template #header>
          <div class="flex items-center justify-between">
            <h3
              class="text-base font-semibold leading-6 text-gray-900 dark:text-white"
            >
              Add Friend
            </h3>
            <UButton
              color="gray"
              variant="ghost"
              icon="i-heroicons-x-mark-20-solid"
              class="-my-1"
              @click="isOpen = false"
            />
          </div>
        </template>

        <div class="space-y-4">
          <UInput
            v-model="query"
            placeholder="Search by name or email..."
            icon="i-heroicons-magnifying-glass"
            @keyup.enter="handleSearch"
          >
            <template #trailing>
              <UButton
                v-if="query.trim()"
                variant="link"
                color="gray"
                :padded="false"
                @click="handleSearch"
                :loading="loading"
                >Search</UButton
              >
            </template>
          </UInput>

          <div
            v-if="results.length > 0"
            class="mt-4 space-y-2 max-h-60 overflow-y-auto"
          >
            <div
              v-for="user in results"
              :key="user.id"
              class="flex items-center justify-between p-2 hover:bg-gray-50 dark:hover:bg-gray-800 rounded"
            >
              <div class="flex items-center gap-3">
                <UAvatar :src="user.avatarUrl" :alt="user.name" size="sm" />
                <div>
                  <p class="font-medium text-sm">{{ user.name }}</p>
                  <p class="text-xs text-gray-500">{{ user.email }}</p>
                </div>
              </div>
              <UButton
                size="xs"
                color="primary"
                variant="soft"
                @click="handleAdd(user)"
                >Add</UButton
              >
            </div>
          </div>
          <div
            v-else-if="query && !loading"
            class="text-center text-sm text-gray-500"
          >
            No users found.
          </div>
        </div>
      </UCard>
    </template>
  </UModal>
</template>
