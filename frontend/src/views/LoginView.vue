<script setup lang="ts">
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { z } from "zod";
import { useAuthStore } from "@/stores/auth";

const router = useRouter();
const authStore = useAuthStore();
const loading = ref(false);
const error = ref("");

const loginSchema = z.object({
  email: z.string().email("Invalid email address"),
  password: z.string().min(6, "Password must be at least 6 characters"),
});

type LoginForm = z.infer<typeof loginSchema>;

const state = reactive<LoginForm>({
  email: "",
  password: "",
});

async function onSubmit() {
  loading.value = true;
  error.value = "";

  try {
    await authStore.login(state.email, state.password);
    router.push("/chats");
  } catch (err: any) {
    error.value = err.response?.data?.message || err.message || "Login failed";
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-900 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900 dark:text-white">
          Sign in to your account
        </h2>
      </div>
      <UForm :schema="loginSchema" :state="state" class="mt-8 space-y-6" @submit="onSubmit">
        <UAlert v-if="error" color="red" variant="soft" :title="error" icon="i-heroicons-exclamation-circle" />
        
        <div class="rounded-md shadow-sm -space-y-px">
          <UFormField label="Email" name="email">
            <UInput v-model="state.email" type="email" placeholder="Email address" />
          </UFormField>
          
          <UFormField label="Password" name="password" class="mt-4">
            <UInput v-model="state.password" type="password" placeholder="Password" />
          </UFormField>
        </div>

        <div>
          <UButton type="submit" block :loading="loading" color="primary">
            Sign in
          </UButton>
        </div>
      </UForm>
      
      <div class="text-center">
         <p class="text-sm text-gray-600 dark:text-gray-400">
            Don't have an account? 
            <router-link to="/register" class="font-medium text-primary-600 hover:text-primary-500">
              Sign up
            </router-link>
         </p>
      </div>
    </div>
  </div>
</template>
