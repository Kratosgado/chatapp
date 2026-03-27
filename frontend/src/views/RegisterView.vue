<script setup lang="ts">
import { register } from "@/services/auth";
import { useAuthStore } from "@/stores/auth";
import type { AuthFormField, FormSubmitEvent } from "@nuxt/ui";
import { ref } from "vue";
import { useRouter } from "vue-router";
import { z } from "zod";
import { useToast } from "@nuxt/ui/composables";

const router = useRouter();
const authStore = useAuthStore();
const loading = ref(false);
const toast = useToast();

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

const fields: AuthFormField[] = [
  {
    name: "username",
    type: "text",
    label: "Username",
    placeholder: "Enter your username",
    required: true,
  },
  {
    name: "email",
    type: "email",
    label: "Email",
    placeholder: "Enter your email",
    required: true,
  },
  {
    name: "password",
    label: "Password",
    type: "password",
    placeholder: "Enter your password",
    required: true,
  },
  {
    name: "confirmPassword",
    label: "Confirm Password",
    type: "password",
    placeholder: "Confirm your password",
    required: true,
  },
];

const providers = [
  {
    label: "Google",
    icon: "i-simple-icons-google",
    onClick: () => {
      toast.add({ title: "Google", description: "Login with Google" });
    },
  },
  {
    label: "GitHub",
    icon: "i-simple-icons-github",
    onClick: () => {
      toast.add({ title: "GitHub", description: "Login with GitHub" });
    },
  },
];
type Schema = z.output<typeof registerSchema>;
async function onSubmit({ data }: FormSubmitEvent<Schema>) {
  loading.value = true;

  try {
    await register(
      data.username,
      data.email,
      data.password,
      data.confirmPassword,
    );
    // After registration, log them in automatically
    await authStore.login(data.email, data.password);
    router.push("/chats");
  } catch (err: any) {
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="flex flex-col items-center justify-center h-lvh gap-4 p-4">
    <UPageCard class="w-full max-w-md" spotlight>
      <UAuthForm
        :schema="registerSchema"
        title="Register"
        description="Enter your details to create an account."
        icon="i-lucide-user"
        :fields="fields"
        :providers="providers"
        :submit="{ label: 'Sign up', color: 'primary' }"
        @submit="onSubmit"
      />

      <div class="text-center">
        <p class="text-sm text-gray-600 dark:text-gray-400">
          Already have an account?
          <router-link
            to="/login"
            class="font-medium text-primary-600 hover:text-primary-500"
          >
            Sign in
          </router-link>
        </p>
      </div>
    </UPageCard>
  </div>
</template>
