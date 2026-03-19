import api from "./api";
import type { User } from "@/types";

export const fetchProfile = async (): Promise<User> => {
  const response = await api.get("/users/me");
  return response.data;
};

export const updateProfile = async (user: User): Promise<User> => {
  const response = await api.put("/users/me", user);
  return response.data;
};

export const searchUsers = async (query: string): Promise<User[]> => {
  const response = await api.get(`/users?query=${encodeURIComponent(query)}`);
  return response.data;
};

export const getUserById = async (id: string): Promise<User> => {
  const response = await api.get(`/users/${id}`);
  return response.data;
};
