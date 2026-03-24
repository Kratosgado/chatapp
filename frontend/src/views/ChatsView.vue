<script setup lang="ts">
import Sidebar from "@/components/Sidebar.vue";
import ChatWindow from "@/components/chat/ChatWindow.vue";
import { ref } from "vue";

const isSidebarOpen = ref(false);
</script>

<template>
  <div class="flex h-screen bg-white dark:bg-gray-900 overflow-hidden">
    <!-- Desktop Sidebar -->
    <div class="hidden md:flex flex-col w-80 h-full">
      <Sidebar />
    </div>

    <!-- Mobile Sidebar (Slideover) -->
    <UDrawer
      v-model:open="isSidebarOpen"
      direction="left"
      :ui="{ width: 'w-80' }"
    >
      <template #content>
        <Sidebar />
      </template>
    </UDrawer>

    <!-- Main Content -->
    <div class="flex-1 flex flex-col min-w-0 h-full relative">
      <!-- Mobile Header to toggle sidebar -->
      <div
        class="md:hidden flex items-center p-4 border-b border-gray-200 dark:border-gray-800 shrink-0"
      >
        <UButton
          icon="i-heroicons-bars-3"
          color="gray"
          variant="ghost"
          @click="isSidebarOpen = true"
        />
        <h1 class="ml-4 font-bold text-lg">ChatApp</h1>
      </div>

      <ChatWindow />
    </div>
  </div>
</template>
