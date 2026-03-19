<script setup lang="ts">
import { useAuthStore } from "@/stores/auth";

const authStore = useAuthStore();

// Ensure auth state is current
authStore.checkAuth();

const features = [
  {
    title: "Real-time Messaging",
    description:
      "Instant communication with friends and colleagues using WebSocket technology.",
    icon: "i-heroicons-bolt",
  },
  {
    title: "Secure & Private",
    description:
      "Your conversations are protected. We prioritize your privacy and data security.",
    icon: "i-heroicons-shield-check",
  },
  {
    title: "Cross-Platform",
    description:
      "Access your chats from any device. Seamless experience on mobile and desktop.",
    icon: "i-heroicons-device-phone-mobile",
  },
];
</script>

<template>
  <div class="min-h-screen flex flex-col bg-gray-50 dark:bg-gray-900">
    <!-- Header -->
    <header class="py-4 bg-white dark:bg-gray-800 shadow-sm">
      <UContainer class="flex justify-between items-center">
        <h1 class="text-xl font-bold text-primary-600 dark:text-primary-400">
          ChatApp
        </h1>
        <div class="flex gap-2">
          <template v-if="!authStore.isAuthenticated">
            <UButton to="/login" variant="ghost" color="gray">Log in</UButton>
            <UButton to="/register" color="primary">Sign up</UButton>
          </template>
          <template v-else>
            <UButton
              to="/chats"
              color="primary"
              icon="i-heroicons-chat-bubble-left-right"
              >Open Chats</UButton
            >
          </template>
        </div>
      </UContainer>
    </header>

    <!-- Hero Section -->
    <main class="flex-grow">
      <section class="py-20 lg:py-32">
        <UContainer class="text-center max-w-3xl">
          <h2
            class="text-4xl md:text-5xl font-extrabold tracking-tight mb-6 text-gray-900 dark:text-white"
          >
            Connect with anyone, anywhere.
          </h2>
          <p class="text-xl text-gray-600 dark:text-gray-300 mb-10">
            A modern, fast, and secure way to message your friends. Join
            thousands of users today.
          </p>
          <div class="flex flex-col sm:flex-row justify-center gap-4">
            <template v-if="!authStore.isAuthenticated">
              <UButton
                to="/register"
                size="xl"
                color="primary"
                icon="i-heroicons-rocket-launch"
                >Get Started</UButton
              >
              <UButton
                to="/login"
                size="xl"
                color="gray"
                variant="soft"
                icon="i-heroicons-arrow-right-on-rectangle"
                >Sign In</UButton
              >
            </template>
            <template v-else>
              <UButton
                to="/chats"
                size="xl"
                color="primary"
                icon="i-heroicons-chat-bubble-left-right"
                >Go to Chats</UButton
              >
            </template>
          </div>
        </UContainer>
      </section>

      <!-- Features Grid -->
      <section class="py-16 bg-white dark:bg-gray-800">
        <UContainer>
          <div class="grid md:grid-cols-3 gap-8">
            <UPageCard
              v-for="feature in features"
              :key="feature.title"
              spotlight
              class="text-center hover:shadow-lg transition-shadow"
            >
              <template #header>
                <div class="flex justify-center mb-4">
                  <UIcon
                    :name="feature.icon"
                    class="w-12 h-12 text-primary-500"
                  />
                </div>
                <h3 class="text-xl font-semibold">{{ feature.title }}</h3>
              </template>
              <p class="text-gray-600 dark:text-gray-300">
                {{ feature.description }}
              </p>
            </UPageCard>
          </div>
        </UContainer>
      </section>
    </main>

    <!-- Footer -->
    <footer
      class="py-8 bg-gray-100 dark:bg-gray-900 border-t border-gray-200 dark:border-gray-800"
    >
      <UContainer class="text-center text-gray-500 text-sm">
        &copy; {{ new Date().getFullYear() }} ChatApp. All rights reserved.
      </UContainer>
    </footer>
  </div>
</template>
