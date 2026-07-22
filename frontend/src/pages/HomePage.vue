<script setup>
// This page displays the simplest dashboard home content.
// It receives backend connection data from App.vue instead of requesting data by itself.
defineOptions({
  inheritAttrs: false
})

defineProps({
  systemLoading: Boolean,
  systemError: String,
  backendInfo: Object
})
</script>

<template>
  <section class="panel status-panel">
    <h2>后端连接状态</h2>

    <p v-if="systemLoading" class="status muted">正在请求系统接口...</p>

    <p v-else-if="systemError" class="status error">
      {{ systemError }}
    </p>

    <div v-else-if="backendInfo" class="result">
      <p class="status success">{{ backendInfo.message }}</p>
      <dl>
        <dt>接口模块</dt>
        <dd>{{ backendInfo.module }}</dd>
      </dl>
    </div>
  </section>
</template>
