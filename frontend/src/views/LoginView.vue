<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { z } from "zod";

const router = useRouter();
const loading = ref(false);
const error = ref("");

const loginSchema = z.object({
  email: z.string().email("Invalid email address"),
  password: z.string().min(6, "Password must be at least 6 characters"),
});

type LoginForm = z.infer<typeof loginSchema>;

const state = ref<LoginForm>({
  email: "",
  password: "",
});

async function onSubmit() {
  loading.value = true;
  error.value = "";

  try {
    // TODO: Replace with actual API call
    const response = await fetch("http://localhost:8080/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(state.value),
    });

    if (!response.ok) {
      throw new Error("Login failed");
    }

    const data = await response.json();
    localStorage.setItem("token", data.token);
    localStorage.setItem("user", JSON.stringify(data.user));

    router.push("/chats");
  } catch (err) {
    error.value = err instanceof Error ? err.message : "Login failed";
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <UContainer class="h-full w-full">
    <UCard class="w-full max-w-md">
      <template #header>
        <div class="text-center">
          <h1 class="text-2xl font-bold text-gray-900">Chat App</h1>
          <p class="text-gray-500 text-sm mt-2">Sign in to your account</p>
        </div>
      </template>

      <div class="space-y-4">
        <UAlert
          v-if="error"
          icon="i-lucide-alert-circle"
          color="red"
          :title="error"
        />

        <UForm
          :schema="loginSchema"
          :state="state"
          @submit="onSubmit"
          class="space-y-4"
        >
          <UFormField label="Email" name="email">
            <UInput
              v-model="state.email"
              type="email"
              placeholder="you@example.com"
              :disabled="loading"
            />
          </UFormField>

          <UFormField label="Password" name="password">
            <UInput
              v-model="state.password"
              type="password"
              placeholder="••••••••"
              :disabled="loading"
            />
          </UFormField>

          <UButton type="submit" block :loading="loading">Sign In</UButton>
        </UForm>
      </div>

      <template #footer>
        <p class="text-center text-sm text-gray-500">
          Don't have an account?
          <ULink
            to="/register"
            class="text-primary-500 hover:text-primary-600 font-semibold"
            >Sign up</ULink
          >
        </p>
      </template>
    </UCard>
  </UContainer>
</template>
