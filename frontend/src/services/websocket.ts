import { Client } from '@stomp/stompjs'

const client = new Client({
  brokerURL: 'ws://localhost:8080/ws/websocket', // Standard WebSocket URL for SockJS endpoint
  // connectionTimeout: 10000,
  reconnectDelay: 5000,
  heartbeatIncoming: 4000,
  heartbeatOutgoing: 4000,
})

client.onConnect = (frame) => {
  console.log('Connected: ' + frame)
}

client.onStompError = (frame) => {
  console.error('Broker reported error: ' + frame.headers['message'])
  console.error('Additional details: ' + frame.body)
}

export const connect = () => {
  if (!client.active) {
    client.activate()
  }
}

export const disconnect = () => {
  if (client.active) {
    client.deactivate()
  }
}

export const subscribe = (destination: string, callback: (message: any) => void) => {
  if (!client.active) {
    console.warn('Client not active, cannot subscribe')
    return null
  }
  return client.subscribe(destination, (message) => {
    callback(JSON.parse(message.body))
  })
}

export const sendMessage = (destination: string, body: any) => {
  if (!client.active) {
    console.warn('Client not active, cannot send')
    return
  }
  client.publish({
    destination,
    body: JSON.stringify(body),
  })
}

export default client
