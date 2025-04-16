<script setup>
import { NMessageProvider } from 'naive-ui'
</script>

<template>
  <n-message-provider>
    <router-view />
  </n-message-provider>
</template>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
}

#app {
  height: 100%;
  width: 100%;
}
</style>