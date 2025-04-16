<script setup>
import {ref, reactive, onMounted, nextTick, watch, defineComponent} from 'vue'
import {NModal, NIcon, NButton, NInput, NList, NListItem, NEmpty, NSpace, NCard, NPopconfirm, useMessage} from 'naive-ui'
import conversationApi from '@/api/conversationApi'
import { marked } from 'marked'
import axios from 'axios'

// 配置marked选项，防止XSS攻击
        marked.setOptions({
    sanitize: true,
            sanitizer: function (text) {
        return text
    }
})

        const message = useMessage()
const showModal = ref(false)
const modalContent = ref('<h2>暂无内容</h2>');
const headerContent = ref('AI 聊天助手');
const footerContent = ref('© 2025 AI 聊天助手');
const handleClose = () => {
showModal.value = false
        }

// 对话管理
        const conversations = ref([])
const currentConversation = ref(null)
const loading = ref(false)

// 消息容器引用
const messagesContainerRef = ref(null)
// 内容容器引用
const contentRef = ref(null)

const scrollToBottom = () => {
        contentRef.value.scrollTo({ top: document.getElementById('chat-container').scrollHeight, behavior: 'smooth' })
        }

// 初始化时加载对话列表
onMounted(async () => {
        try {
await loadConversations()
// 初始化完成后滚动到底部
scrollToBottom()
  } catch (error) {
        message.error('加载对话列表失败：' + (error.response?.data?.message || error.message))
        }
        })

// 监听当前对话的消息变化，自动滚动到底部
watch(() => currentConversation.value?.messages, () => {
scrollToBottom()
}, { deep: true })

// 加载对话列表
        const loadConversations = async () => {
        try {
loading.value = true
        const response = await conversationApi.getConversations()
conversations.value = response.data.conversations || []

        // 如果有对话，选择第一个作为当前对话
        if (conversations.value.length > 0) {
switchConversation(conversations.value[0])
    } else {
// 如果没有对话，创建一个默认对话
await createNewConversation('欢迎使用AI助手')
    }
            } catch (error) {
        console.error('加载对话列表失败', error)
    throw error
  } finally {
loading.value = false
        }
        }

// 创建新对话
        const createNewConversation = async (title) => {
        try {
loading.value = true
        const response = await conversationApi.createConversation(title)
    const newConversation = response.data

// 确保conversations数组已初始化
    if (!conversations.value) {
conversations.value = []
        }

        // 将所有对话设为非活跃
        conversations.value.forEach(conv => {
    if (conv) conv.active = false
})

// 添加新对话并设为活跃
newConversation.active = true
        conversations.value.push(newConversation)
currentConversation.value = newConversation
    
    return newConversation
  } catch (error) {
        message.error('创建新对话失败：' + (error.response?.data?.message || error.message))
        console.error('创建新对话失败', error)
    throw error
  } finally {
loading.value = false
        }
        }

// 切换当前对话
        const switchConversation = async (conversation) => {
        if (!conversation) return

        try {
        // 确保conversations数组已初始化
        if (!conversations.value) {
conversations.value = []
        }

        // 先从本地切换
        conversations.value.forEach(conv => {
    if (conv) conv.active = false
})
conversation.active = true

        // 如果对话没有完整的消息历史，则从服务器获取完整对话
        if (!conversation.messages || conversation.messages.length === 0) {
        const response = await conversationApi.getConversation(conversation.id)
// 更新对话的完整信息，包括消息历史
      Object.assign(conversation, response.data)
    }

currentConversation.value = conversation
    // 切换对话后滚动到底部
scrollToBottom()
  } catch (error) {
        message.error('切换对话失败：' + (error.response?.data?.message || error.message))
        console.error('切换对话失败', error)
  }
          }

// 删除对话
          const deleteConversation = async (conversation) => {
        try {
loading.value = true
// 调用API删除对话
await conversationApi.deleteConversation(conversation.id)

// 从本地列表中移除
    const index = conversations.value.findIndex(c => c.id === conversation.id)
    if (index !== -1) {
        conversations.value.splice(index, 1)

// 如果删除的是当前对话，则切换到第一个对话
      if (currentConversation.value && currentConversation.value.id === conversation.id) {
        if (conversations.value.length > 0) {
await switchConversation(conversations.value[0])
        } else {
// 如果没有对话了，创建一个新对话
await createNewConversation('欢迎使用AI助手')
        }
                }
                }

                message.success('对话已删除')
  } catch (error) {
        message.error('删除对话失败：' + (error.response?.data?.message || error.message))
        console.error('删除对话失败', error)
  } finally {
loading.value = false
        }
        }

// 消息输入和发送
        const inputMessage = ref('')

// 发送消息
const sendMessage = async () => {
        if (!inputMessage.value.trim() || loading.value || !currentConversation.value) return

        const messageContent = inputMessage.value.trim()
// 清空输入
inputMessage.value = ''

        try {
loading.value = true

        // 先在UI上添加用户消息
        if (!currentConversation.value.messages) {
currentConversation.value.messages = []
        }

        currentConversation.value.messages.push({
    role: 'user',
            content: messageContent,
            timestamp: new Date()
})

        // 先添加一个空的AI回复消息到列表中，用于流式更新内容
        const assistantMessage = {
role: 'assistant',
content: '',
timestamp: new Date()
    }
            currentConversation.value.messages.push(assistantMessage)

// 消息添加后滚动到底部
scrollToBottom()

// 调用API发送消息并获取流式回复
    try {
            // 创建请求URL和请求体
            const url = `/api/ai/messages/stream`
        const requestBody = {
conversationId: currentConversation.value.id,
content: messageContent
      }

              // 使用 fetch API 实现流式响应处理
              const response = await fetch(url, {
    method: 'POST',
            headers: {
        'Content-Type': 'application/json',
                'Accept': 'text/event-stream',
                'Cache-Control': 'no-cache'
    },
    body: JSON.stringify(requestBody)
})

        if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`)
        }

        // 获取响应的可读流
        const reader = response.body.getReader()
      const decoder = new TextDecoder('utf-8')
let buffer = ''

// 处理流式数据
      while (true) {
              const { done, value } = await reader.read()
        if (done) break

        // 解码二进制数据为文本
        const chunk = decoder.decode(value, { stream: true })
buffer += chunk

// 处理SSE格式的数据
// SSE格式通常是以 "data: {json数据}\n\n" 的形式发送的
        const lines = buffer.split('\n\n')

// 保留最后一个可能不完整的行
        const lastLine = lines.pop()
buffer = lastLine || ''

        for (let line of lines) {
        try {
        // 提取并解析JSON数据
        const jsonStr = line.substring(5).trim() // 去掉 "data: " 前缀
              const data = JSON.parse(jsonStr)
              if (data.results[0].metadata.finishReason && data.results[0].metadata.finishReason === 'STOP') {
        return;
        }
        // 从Spring AI的ChatResponse中提取文本内容
        if (data.results[0] && data.results[0].output && data.results[0].output.text) {
        // 获取新的文本片段
        const newText = data.results[0].output.text

// 将新文本添加到现有内容
assistantMessage.content += newText

    // 每次更新后滚动到底部
scrollToBottom()
              }
                      } catch (error) {
        console.error('解析SSE数据失败', error, line)
            }

                    }
                    console.log(assistantMessage.content)
      }

              console.log('流式请求完成')
scrollToBottom()
    } catch (error) {
        message.error('API请求失败：' + (error.response?.data?.message || error.message))
        console.error('API请求失败', error)

// 更新AI消息为错误状态
assistantMessage.content = '消息发送失败，请重试。'
assistantMessage.isError = true
        }
        } catch (error) {
        message.error('发送消息失败：' + (error.response?.data?.message || error.message))
        console.error('发送消息失败', error)

// 在发送失败时，更新已添加的AI消息为错误消息
    const lastMessage = currentConversation.value.messages[currentConversation.value.messages.length - 1]
        if (lastMessage && lastMessage.role === 'assistant') {
lastMessage.content = '消息发送失败，请重试。'
lastMessage.isError = true
        } else {
        // 如果没有找到AI消息（不应该发生），则添加一条新的错误消息
        currentConversation.value.messages.push({
    role: 'assistant',
            content: '消息发送失败，请重试。',
            timestamp: new Date(),
            isError: true
})
        }
        } finally {
loading.value = false
        }
        }

// 处理按键事件
        const handleKeyPress = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault()
sendMessage()
  }
          }
</script>

<template style="height: 100%">
  <NModal
v-model:show="showModal"
closable @on-after-leave=""
        :mask-closable="false"
preset="dialog"
title="公告详情"
positive-text="我已知晓"
@positive-click="handleClose"
        >
    <div v-html="modalContent"></div>
  </NModal>

  <n-layout style="height: 100%">
    <n-layout-header style="height: 64px; padding: 24px" bordered>
      <img
src="@/assets/icons/favicon.svg"
style="width: 24px; height: 24px; vertical-align: middle; margin-right: 8px;"
alt="">
        {{headerContent}}
    </n-layout-header>
    <n-layout position="absolute" style="top: 64px;" has-sider>
      <n-layout-sider
content-style="padding: 24px;"
        :native-scrollbar="false"
bordered
      >
        <div class="sidebar-content">
          <n-space vertical size="large" style="width: 100%">
            <n-button
        type="primary"
block
@click="() => createNewConversation()"
        :loading="loading"
        :disabled="loading"
        >
              <template #icon>
                <n-icon>
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24">
                    <path fill="currentColor" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6z"/>
                  </svg>
                </n-icon>
              </template>
新建对话
        </n-button>
            
            <div class="conversation-list">
              <n-list v-if="conversations.length > 0" hoverable clickable>
                <n-list-item
v-for="conversation in conversations"
        :key="conversation.id"
        :class="{ 'active-conversation': conversation.active }"
@click="switchConversation(conversation)"
        >
                  <n-space justify="space-between" align="center" style="width: 100%">
                    <span class="conversation-title">{{ conversation.title }}</span>
                    <n-popconfirm
v-if="conversations.length > 1"
@positive-click="() => deleteConversation(conversation)"
negative-text="取消"
positive-text="确认"
        >
                      <template #trigger>
                        <n-button quaternary circle size="small" @click.stop :disabled="loading">
                          <n-icon>
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24">
                              <path fill="currentColor" d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
                            </svg>
                          </n-icon>
                        </n-button>
                      </template>
确定要删除此对话吗？
                    </n-popconfirm>
                  </n-space>
                </n-list-item>
              </n-list>
              <n-empty v-else description="暂无对话" />
            </div>
          </n-space>
        </div>
      </n-layout-sider>
      <n-layout ref="contentRef" content-style="padding: 24px;" :native-scrollbar="false">
        <div class="chat-container" id="chat-container">
          <div v-if="loading && (!currentConversation || !currentConversation.messages || currentConversation.messages.length === 0)" class="loading-container">
            <n-spin size="large" />
            <p>加载中...</p>
          </div>
          <div class="messages-container" ref="messagesContainerRef" v-else-if="currentConversation && currentConversation.messages && currentConversation.messages.length > 0">
            <n-card v-for="(message, index) in currentConversation.messages"
        :key="index"
        :class="['message-card', message.role === 'user' ? 'user-message' : 'ai-message', message.isError ? 'error-message' : '']"
size="small"
style="margin-bottom: 16px;">
              <template #header>
                <div class="message-header">
        {{ message.role === 'user' ? '用户' : 'AI' }}
                </div>
              </template>
              <div class="markdown-content" v-html="message.role === 'assistant' ? marked.parse(message.content) : message.content"></div>
            </n-card>
          </div>
          <n-empty v-else description="开始新的对话吧！" />
        </div>

        <n-layout-footer
        position="absolute"
style="height: 100px; padding: 24px"
bordered
        >
          <div class="input-container">
            <n-input
        type="textarea"
placeholder="输入您的问题..."
        :autosize="{ minRows: 1, maxRows: 3 }"
v-model:value="inputMessage"
@input="(e) => inputMessage = e"
@keypress="handleKeyPress"
        :disabled="loading"
        />
            <n-button type="primary" @click="sendMessage" :loading="loading" :disabled="!inputMessage.trim() || loading">
发送
        </n-button>
          </div>
          <div class="footer-text">
<b>{{ footerContent }}</b>
          </div>
        </n-layout-footer>
      </n-layout>
    </n-layout>
  </n-layout>

</template>

<style scoped>
.sidebar-content {
    height: 100%;
    display: flex;
    flex-direction: column;
}

.conversation-list {
    flex: 1;
    overflow-y: auto;
    margin-top: 10px;
}

.conversation-title {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 150px;
}

.active-conversation {
    background-color: rgba(0, 128, 255, 0.1);
    font-weight: bold;
}

.chat-container {
    height: calc(100% - 100px);
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    padding-bottom: 120px; /* 增加底部padding，确保内容不被footer遮挡 */
}

.loading-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    color: #999;
}

.messages-container {
    flex: 1;
    padding: 10px 0 20px 0; /* 增加底部padding */
}

.message-card {
    max-width: 80%;
    margin-bottom: 16px;
}

.user-message {
    align-self: flex-end;
    margin-left: auto;
    background-color: #e6f7ff;
}

.ai-message {
    align-self: flex-start;
    margin-right: auto;
    background-color: #f5f5f5;
}

.error-message {
    background-color: #fff2f0;
    border: 1px solid #ffccc7;
}

.markdown-content {
    line-height: 1.6;
}

.markdown-content :deep(p) {
    margin: 1em 0;
}

.markdown-content :deep(pre) {
    background-color: #f6f8fa;
    padding: 1em;
    border-radius: 4px;
    overflow-x: auto;
}

.markdown-content :deep(code) {
    background-color: rgba(175, 184, 193, 0.2);
    padding: 0.2em 0.4em;
    border-radius: 4px;
    font-family: monospace;
}

.markdown-content :deep(ul), .markdown-content :deep(ol) {
    padding-left: 2em;
}

.markdown-content :deep(blockquote) {
    margin: 1em 0;
    padding-left: 1em;
    border-left: 4px solid #ddd;
    color: #666;
}
.message-header {
    font-weight: bold;
    font-size: 0.9em;
    color: #666;
}

.input-container {
    display: flex;
    gap: 10px;
    margin-bottom: 10px;
}

.input-container .n-input {
    flex: 1;
}

.footer-text {
    text-align: center;
    font-size: 0.8em;
    color: #999;
}
</style>