import api from "./api";
import type { ChatRoom, Message } from "@/types";

export const getChats = async (): Promise<ChatRoom[]> => {
  const response = await api.get("/chats");
  return response.data.data;
};

export const createChat = async (
  participants: string[],
  name?: string,
): Promise<ChatRoom> => {
  const response = await api.post("/chats", { participants, name });
  return response.data.data;
};

export const getMessages = async (
  chatId: string,
  page = 0,
  size = 20,
): Promise<Message[]> => {
  const response = await api.get(
    `/chats/${chatId}/messages?page=${page}&size=${size}`,
  );
  return response.data.data;
};

export const sendMessage = async (
  chatId: string,
  content: string,
): Promise<Message> => {
  const response = await api.post(`/chats/${chatId}/messages`, { content });
  return response.data.data;
};
