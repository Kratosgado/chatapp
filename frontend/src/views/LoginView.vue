<script setup lang="ts">
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { z } from "zod";
import { useAuthStore } from "@/stores/auth";
import type { AuthFormField, FormSubmitEvent } from "@nuxt/ui";

const router = useRouter();
const authStore = useAuthStore();
const loading = ref(false);
const toast = useToast();

async function onSubmit({ data }: FormSubmitEvent<Schema>) {
  loading.value = true;

  try {
    await authStore.login(data.email, data.password);
    router.push("/chats");
  } catch (err: any) {
  } finally {
    loading.value = false;
  }
}

const fields: AuthFormField[] = [
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
    name: "remember",
    label: "Remember me",
    type: "checkbox",
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

const schema = z.object({
  email: z.email("Invalid email"),
  password: z
    .string("Password is required")
    .min(8, "Must be at least 8 characters"),
});

type Schema = z.output<typeof schema>;
</script>

<template>
  <div class="flex flex-col items-center justify-center h-lvh gap-4 p-4">
    <UPageCard class="w-full max-w-md" spotlight>
      <UAuthForm
        :schema="schema"
        title="Login"
        description="Enter your credentials to access your account."
        icon="i-lucide-user"
        :fields="fields"
        :providers="providers"
        :submit="{ label: 'Sign in', color: 'primary' }"
        @submit="onSubmit"
      />
      <div class="text-center">
        <p class="text-sm text-muted">
          Don't have an account?
          <router-link
            to="/register"
            class="font-medium text-primary-600 hover:text-primary-500"
          >
            Sign up
          </router-link>
        </p>
      </div>
    </UPageCard>
  </div>
</template>
