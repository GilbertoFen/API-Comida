"use client";

import { useState } from "react";
import {
  clearStoredSession,
  executeEndpoint,
  extractSessionFromResponse,
  saveStoredSession,
} from "@/services/api";
import type { ApiExecutionResult, AuthSession, EndpointDefinition } from "@/types/domain";

type EndpointCardProps = {
  baseUrl: string;
  endpoint: EndpointDefinition;
  session: AuthSession | null;
  onSessionChange: (session: AuthSession | null) => void;
};

function stringifyTemplate(value: unknown): string {
  if (value === undefined) {
    return "";
  }

  return JSON.stringify(value, null, 2);
}

function parseObjectInput(source: string, label: string): Record<string, unknown> | undefined {
  const trimmed = source.trim();
  if (!trimmed) {
    return undefined;
  }

  const parsed = JSON.parse(trimmed) as unknown;
  if (typeof parsed !== "object" || parsed === null || Array.isArray(parsed)) {
    throw new Error(`${label} debe ser un objeto JSON.`);
  }

  return parsed as Record<string, unknown>;
}

function parseBodyInput(source: string): unknown {
  const trimmed = source.trim();
  if (!trimmed) {
    return undefined;
  }

  return JSON.parse(trimmed) as unknown;
}

function formatJson(value: unknown): string {
  return JSON.stringify(value, null, 2);
}

export function EndpointCard({
  baseUrl,
  endpoint,
  session,
  onSessionChange,
}: EndpointCardProps) {
  const [pathParams, setPathParams] = useState<Record<string, string>>(
    Object.fromEntries(endpoint.pathParams?.map((item) => [item.name, item.value]) ?? []),
  );
  const [queryText, setQueryText] = useState(stringifyTemplate(endpoint.queryTemplate));
  const [bodyText, setBodyText] = useState(stringifyTemplate(endpoint.bodyTemplate));
  const [isRunning, setIsRunning] = useState(false);
  const [error, setError] = useState("");
  const [result, setResult] = useState<ApiExecutionResult | null>(null);

  async function handleRun() {
    try {
      setIsRunning(true);
      setError("");

      const parsedQuery = parseObjectInput(queryText, "Query");
      const parsedBody = parseBodyInput(bodyText);

      const finalBody =
        endpoint.id === "auth.refresh-token" &&
        (!parsedBody || typeof parsedBody !== "object" || parsedBody === null)
          ? { refreshToken: session?.refreshToken ?? "" }
          : endpoint.id === "auth.refresh-token" &&
              typeof parsedBody === "object" &&
              parsedBody !== null &&
              "refreshToken" in parsedBody &&
              (parsedBody.refreshToken === "" || parsedBody.refreshToken === null)
            ? { ...parsedBody, refreshToken: session?.refreshToken ?? "" }
            : parsedBody;

      const execution = await executeEndpoint({
        baseUrl,
        endpoint,
        pathParams,
        query: parsedQuery,
        body: finalBody,
        session,
      });

      setResult(execution);

      const nextSession = extractSessionFromResponse(execution.data, session);
      if (nextSession) {
        saveStoredSession(nextSession);
        onSessionChange(nextSession);
      }

      if (execution.ok && endpoint.id === "auth.logout") {
        clearStoredSession();
        onSessionChange(null);
      }

      if (!execution.ok) {
        setError(`HTTP ${execution.status}`);
      }
    } catch (runError) {
      setError(runError instanceof Error ? runError.message : "No se pudo ejecutar el endpoint.");
      setResult(null);
    } finally {
      setIsRunning(false);
    }
  }

  return (
    <article className="endpoint-card">
      <div className="endpoint-header">
        <div>
          <div className="endpoint-meta">
            <span className={`method-badge method-${endpoint.method.toLowerCase()}`}>
              {endpoint.method}
            </span>
            <span className={endpoint.auth === "jwt" ? "auth-badge auth-jwt" : "auth-badge auth-public"}>
              {endpoint.auth === "jwt" ? "JWT" : "Publico"}
            </span>
          </div>
          <h3>{endpoint.title}</h3>
          <p>{endpoint.description}</p>
        </div>
        <code>{endpoint.path}</code>
      </div>

      {endpoint.notes ? <p className="endpoint-notes">{endpoint.notes}</p> : null}

      {endpoint.pathParams?.length ? (
        <div className="editor-block">
          <span className="editor-label">Path params</span>
          <div className="field-grid">
            {endpoint.pathParams.map((field) => (
              <label key={field.name}>
                <span>{field.name}</span>
                <input
                  value={pathParams[field.name] ?? ""}
                  onChange={(event) =>
                    setPathParams((current) => ({ ...current, [field.name]: event.target.value }))
                  }
                  placeholder={field.value}
                />
              </label>
            ))}
          </div>
        </div>
      ) : null}

      {endpoint.queryTemplate !== undefined ? (
        <div className="editor-block">
          <label>
            <span className="editor-label">Query JSON</span>
            <textarea
              rows={5}
              value={queryText}
              onChange={(event) => setQueryText(event.target.value)}
              spellCheck={false}
            />
          </label>
        </div>
      ) : null}

      {endpoint.bodyTemplate !== undefined ? (
        <div className="editor-block">
          <label>
            <span className="editor-label">Body JSON</span>
            <textarea
              rows={10}
              value={bodyText}
              onChange={(event) => setBodyText(event.target.value)}
              spellCheck={false}
            />
          </label>
        </div>
      ) : null}

      <div className="endpoint-actions">
        <button type="button" onClick={() => void handleRun()} disabled={isRunning}>
          {isRunning ? "Ejecutando..." : "Ejecutar"}
        </button>
        <span className="request-hint">
          {endpoint.auth === "jwt"
            ? session?.accessToken
              ? "Usara el JWT guardado"
              : "Requiere login previo"
            : "No requiere token"}
        </span>
      </div>

      {error ? <p className="message error">{error}</p> : null}

      {result ? (
        <div className="response-panel">
          <div className="response-head">
            <strong>{result.ok ? "Respuesta" : "Respuesta con error"}</strong>
            <span>
              HTTP {result.status} · {result.url}
            </span>
          </div>
          <pre>{formatJson(result.data)}</pre>
        </div>
      ) : null}
    </article>
  );
}
