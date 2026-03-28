<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'
import { updateProfile } from '@/services/user'

const router = useRouter()
const authStore = useAuthStore()
const { user } = storeToRefs(authStore)

const name = ref('')
const avatarUrl = ref('')
const saving = ref(false)
const error = ref('')
const success = ref('')

onMounted(() => {
  if (user.value) {
    name.value = user.value.name
    avatarUrl.value = user.value.avatarUrl || ''
  }
})

async function handleSave() {
  if (!user.value) return
  saving.value = true
  error.value = ''
  success.value = ''

  try {
    const updatedUser = await updateProfile({
      ...user.value,
      name: name.value,
      avatarUrl: avatarUrl.value || undefined
    })
    // Update store
    user.value = updatedUser
    success.value = 'Profile updated successfully'
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Failed to update profile'
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-gray-50/50 dark:bg-gray-950/50 flex flex-col pt-12">
    <div class="w-full max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="flex items-center justify-between pb-8 border-b border-gray-200 dark:border-gray-800">
        <div>
          <h1 class="text-3xl font-bold tracking-tight text-gray-900 dark:text-white">Settings</h1>
          <p class="mt-2 text-sm text-gray-500 dark:text-gray-400">Manage your profile and account preferences</p>
        </div>
        <UButton icon="i-heroicons-arrow-left" color="gray" variant="ghost" @click="router.push('/chats')">
          Back to Chats
        </UButton>
      </div>

      <div class="mt-12">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
          <!-- Sidebar Info -->
          <div class="md:col-span-1">
            <h2 class="text-lg font-semibold text-gray-900 dark:text-white">Public Profile</h2>
            <p class="mt-1 text-sm text-gray-500 dark:text-gray-400">
              This information will be displayed publicly so be careful what you share.
            </p>
          </div>

          <!-- Form -->
          <UCard class="md:col-span-2 shadow-sm border border-gray-200 dark:border-gray-800">
            <form @submit.prevent="handleSave" class="space-y-8 p-4">
              <!-- Alerts -->
              <div v-if="error || success" class="space-y-4">
                <UAlert v-if="error" color="red" variant="soft" :title="error" icon="i-heroicons-exclamation-circle" />
                <UAlert v-if="success" color="green" variant="soft" :title="success" icon="i-heroicons-check-circle" />
              </div>

              <!-- Avatar Section -->
              <div class="flex items-start gap-6">
                <UAvatar
                  :src="avatarUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(name || 'User')}&background=random`"
                  size="3xl"
                  class="ring-4 ring-white dark:ring-gray-900 shadow-md"
                />
                <div class="flex-1 space-y-2">
                  <UFormField label="Avatar URL" name="avatarUrl" help="Paste a link to your new avatar image">
                    <UInput
                      v-model="avatarUrl"
                      icon="i-heroicons-photo"
                      placeholder="https://example.com/avatar.jpg"
                      size="md"
                    />
                  </UFormField>
                </div>
              </div>

              <div class="border-t border-gray-100 dark:border-gray-800 pt-6">
                <div class="grid grid-cols-1 gap-6">
                  <UFormField label="Display Name" name="name" required>
                    <UInput v-model="name" icon="i-heroicons-user" size="md" />
                  </UFormField>

                  <UFormField label="Email Address" name="email" help="Your email address is used for login and cannot be changed">
                    <UInput :model-value="user?.email" disabled icon="i-heroicons-envelope" size="md" class="opacity-70" />
                  </UFormField>
                </div>
              </div>

              <!-- Actions -->
              <div class="flex items-center justify-end pt-6 border-t border-gray-100 dark:border-gray-800 gap-4">
                <UButton type="button" variant="ghost" color="gray" @click="router.push('/chats')">
                  Cancel
                </UButton>
                <UButton type="submit" :loading="saving" color="primary" icon="i-heroicons-check">
                  Save Changes
                </UButton>
              </div>
            </form>
          </UCard>
        </div>
      </div>
    </div>
  </div>
</template>