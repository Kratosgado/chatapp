<script setup lang="ts">
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { z } from "zod";
import { register } from "@/services/auth";
import { useAuthStore } from "@/stores/auth";

const router = useRouter();
const authStore = useAuthStore();
const loading = ref(false);
const error = ref("");

const registerSchema = z
  .object({
    username: z.string().min(3, "Username must be at least 3 characters"),
    email: z.string().email("Invalid email address"),
    password: z.string().min(6, "Password must be at least 6 characters"),
    confirmPassword: z.string(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"],
  });

type RegisterForm = z.infer<typeof registerSchema>;

const state = reactive<RegisterForm>({
  username: "",
  email: "",
  password: "",
  confirmPassword: "",
});

async function onSubmit() {
  loading.value = true;
  error.value = "";

  try {
    await register(state.username, state.email, state.password, state.confirmPassword);
    // After registration, log them in automatically
    await authStore.login(state.email, state.password);
    router.push("/chats");
  } catch (err: any) {
    error.value = err.response?.data?.message || err.message || "Registration failed";
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
          Create your account
        </h2>
      </div>
      <UForm :schema="registerSchema" :state="state" class="mt-8 space-y-6" @submit="onSubmit">
        <UAlert v-if="error" color="red" variant="soft" :title="error" icon="i-heroicons-exclamation-circle" />
        
        <div class="rounded-md shadow-sm -space-y-px">
          <UFormField label="Username" name="username">
            <UInput v-model="state.username" type="text" placeholder="Username" />
          </UFormField>

          <UFormField label="Email" name="email" class="mt-4">
            <UInput v-model="state.email" type="email" placeholder="Email address" />
          </UFormField>
          
          <UFormField label="Password" name="password" class="mt-4">
            <UInput v-model="state.password" type="password" placeholder="Password" />
          </UFormField>

          <UFormField label="Confirm Password" name="confirmPassword" class="mt-4">
            <UInput v-model="state.confirmPassword" type="password" placeholder="Confirm Password" />
          </UFormField>
        </div>

        <div>
          <UButton type="submit" block :loading="loading" color="primary">
            Sign up
          </UButton>
        </div>
      </UForm>

      <div class="text-center">
         <p class="text-sm text-gray-600 dark:text-gray-400">
            Already have an account? 
            <router-link to="/login" class="font-medium text-primary-600 hover:text-primary-500">
              Sign in
            </router-link>
         </p>
      </div>
    </div>
  </div>
</template>
