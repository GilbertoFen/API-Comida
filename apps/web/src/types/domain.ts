export type HttpMethod = "GET" | "POST" | "PATCH" | "DELETE";

export type EndpointAuthMode = "public" | "jwt";

export type EndpointField = {
  name: string;
  value: string;
  required?: boolean;
  hint?: string;
};

export type EndpointDefinition = {
  id: string;
  title: string;
  description: string;
  method: HttpMethod;
  path: string;
  auth: EndpointAuthMode;
  notes?: string;
  pathParams?: EndpointField[];
  queryTemplate?: Record<string, unknown>;
  bodyTemplate?: unknown;
};

export type EndpointGroup = {
  id: string;
  title: string;
  description: string;
  endpoints: EndpointDefinition[];
};

export type AuthSession = {
  accessToken: string;
  refreshToken: string;
  user?: unknown;
};

export type ApiExecutionRequest = {
  baseUrl: string;
  endpoint: EndpointDefinition;
  pathParams?: Record<string, string>;
  query?: Record<string, unknown>;
  body?: unknown;
  session?: AuthSession | null;
};

export type ApiExecutionResult = {
  ok: boolean;
  status: number;
  url: string;
  data: unknown;
  headers: Record<string, string>;
};
