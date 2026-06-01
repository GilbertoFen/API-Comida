"use client";

import { useEffect, useState } from "react";
import { EndpointCard } from "@/components/endpoint-card";
import { DEFAULT_API_URL, endpointGroups, getStoredSession, normalizeBaseUrl } from "@/services/api";
import type { AuthSession } from "@/types/domain";

function maskToken(token: string): string {
  if (token.length <= 20) {
    return token;
  }

  return `${token.slice(0, 10)}...${token.slice(-10)}`;
}

export function Dashboard() {
  const [baseUrl, setBaseUrl] = useState(DEFAULT_API_URL);
  const [session, setSession] = useState<AuthSession | null>(null);

  useEffect(() => {
    setSession(getStoredSession());
  }, []);

  const totalEndpoints = endpointGroups.reduce((sum, group) => sum + group.endpoints.length, 0);

  return (
    <main className="page-shell">
      <section className="hero-panel">
        <div className="hero-copy">
          <span className="eyebrow">Frontend de prueba</span>
          <h1>Consola simple para probar AppFoodSpring desde Next.js.</h1>
          <p>
            Esta vista reemplaza Postman y los archivos `.http` para validaciones rapidas desde el
            frontend. Cada bloque representa endpoints del backend en el orden recomendado de uso.
          </p>
        </div>

        <aside className="session-panel">
          <h2>Conexion activa</h2>
          <label className="base-url-field">
            <span>Base URL del backend</span>
            <input
              value={baseUrl}
              onChange={(event) => setBaseUrl(event.target.value)}
              placeholder="http://localhost:8081"
            />
          </label>
          <p className="status-line">
            Resuelta como <code>{normalizeBaseUrl(baseUrl)}</code>
          </p>
          <div className="session-grid">
            <article>
              <strong>{endpointGroups.length}</strong>
              <span>listas de prueba</span>
            </article>
            <article>
              <strong>{totalEndpoints}</strong>
              <span>endpoints cargados</span>
            </article>
            <article>
              <strong>{session ? "si" : "no"}</strong>
              <span>sesion JWT</span>
            </article>
          </div>
          <div className="token-panel">
            <span className="token-label">Access token</span>
            <code>{session?.accessToken ? maskToken(session.accessToken) : "Sin token guardado"}</code>
          </div>
          <div className="token-panel">
            <span className="token-label">Refresh token</span>
            <code>{session?.refreshToken ? maskToken(session.refreshToken) : "Sin refresh token"}</code>
          </div>
        </aside>
      </section>

      <section className="guide-panel">
        <h2>Orden sugerido</h2>
        <ol>
          <li>Haz `register` o `login` en la primera lista para guardar JWT automaticamente.</li>
          <li>Completa `users/me` y `onboarding` para dejar contexto del usuario.</li>
          <li>Prueba catalogos (`foods`, `exercises`) y usa sus UUIDs en las siguientes tarjetas.</li>
          <li>Crea recetas, luego food logs, refri, meal plans y workout plans.</li>
          <li>Deja IA al final porque normalmente depende del resto de la informacion.</li>
        </ol>
      </section>

      <section className="group-stack">
        {endpointGroups.map((group) => (
          <article key={group.id} className="group-panel">
            <div className="group-heading">
              <div>
                <h2>{group.title}</h2>
                <p>{group.description}</p>
              </div>
              <span className="group-count">{group.endpoints.length} endpoints</span>
            </div>

            <div className="endpoint-list">
              {group.endpoints.map((endpoint) => (
                <EndpointCard
                  key={endpoint.id}
                  baseUrl={baseUrl}
                  endpoint={endpoint}
                  session={session}
                  onSessionChange={setSession}
                />
              ))}
            </div>
          </article>
        ))}
      </section>
    </main>
  );
}
