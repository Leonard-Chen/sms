<template>
  <div v-if="show" class="ai-float-wrap">
    <el-tooltip content="AI 助手" placement="right">
      <button class="ai-float-ball" @click="visible = true">AI</button>
    </el-tooltip>
    <AIChatPanel v-model="visible" />
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import AIChatPanel from './AIChatPanel.vue'

const route = useRoute()
const visible = ref(false)

const show = computed(() => {
  const token = localStorage.getItem('access_token')
  if (!token) return false
  return route.path !== '/login' && route.path !== '/callback'
})
</script>

<style scoped>
.ai-float-wrap {
  position: fixed;
  left: 24px;
  bottom: 28px;
  z-index: 2100;
}

.ai-float-ball {
  width: 56px;
  height: 56px;
  border: none;
  border-radius: 50%;
  color: #fff;
  font-weight: 700;
  cursor: pointer;
  background: linear-gradient(135deg, #2563eb, #7c3aed);
  box-shadow: 0 10px 24px rgba(37, 99, 235, 0.35);
}

.ai-float-ball:hover {
  transform: translateY(-2px);
}
</style>
