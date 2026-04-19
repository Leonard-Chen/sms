import request from '../utils/request'

export const chatWithAi = (payload) => {
  return request.post('/api/ai/chat', payload)
}

export const aiHealth = () => {
  return request.get('/api/ai/health')
}
