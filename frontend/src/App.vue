<script setup lang="ts">
import { useColorMode } from "@vueuse/core";
import { onBeforeMount } from "vue";
import IncomingCallModal from "@/components/call/IncomingCallModal.vue";
import ActiveCallModal from "@/components/call/ActiveCallModal.vue";

const colorMode = useColorMode();

onBeforeMount(() => {
  if (!colorMode.value) {
    colorMode.value = "light";
  }
});
</script>

<template>
  <UApp>
    <!-- <UHeader> -->
    <!--   <template #title> -->
    <!--     <ULink to="/" class="text-2xl font-bold">Demo Chat App</ULink> -->
    <!--   </template> -->
    <!-- </UHeader> -->
    <UMain class="flex-1 overflow-y-auto">
      <RouterView />
    </UMain>
    <IncomingCallModal />
    <ActiveCallModal />
    <!-- <UFooter /> -->
  </UApp>
</template>
