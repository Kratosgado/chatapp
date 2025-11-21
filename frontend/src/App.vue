<script setup lang="ts">
import { useColorMode } from "@vueuse/core";
import { onBeforeMount } from "vue";

const colorMode = useColorMode();

onBeforeMount(() => {
  if (!colorMode.value) {
    colorMode.value = "light";
  }
});
</script>

<template>
  <UApp>
    <UHeader>
      <template #title>
        <ULink to="/" class="text-2xl font-bold">Demo Chat App</ULink>
      </template>
    </UHeader>
    <UMain>
      <RouterView />
    </UMain>
    <!-- <UFooter /> -->
  </UApp>
</template>
