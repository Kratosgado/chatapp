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
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-3xl mx-auto">
      <div class="mb-6 flex items-center justify-between">
         <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Settings</h1>
         <UButton icon="i-heroicons-arrow-left" color="neutral" variant="ghost" @click="router.push('/chats')">Back to Chats</UButton>
      </div>

      <UCard>
        <template #header>
          <h2 class="text-lg font-medium text-gray-900 dark:text-white">Profile</h2>
          <p class="mt-1 text-sm text-gray-500 dark:text-gray-400">Update your personal information.</p>
        </template>

        <form @submit.prevent="handleSave" class="space-y-6">
           <UAlert v-if="error" color="error" variant="soft" :title="error" icon="i-heroicons-exclamation-circle" />
           <UAlert v-if="success" color="success" variant="soft" :title="success" icon="i-heroicons-check-circle" />

           <div class="flex items-center gap-6">
              <UAvatar :src="avatarUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(name || 'User')}`" size="3xl" />
              <div class="flex-1">
                 <UFormField label="Avatar URL" name="avatarUrl" help="Paste an image URL for your avatar">
                    <UInput v-model="avatarUrl" placeholder="https://example.com/avatar.jpg" />
                 </UFormField>
              </div>
           </div>

           <UFormField label="Display Name" name="name" required>
              <UInput v-model="name" />
           </UFormField>

           <UFormField label="Email" name="email">
              <UInput :model-value="user?.email" disabled />
           </UFormField>

           <div class="flex justify-end">
              <UButton type="submit" :loading="saving" color="primary">Save Changes</UButton>
           </div>
        </form>
      </UCard>
    </div>
  </div>
</template>
