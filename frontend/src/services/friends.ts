import api from './api'
import type { FriendRequest, User } from '@/types'

export const sendFriendRequest = async (receiverId: string): Promise<string> => {
  const response = await api.post('/friends/requests', { receiverId })
  return response.data
}

export const getPendingRequests = async (): Promise<FriendRequest[]> => {
  const response = await api.get('/friends/requests')
  return response.data
}

export const respondToRequest = async (requestId: string, accept: boolean): Promise<string> => {
  const response = await api.put(`/friends/requests/${requestId}?accept=${accept}`)
  return response.data
}

export const getFriends = async (): Promise<User[]> => {
  const response = await api.get('/friends')
  return response.data
}

export const removeFriend = async (friendId: string): Promise<string> => {
  const response = await api.delete(`/friends/${friendId}`)
  return response.data
}
