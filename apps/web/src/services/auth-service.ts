import {
  clearStoredSession,
  DEFAULT_API_URL,
  requestJson,
  saveStoredSession,
} from "@/services/api-client";
import type {
  AuthResponse,
  AuthSession,
  LoginRequest,
  RegisterRequest,
  User,
} from "@/types/domain";

function toSession(response: AuthResponse): AuthSession {
  return {
    accessToken: response.accessToken,
    refreshToken: response.refreshToken,
    user: response.user,
  };
}

export async function login(payload: LoginRequest, baseUrl = DEFAULT_API_URL): Promise<AuthSession> {
  const response = await requestJson<AuthResponse>({
    path: "/auth/login",
    method: "POST",
    baseUrl,
    auth: "public",
    body: payload,
  });

  const session = toSession(response);
  saveStoredSession(session);
  return session;
}

export async function register(payload: RegisterRequest, baseUrl = DEFAULT_API_URL): Promise<AuthSession> {
  const response = await requestJson<AuthResponse>({
    path: "/auth/register",
    method: "POST",
    baseUrl,
    auth: "public",
    body: payload,
  });

  const session = toSession(response);
  saveStoredSession(session);
  return session;
}

export async function refreshSession(
  session: AuthSession,
  baseUrl = DEFAULT_API_URL,
): Promise<AuthSession> {
  const response = await requestJson<AuthResponse>({
    path: "/auth/refresh-token",
    method: "POST",
    baseUrl,
    auth: "public",
    body: {
      refreshToken: session.refreshToken,
    },
  });

  const nextSession = toSession(response);
  saveStoredSession(nextSession);
  return nextSession;
}

export async function logout(session: AuthSession, baseUrl = DEFAULT_API_URL): Promise<void> {
  await requestJson<{ message: string }>({
    path: "/auth/logout",
    method: "POST",
    baseUrl,
    session,
    auth: "jwt",
  });
  clearStoredSession();
}

export async function getAuthenticatedUser(
  session: AuthSession,
  baseUrl = DEFAULT_API_URL,
): Promise<User> {
  return requestJson<User>({
    path: "/auth/me",
    method: "GET",
    baseUrl,
    session,
    auth: "jwt",
  });
}
