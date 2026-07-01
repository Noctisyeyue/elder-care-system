<template>
  <ScreenLock />
  <el-drawer
    v-model="chatVisible"
    title="Chat Bot"
    direction="rtl"
    :size="600"
    class="chat-drawer"
  >
    <div class="chat-container">
      <div class="chat-body" ref="chatBodyRef">
        <div v-for="(msg, idx) in messages" :key="idx" :class="['message', msg.role]">
          <el-avatar :size="32" :src="msg.avatar" class="chat-avatar" />
          <div class="message-content">
            <div class="message-bubble" v-html="renderContent(msg.content)"></div>
          </div>
        </div>
      </div>
      <div class="chat-footer">
        <el-input
          v-model="newMessage"
          placeholder="输入消息..."
          :disabled="sending"
          @keyup.enter="sendMessage"
        />
        <el-button
          type="primary"
          :loading="sending"
          :disabled="sending || !newMessage.trim()"
          @click="sendMessage"
        >
          发送
        </el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, onUnmounted } from 'vue'
import { renderContent } from '@/utils/contentRenderer'
import { streamPost } from '@/utils/request'
import { getUserAvatar } from '@/api/user'
import { mittBus } from '@/utils/mitt'
import ScreenLock from '@/components/layouts/screen-lock/index.vue'

defineOptions({ name: 'GlobalComponent' })

interface ChatMessage {
  role: 'user' | 'bot'
  content: string
  avatar: string
}

const botAvatarUrl = new URL('@/assets/AIBot.svg', import.meta.url).href
const defaultAvatar = 'https://img95.699pic.com/element/40112/2503.png_300.png'

const chatVisible = ref(false)
const newMessage = ref('')
const sending = ref(false)
const avatarUrl = ref(defaultAvatar)
const chatBodyRef = ref<HTMLElement>()
const messages = ref<ChatMessage[]>([
  {
    role: 'bot',
    content: '你好！我是你的AI助手，有什么可以帮你的吗？',
    avatar: botAvatarUrl,
  },
])

function scrollToBottom() {
  nextTick(() => {
    if (chatBodyRef.value) {
      chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight
    }
  })
}

async function sendMessage() {
  const messageContent = newMessage.value.trim()
  if (!messageContent) return

  messages.value.push({
    role: 'user',
    content: messageContent,
    avatar: avatarUrl.value,
  })
  sending.value = true
  newMessage.value = ''

  const botMsg: ChatMessage = { role: 'bot', content: '', avatar: botAvatarUrl }
  messages.value.push(botMsg)

  try {
    await streamPost(
      '/chatStream',
      messageContent,
      (chunk) => {
        botMsg.content += chunk
        messages.value = [...messages.value]
        scrollToBottom()
      },
      () => {},
      (error) => {
        botMsg.content += `\n[出错了，请稍后重试: ${error.message}]`
        messages.value = [...messages.value]
        scrollToBottom()
      },
    )
  } catch {
    botMsg.content += '\n[出错了，请稍后重试]'
    messages.value = [...messages.value]
    scrollToBottom()
  } finally {
    sending.value = false
    scrollToBottom()
  }
}

function openChat() {
  chatVisible.value = true
}

onMounted(async () => {
  mittBus.on('openChat', openChat)
  try {
    avatarUrl.value = await getUserAvatar()
  } catch {
    /* default */
  }
})

onUnmounted(() => {
  mittBus.off('openChat', openChat)
})
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 15px 10px;
}

.chat-footer {
  display: flex;
  align-items: center;
  padding: 8px;
  border-top: 1px solid var(--default-border);
  gap: 10px;
}

.message {
  display: flex;
  margin-bottom: 15px;
  align-items: flex-start;
}

.message .message-content {
  max-width: 75%;
}

.message .message-bubble {
  padding: 8px 12px;
  border-radius: 8px;
  background: var(--ui-gray-100);
  line-height: 1.5;
  font-size: 14px;
  word-wrap: break-word;
}

.message.user {
  flex-direction: row-reverse;
}

.message.user .chat-avatar {
  margin-left: 12px;
}

.message.user .message-bubble {
  background: var(--theme-color);
  color: #fff;
}

.message.bot .chat-avatar {
  margin-right: 12px;
}
</style>
