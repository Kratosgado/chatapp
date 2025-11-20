<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

interface User {
  name: string
  email: string
  bio: string
  avatar: string
}

interface Settings {
  emailNotifications: boolean
  pushNotifications: boolean
  soundNotifications: boolean
  messagePermission: 'all' | 'friends' | 'none'
  showOnlineStatus: boolean
}

const user = ref<User>({
  name: 'John Doe',
  email: 'john@example.com',
  bio: 'Love chatting with friends!',
  avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=John',
})

const settings = ref<Settings>({
  emailNotifications: true,
  pushNotifications: true,
  soundNotifications: true,
  messagePermission: 'all',
  showOnlineStatus: true,
})

const saving = ref(false)

async function saveProfile() {
  saving.value = true
  try {
    // TODO: API call to save profile
    console.log('Saving profile:', user.value)
    // Show success toast
  } finally {
    saving.value = false
  }
}

async function saveNotifications() {
  saving.value = true
  try {
    // TODO: API call to save notification settings
    console.log('Saving notification settings:', settings.value)
    // Show success toast
  } finally {
    saving.value = false
  }
}

async function savePrivacy() {
  saving.value = true
  try {
    // TODO: API call to save privacy settings
    console.log('Saving privacy settings:', settings.value)
    // Show success toast
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="max-w-2xl mx-auto py-8 px-4">
    <UCard>
      <template #header>
        <div class="flex items-center justify-between">
          <h2 class="text-2xl font-bold text-gray-900">Settings</h2>
          <RouterLink to="/chats">
            <UButton icon="i-lucide-arrow-left" color="gray" variant="ghost" />
          </RouterLink>
        </div>
      </template>

      <UTabs
        :items="[
          { slot: 'profile', label: 'Profile' },
          { slot: 'notifications', label: 'Notifications' },
          { slot: 'privacy', label: 'Privacy' },
        ]"
      >
        <template #profile>
          <div class="space-y-4">
            <div class="flex items-center gap-4">
              <UAvatar :src="user.avatar" :alt="user.name" size="xl" />
              <UButton>Change Avatar</UButton>
            </div>

            <UFormField label="Full Name">
              <UInput v-model="user.name" />
            </UFormField>

            <UFormField label="Email">
              <UInput v-model="user.email" type="email" />
            </UFormField>

            <UFormField label="Bio">
              <UTextarea v-model="user.bio" placeholder="Tell us about yourself" />
            </UFormField>

            <UButton @click="saveProfile" :loading="saving">Save Changes</UButton>
          </div>
        </template>

        <template #notifications>
          <div class="space-y-4">
            <UCheckbox v-model="settings.emailNotifications" label="Email Notifications" />
            <UCheckbox v-model="settings.pushNotifications" label="Push Notifications" />
            <UCheckbox v-model="settings.soundNotifications" label="Sound Notifications" />

            <UButton @click="saveNotifications" :loading="saving">Save Preferences</UButton>
          </div>
        </template>

        <template #privacy>
          <div class="space-y-4">
            <UFormField label="Who can message you?">
              <USelectMenu
                v-model="settings.messagePermission"
                :options="[
                  { value: 'all', label: 'Everyone' },
                  { value: 'friends', label: 'Friends only' },
                  { value: 'none', label: 'Nobody' },
                ]"
              />
            </UFormField>

            <UCheckbox v-model="settings.showOnlineStatus" label="Show online status" />

            <UButton @click="savePrivacy" :loading="saving">Save Settings</UButton>
          </div>
        </template>
      </UTabs>
    </UCard>
  </div>
</template>
