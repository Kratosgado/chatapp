<script setup lang="ts">
import { ref } from "vue";
import { useFriendsStore } from "@/stores/friends";
import { useAuthStore } from "@/stores/auth";
import { searchUsers } from "@/services/user";

const open = ref(false);

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
    open.value = false;
  } catch (error) {
    console.error("Failed to add friend:", error);
  }
}
</script>

<template>
  <div>
    <UButton
      icon="i-heroicons-user-plus"
      color="neutral"
      variant="ghost"
      size="sm"
      class="hover:text-primary-500 transition-colors"
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
              <h3
                class="text-lg font-semibold leading-6 text-gray-900 dark:text-white flex items-center gap-2"
              >
                <UIcon
                  name="i-heroicons-user-plus"
                  class="w-5 h-5 text-primary-500"
                />
                Add Friend
              </h3>
              <UButton
                color="neutral"
                variant="ghost"
                icon="i-heroicons-x-mark-20-solid"
                class="-my-1"
                @click="open = false"
              />
            </div>
          </template>

          <div class="space-y-6">
            <div class="relative">
              <UInput
                v-model="query"
                placeholder="Search by name or email..."
                icon="i-heroicons-magnifying-glass"
                size="lg"
                :ui="{}"
                @keyup.enter="handleSearch"
                autofocus
              >
                <template #trailing>
                  <UButton
                    v-if="query.trim()"
                    variant="ghost"
                    color="neutral"
                    :padded="false"
                    :loading="loading"
                    @click="handleSearch"
                    class="mr-1"
                  >
                    Search
                  </UButton>
                </template>
              </UInput>
              <p class="text-xs text-gray-500 mt-2 px-1">
                Press
                <kbd
                  class="font-sans px-1 py-0.5 bg-gray-100 dark:bg-gray-800 rounded border border-gray-200 dark:border-gray-700"
                  >Enter</kbd
                >
                to search
              </p>
            </div>

            <div
              class="min-h-[200px] max-h-[400px] overflow-y-auto pr-1 custom-scrollbar"
            >
              <!-- Results -->
              <div v-if="results.length > 0" class="space-y-3">
                <transition-group name="list" tag="div">
                  <div
                    v-for="user in results"
                    :key="user.id"
                    class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-800/50 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 transition-all border border-transparent hover:border-gray-200 dark:hover:border-gray-700 group"
                  >
                    <div class="flex items-center gap-4">
                      <UAvatar
                        :src="user.avatarUrl"
                        :alt="user.name"
                        size="md"
                      />
                      <div>
                        <p
                          class="font-medium text-sm text-gray-900 dark:text-gray-100"
                        >
                          {{ user.name }}
                        </p>
                        <p class="text-xs text-gray-500">{{ user.email }}</p>
                      </div>
                    </div>
                    <UButton
                      size="xs"
                      color="primary"
                      variant="soft"
                      icon="i-heroicons-plus"
                      class="opacity-0 group-hover:opacity-100 transition-opacity focus:opacity-100"
                      @click="handleAdd(user)"
                    >
                      Add
                    </UButton>
                  </div>
                </transition-group>
              </div>

              <!-- Empty State -->
              <div
                v-else-if="!loading && query"
                class="flex flex-col items-center justify-center h-40 text-center text-gray-400"
              >
                <UIcon
                  name="i-heroicons-face-frown"
                  class="w-12 h-12 mb-2 opacity-50"
                />
                <p>No users found matching "{{ query }}"</p>
              </div>

              <!-- Initial State -->
              <div
                v-else-if="!loading && !query"
                class="flex flex-col items-center justify-center h-40 text-center text-gray-400"
              >
                <UIcon
                  name="i-heroicons-users"
                  class="w-12 h-12 mb-2 opacity-50"
                />
                <p>Search for people to add as friends</p>
              </div>

              <!-- Loading Skeleton -->
              <div v-else-if="loading" class="space-y-3">
                <div
                  v-for="i in 3"
                  :key="i"
                  class="flex items-center justify-between p-3 rounded-lg animate-pulse"
                >
                  <div class="flex items-center gap-4 w-full">
                    <div
                      class="w-10 h-10 bg-gray-200 dark:bg-gray-700 rounded-full"
                    ></div>
                    <div class="space-y-2 flex-1">
                      <div
                        class="h-4 bg-gray-200 dark:bg-gray-700 rounded w-1/3"
                      ></div>
                      <div
                        class="h-3 bg-gray-200 dark:bg-gray-700 rounded w-1/2"
                      ></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </UCard>
      </template>
    </UModal>
  </div>
</template>

<style scoped>
.list-enter-active,
.list-leave-active {
  transition: all 0.3s ease;
}
.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
