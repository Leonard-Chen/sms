<template>
  <el-drawer
      v-model="visible"
      title="AI 助手"
      direction="rtl"
      size="420px"
      :with-header="true"
      destroy-on-close
  >
    <div class="ai-panel">
      <div class="ai-messages" ref="msgRef">
        <div v-for="(item, idx) in messages" :key="idx" :class="['msg-item', item.role]">
          <div class="msg-bubble">{{ item.content }}</div>
        </div>
      </div>

      <div class="ai-actions">
        <el-button link type="danger" @click="clearHistory">清空会话</el-button>
      </div>

      <div class="ai-input">
        <el-input
            v-model="inputText"
            type="textarea"
            :rows="3"
            resize="none"
            placeholder="请输入你的问题，Enter 发送，Shift+Enter 换行"
            @keydown="onKeydown"
        />
        <el-button type="primary" :loading="sending" @click="sendMessage">发送</el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script setup>
import {ElMessage} from 'element-plus'
import {chatWithAi} from '../../api/ai'
import {nextTick, ref, watch} from 'vue'

const visible = defineModel({type: Boolean, default: false})

const inputText = ref('')
const sending = ref(false)
const msgRef = ref(null)
const sessionId = ref(localStorage.getItem('sms_ai_session_id') || crypto.randomUUID())
const messages = ref([
  {role: 'assistant', content: '你好，我是 AI 助手。可以帮你解释功能、排查问题、整理需求。'}
])

const scrollToBottom = async () => {
  await nextTick()
  if (msgRef.value) {
    msgRef.value.scrollTop = msgRef.value.scrollHeight
  }
}

watch(visible, async (v) => {
  if (v) {
    await scrollToBottom()
  }
})

const clearHistory = () => {
  messages.value = [{role: 'assistant', content: '会话已清空，你可以开始新的提问。'}]
  sessionId.value = crypto.randomUUID()
  localStorage.setItem('sms_ai_session_id', sessionId.value)
}

const sendMessage = async () => {
  const text = inputText.value.trim()
  if (!text || sending.value) return

  messages.value.push({role: 'user', content: text})
  inputText.value = ''
  await scrollToBottom()

  sending.value = true
  try {
    const history = messages.value
        .slice(-20)
        .filter(m => m.role === 'user' || m.role === 'assistant')
        .map(m => ({role: m.role, content: m.content}))
    const res = await chatWithAi({
      sessionId: sessionId.value,
      message: text,
      history
    })
    sessionId.value = res.sessionId || sessionId.value
    localStorage.setItem('sms_ai_session_id', sessionId.value)
    messages.value.push({role: 'assistant', content: res.reply})
  } catch (e) {
    messages.value.push({role: 'assistant', content: 'AI 助手暂时不可用，请稍后再试。'})
    ElMessage.error(e?.message || 'AI 请求失败')
  } finally {
    sending.value = false
    await scrollToBottom()
  }
}

const onKeydown = (event) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    sendMessage()
  }
}
</script>

<style scoped>
.ai-panel {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.ai-messages {
  flex: 1;
  overflow: auto;
  padding: 4px 0;
}

.msg-item {
  display: flex;
  margin: 10px 0;
}

.msg-item.user {
  justify-content: flex-end;
}

.msg-item.assistant {
  justify-content: flex-start;
}

.msg-bubble {
  max-width: 84%;
  padding: 10px 12px;
  border-radius: 12px;
  line-height: 1.5;
  white-space: pre-wrap;
}

.msg-item.user .msg-bubble {
  background: #2563eb;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.msg-item.assistant .msg-bubble {
  background: #eef2ff;
  color: #1f2937;
  border-bottom-left-radius: 4px;
}

.ai-actions {
  display: flex;
  justify-content: flex-end;
  margin: 4px 0 8px;
}

.ai-input {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 8px;
  align-items: end;
}
</style>
