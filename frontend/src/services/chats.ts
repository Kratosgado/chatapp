import api from "./api";
import type { ChatRoom, Message } from "@/types";

export const getChats = async (): Promise<ChatRoom[]> => {
  const response = await api.get("/chats");
  return response.data;
};

export const createChat = async (
  participantIds: string[],
  name?: string,
): Promise<ChatRoom> => {
  const response = await api.post("/chats", { participantIds, name });
  return response.data;
};

export const getMessages = async (
  chatId: string,
  page = 0,
  size = 20,
): Promise<Message[]> => {
  const response = await api.get(
    `/chats/${chatId}/messages?page=${page}&size=${size}`,
  );
  return response.data;
};

export const sendMessage = async (
  chatId: string,
  content: string,
): Promise<Message> => {
  const response = await api.post(`/chats/${chatId}/messages`, { content });
  return response.data;
};
