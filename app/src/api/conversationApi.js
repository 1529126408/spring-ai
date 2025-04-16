import axios from 'axios';

// API基础URL
const API_BASE_URL = '/api/ai';

/**
 * 对话API服务，封装与后端的通信
 */
export default {
  /**
   * 创建新对话
   * @param {string} title 对话标题（可选）
   * @returns {Promise} 创建的对话信息
   */
  createConversation(title) {
    return axios.post(`${API_BASE_URL}/conversations`, null, {
      params: { title }
    });
  },

  /**
   * 获取所有对话列表
   * @returns {Promise} 对话列表
   */
  getConversations() {
    return axios.get(`${API_BASE_URL}/conversations`);
  },

  /**
   * 获取指定对话的详细信息
   * @param {string} conversationId 对话ID
   * @returns {Promise} 对话详细信息
   */
  getConversation(conversationId) {
    return axios.get(`${API_BASE_URL}/conversations/${conversationId}`);
  },

  /**
   * 删除指定对话
   * @param {string} conversationId 对话ID
   * @returns {Promise} 操作结果
   */
  deleteConversation(conversationId) {
    return axios.delete(`${API_BASE_URL}/conversations/${conversationId}`);
  },

  /**
   * 发送消息并获取AI回复（非流式）
   * @param {string} conversationId 对话ID（可选，如果为空则创建新对话）
   * @param {string} content 消息内容
   * @returns {Promise} AI回复消息
   */
  sendMessage(conversationId, content) {
    return axios.post(`${API_BASE_URL}/messages`, {
      conversationId,
      content
    });
  },

  /**
   * 发送消息并获取流式AI回复
   * @param {string} conversationId 对话ID（可选，如果为空则创建新对话）
   * @param {string} content 消息内容
   * @returns {Promise} 流式AI回复
   */
  sendMessageStream(conversationId, content) {
    return axios.post(`${API_BASE_URL}/messages/stream`, {
      conversationId,
      content
    }, {
      responseType: 'stream'
    });
  }
};