import type { ApiExecutionRequest, ApiExecutionResult, AuthSession } from "@/types/domain";

export const DEFAULT_API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8081";
export const SESSION_STORAGE_KEY = "appfoodspring-api-session";

export class ApiError extends Error {
  status: number;
  payload: unknown;

  constructor(status: number, message: string, payload: unknown) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.payload = payload;
  }
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null && !Array.isArray(value);
}

function isUser(value: unknown): value is NonNullable<AuthSession["user"]> {
  return (
    isRecord(value) &&
    typeof value.id === "string" &&
    typeof value.email === "string" &&
    typeof value.isActive === "boolean"
  );
}

export function normalizeBaseUrl(baseUrl: string): string {
  const trimmed = baseUrl.trim().replace(/\/+$/, "");
  return trimmed || DEFAULT_API_URL;
}

function resolvePath(path: string, pathParams: Record<string, string> = {}): string {
  return path.replace(/\{([^}]+)\}/g, (_, key: string) => encodeURIComponent(pathParams[key] ?? `{${key}}`));
}

function cleanQuery(query?: Record<string, unknown>): Record<string, string> {
  if (!query) {
    return {};
  }

  return Object.entries(query).reduce<Record<string, string>>((accumulator, [key, value]) => {
    if (value === undefined || value === null || value === "") {
      return accumulator;
    }

    accumulator[key] = typeof value === "string" ? value : JSON.stringify(value);
    return accumulator;
  }, {});
}

async function parseResponse(response: Response): Promise<unknown> {
  if (response.status === 204) {
    return null;
  }

  const contentType = response.headers.get("content-type") ?? "";

  if (contentType.includes("application/json")) {
    return response.json();
  }

  const text = await response.text();
  return text ? text : null;
}

function toErrorMessage(payload: unknown, fallback: string): string {
  if (typeof payload === "string" && payload.trim()) {
    return payload;
  }

  if (isRecord(payload) && typeof payload.message === "string" && payload.message.trim()) {
    return payload.message;
  }

  return fallback;
}

type RequestJsonOptions = {
  path: string;
  method?: "GET" | "POST" | "PATCH" | "DELETE";
  baseUrl?: string;
  query?: Record<string, unknown>;
  body?: unknown;
  session?: AuthSession | null;
  auth?: "public" | "jwt";
};

export async function requestJson<T>({
  path,
  method = "GET",
  baseUrl = DEFAULT_API_URL,
  query,
  body,
  session,
  auth = "jwt",
}: RequestJsonOptions): Promise<T> {
  const normalizedBaseUrl = normalizeBaseUrl(baseUrl);
  const url = new URL(path.replace(/^\//, ""), `${normalizedBaseUrl}/`);

  Object.entries(cleanQuery(query)).forEach(([key, value]) => {
    url.searchParams.set(key, value);
  });

  const headers = new Headers();
  headers.set("Accept", "application/json, text/plain;q=0.9, */*;q=0.8");

  if (body !== undefined) {
    headers.set("Content-Type", "application/json");
  }

  if (auth === "jwt" && session?.accessToken) {
    headers.set("Authorization", `Bearer ${session.accessToken}`);
  }

  const response = await fetch(url.toString(), {
    method,
    headers,
    body: body === undefined ? undefined : JSON.stringify(body),
    cache: "no-store",
  });

  const payload = await parseResponse(response);
  if (!response.ok) {
    throw new ApiError(response.status, toErrorMessage(payload, `HTTP ${response.status}`), payload);
  }

  return payload as T;
}

export async function executeEndpoint(request: ApiExecutionRequest): Promise<ApiExecutionResult> {
  const baseUrl = normalizeBaseUrl(request.baseUrl);
  const resolvedPath = resolvePath(request.endpoint.path, request.pathParams);
  const url = new URL(resolvedPath.replace(/^\//, ""), `${baseUrl}/`);
  const query = cleanQuery(request.query);

  Object.entries(query).forEach(([key, value]) => {
    url.searchParams.set(key, value);
  });

  const headers = new Headers();
  headers.set("Accept", "application/json, text/plain;q=0.9, */*;q=0.8");

  if (request.body !== undefined) {
    headers.set("Content-Type", "application/json");
  }

  if (request.endpoint.auth === "jwt" && request.session?.accessToken) {
    headers.set("Authorization", `Bearer ${request.session.accessToken}`);
  }

  const response = await fetch(url.toString(), {
    method: request.endpoint.method,
    headers,
    body: request.body === undefined ? undefined : JSON.stringify(request.body),
    cache: "no-store",
  });

  const resultHeaders = Object.fromEntries(response.headers.entries());
  const data = await parseResponse(response);

  return {
    ok: response.ok,
    status: response.status,
    url: url.toString(),
    data,
    headers: resultHeaders,
  };
}

export function extractSessionFromResponse(
  data: unknown,
  currentSession?: AuthSession | null,
): AuthSession | null {
  if (!isRecord(data)) {
    return null;
  }

  const accessToken =
    typeof data.accessToken === "string" ? data.accessToken : currentSession?.accessToken;
  const refreshToken =
    typeof data.refreshToken === "string" ? data.refreshToken : currentSession?.refreshToken;

  if (!accessToken || !refreshToken) {
    return null;
  }

  return {
    accessToken,
    refreshToken,
    user: isUser(data.user) ? data.user : currentSession?.user,
  };
}

export function getStoredSession(): AuthSession | null {
  if (typeof window === "undefined") {
    return null;
  }

  const rawValue = window.localStorage.getItem(SESSION_STORAGE_KEY);
  if (!rawValue) {
    return null;
  }

  try {
    const parsed = JSON.parse(rawValue) as unknown;
    if (!isRecord(parsed)) {
      return null;
    }

    if (typeof parsed.accessToken !== "string" || typeof parsed.refreshToken !== "string") {
      return null;
    }

    return {
      accessToken: parsed.accessToken,
      refreshToken: parsed.refreshToken,
      user: isUser(parsed.user) ? parsed.user : null,
    };
  } catch {
    return null;
  }
}

export function saveStoredSession(session: AuthSession): void {
  if (typeof window === "undefined") {
    return;
  }

  window.localStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(session));
}

export function clearStoredSession(): void {
  if (typeof window === "undefined") {
    return;
  }

  window.localStorage.removeItem(SESSION_STORAGE_KEY);
}
