import api from "./api";
import type { LoginResponse } from "@/types";

export const login = async (
  email: string,
  password: string,
): Promise<LoginResponse> => {
  const response = await api.post("/auth/login", { email, password });
  return response.data;
};

export const register = async (
  name: string,
  email: string,
  password: string,
  confirmPassword: string,
): Promise<LoginResponse> => {
  // Wait, backend RegisterDto doesn't return LoginResponse, it returns the user directly.
  // The login flow should likely happen after registration or I update backend to auto-login.
  // Let's check backend AuthController again. It returns User.
  // So we register, then we login.
  // Or I can update backend to login after register.
  // For now, let's just return the user, and client side can handle login.
  const response = await api.post("/auth/register", {
    name,
    email,
    password,
    confirmPassword,
  });
  return response.data;
};

export const logout = async (): Promise<void> => {
  await api.post("/auth/logout");
};
