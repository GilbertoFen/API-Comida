"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { BrandMark } from "./brand-mark";
import {
  AI_COACH_NAME,
  getActiveSession,
  loginWithBackend,
  readPreferredApiUrl,
  registerWithBackend,
  resolvePostAuthDestination,
  writePreferredApiUrl,
} from "./backend";
import styles from "./product-shell.module.css";
import {
  ArrowRightIcon,
  BarChartIcon,
  CheckCircleIcon,
  GlobeIcon,
  PhoneIcon,
  ShieldIcon,
  SparkIcon,
  UtensilsIcon,
} from "./icons";
import { Reveal } from "./reveal";

type AuthMode = "login" | "register";

const productHighlights = [
  {
    title: "Registro de comidas y progreso",
    description:
      "Logs nutricionales, calorias diarias y contexto fisico en una misma capa clara.",
    icon: UtensilsIcon,
  },
  {
    title: "Planes, recetas y refrigerador",
    description:
      "La experiencia conecta tus objetivos con recetas, ingredientes y decisiones rapidas.",
    icon: BarChartIcon,
  },
  {
    title: `${AI_COACH_NAME} como asistente futura`,
    description:
      "El flujo ya deja lista la base de datos para consejos, coaching y seguimiento inteligente.",
    icon: SparkIcon,
  },
];

const journeySteps = [
  {
    title: "Autenticacion segura",
    description: "JWT persistente, refresco de sesion y rutas protegidas desde el primer acceso.",
    icon: ShieldIcon,
  },
  {
    title: "Onboarding obligatorio",
    description: "Si falta el cuestionario, el flujo lleva al usuario a completarlo antes de entrar.",
    icon: CheckCircleIcon,
  },
  {
    title: "Dashboard con datos reales",
    description: "El home se conecta a perfil, logs, ejercicios, recetas y refrigerador del backend.",
    icon: GlobeIcon,
  },
];

export function LandingScreen() {
  const router = useRouter();
  const [mode, setMode] = useState<AuthMode>("login");
  const [baseUrl, setBaseUrl] = useState(readPreferredApiUrl);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [assistantOpen, setAssistantOpen] = useState(false);
  const [loginForm, setLoginForm] = useState({
    email: "demo@appfoodspring.com",
    password: "Password123!",
  });
  const [registerForm, setRegisterForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  useEffect(() => {
    writePreferredApiUrl(baseUrl);
  }, [baseUrl]);

  function scrollToId(id: string) {
    document.getElementById(id)?.scrollIntoView({ behavior: "smooth", block: "start" });
  }

  function validateCurrentMode(): string | null {
    if (mode === "login") {
      if (!loginForm.email.trim()) {
        return "El email es obligatorio.";
      }
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(loginForm.email)) {
        return "Ingresa un email valido.";
      }
      if (!loginForm.password) {
        return "La contrasena es obligatoria.";
      }
      return null;
    }

    if (!registerForm.firstName.trim() || !registerForm.lastName.trim()) {
      return "Nombre y apellido son obligatorios.";
    }
    if (!registerForm.email.trim()) {
      return "El email es obligatorio.";
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerForm.email)) {
      return "Ingresa un email valido.";
    }
    if (registerForm.password.length < 8) {
      return "La contrasena debe tener al menos 8 caracteres.";
    }
    if (registerForm.password !== registerForm.confirmPassword) {
      return "La confirmacion de contrasena no coincide.";
    }

    return null;
  }

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);

    const validationError = validateCurrentMode();
    if (validationError) {
      setError(validationError);
      return;
    }

    setBusy(true);

    try {
      const resolvedBaseUrl = writePreferredApiUrl(baseUrl);
      const session =
        mode === "login"
          ? await loginWithBackend(resolvedBaseUrl, loginForm)
          : await registerWithBackend(resolvedBaseUrl, {
              firstName: registerForm.firstName.trim(),
              lastName: registerForm.lastName.trim(),
              email: registerForm.email.trim(),
              password: registerForm.password,
            });

      const resolution = await resolvePostAuthDestination(resolvedBaseUrl, session);
      router.push(resolution.destination);
    } catch (submitError) {
      setError(
        submitError instanceof Error
          ? submitError.message
          : "No fue posible completar la autenticacion.",
      );
    } finally {
      setBusy(false);
    }
  }

  async function handleResumeSession() {
    const session = getActiveSession();
    if (!session) {
      scrollToId("auth");
      return;
    }

    setBusy(true);
    setError(null);

    try {
      const resolution = await resolvePostAuthDestination(baseUrl, session);
      router.push(resolution.destination);
    } catch (sessionError) {
      setError(sessionError instanceof Error ? sessionError.message : "No se pudo retomar la sesion.");
    } finally {
      setBusy(false);
    }
  }

  return (
    <main className={styles.productPage}>
      <div className={styles.pageGlow} aria-hidden="true" />

      <header className={styles.marketingNav}>
        <button type="button" className={styles.brandButton} onClick={() => window.scrollTo({ top: 0, behavior: "smooth" })}>
          <BrandMark compact />
        </button>

        <nav className={styles.marketingLinks} aria-label="Navegacion principal">
          <button type="button" className={styles.navPillButton} onClick={() => scrollToId("about")}>
            Que es AppFoodSpring
          </button>
          <button type="button" className={styles.navPillButton} onClick={() => scrollToId("auth")}>
            Inicia sesion
          </button>
          <button type="button" className={styles.navPillButton} onClick={() => setAssistantOpen(true)}>
            Preguntar a {AI_COACH_NAME}
          </button>
          <Link href="/plans">Planes</Link>
          <button type="button" className={styles.navPillButton} onClick={() => scrollToId("downloads")}>
            Descargar aplicacion
          </button>
        </nav>
      </header>

      <section className={styles.heroSection}>
        <Reveal className={styles.heroIntroGrid} delay={40}>
          <article className={styles.heroTitleCard}>
            <span className={styles.sectionBadge}>Landing publica</span>
            <h1 className={styles.heroTitle}>
              Tu app de nutricion, entrenamiento y trazabilidad diaria en una sola experiencia.
            </h1>
            <p className={styles.heroText}>
              AppFoodSpring conecta autenticacion, onboarding, comida, recetas, refrigerador y
              progreso para que entrar al producto ya se sienta util desde el primer dia.
            </p>
            <div className={styles.heroBulletList}>
              <div className={styles.heroBullet}>
                <CheckCircleIcon className={styles.bulletIcon} />
                <span>Landing publica con acceso, explicacion y CTA de descarga.</span>
              </div>
              <div className={styles.heroBullet}>
                <CheckCircleIcon className={styles.bulletIcon} />
                <span>Flujo protegido: login, registro y cuestionario obligatorio.</span>
              </div>
              <div className={styles.heroBullet}>
                <CheckCircleIcon className={styles.bulletIcon} />
                <span>Base visual lista para version web y companion movil.</span>
              </div>
            </div>
          </article>

          <article className={styles.heroInfoCard}>
            <span className={styles.sectionBadge}>Estado de producto</span>
            <h2 className={styles.sectionTitle}>Diseno actual, conectado a backend real.</h2>
            <p className={styles.sectionText}>
              La capa visual se mantiene y ahora el flujo de acceso ya decide si el usuario entra a
              `/home` o debe completar `/register` segun el estado real del onboarding.
            </p>
            <div className={styles.heroActionRow}>
              <button type="button" className={styles.primaryButton} onClick={() => scrollToId("auth")}>
                <span>Entrar ahora</span>
                <ArrowRightIcon className={styles.buttonIcon} />
              </button>
              <button type="button" className={styles.secondaryButton} onClick={handleResumeSession} disabled={busy}>
                Retomar sesion
              </button>
            </div>
          </article>
        </Reveal>

        <div id="auth">
          <Reveal className={styles.heroAuthWrap} delay={120}>
            <div className={styles.authPanel}>
            <div className={styles.authPanelHeader}>
              <div>
                <span className={styles.sectionBadge}>Autenticacion</span>
                <h2 className={styles.sectionTitle}>Inicia sesion o crea tu cuenta.</h2>
              </div>
              <div className={styles.authModeSwitch}>
                <button
                  type="button"
                  className={mode === "login" ? styles.segmentActive : styles.segmentButton}
                  onClick={() => setMode("login")}
                >
                  Iniciar sesion
                </button>
                <button
                  type="button"
                  className={mode === "register" ? styles.segmentActive : styles.segmentButton}
                  onClick={() => setMode("register")}
                >
                  Registrarse
                </button>
              </div>
            </div>

            <form className={styles.authForm} onSubmit={handleSubmit}>
              <label className={styles.field}>
                <span>Base URL del backend</span>
                <input
                  className={styles.input}
                  value={baseUrl}
                  onChange={(event) => setBaseUrl(event.target.value)}
                  placeholder="http://localhost:8081"
                />
              </label>

              {mode === "register" ? (
                <div className={styles.gridTwo}>
                  <label className={styles.field}>
                    <span>Nombre</span>
                    <input
                      className={styles.input}
                      value={registerForm.firstName}
                      onChange={(event) =>
                        setRegisterForm((current) => ({ ...current, firstName: event.target.value }))
                      }
                      required
                    />
                  </label>
                  <label className={styles.field}>
                    <span>Apellido</span>
                    <input
                      className={styles.input}
                      value={registerForm.lastName}
                      onChange={(event) =>
                        setRegisterForm((current) => ({ ...current, lastName: event.target.value }))
                      }
                      required
                    />
                  </label>
                </div>
              ) : null}

              <label className={styles.field}>
                <span>Email</span>
                <input
                  className={styles.input}
                  type="email"
                  value={mode === "login" ? loginForm.email : registerForm.email}
                  onChange={(event) =>
                    mode === "login"
                      ? setLoginForm((current) => ({ ...current, email: event.target.value }))
                      : setRegisterForm((current) => ({ ...current, email: event.target.value }))
                  }
                  autoComplete="email"
                  required
                />
              </label>

              <div className={styles.gridTwo}>
                <label className={styles.field}>
                  <span>Contrasena</span>
                  <input
                    className={styles.input}
                    type="password"
                    value={mode === "login" ? loginForm.password : registerForm.password}
                    onChange={(event) =>
                      mode === "login"
                        ? setLoginForm((current) => ({ ...current, password: event.target.value }))
                        : setRegisterForm((current) => ({ ...current, password: event.target.value }))
                    }
                    autoComplete={mode === "login" ? "current-password" : "new-password"}
                    required
                  />
                </label>

                {mode === "register" ? (
                  <label className={styles.field}>
                    <span>Confirmar contrasena</span>
                    <input
                      className={styles.input}
                      type="password"
                      value={registerForm.confirmPassword}
                      onChange={(event) =>
                        setRegisterForm((current) => ({
                          ...current,
                          confirmPassword: event.target.value,
                        }))
                      }
                      autoComplete="new-password"
                      required
                    />
                  </label>
                ) : (
                  <div className={styles.authInfoCard}>
                    <strong>Despues del acceso</strong>
                    <span>
                      Si el onboarding ya esta completo iras a `/home`; si no, el flujo te lleva a
                      `/register`.
                    </span>
                  </div>
                )}
              </div>

              {error ? (
                <p className={styles.formError} role="alert">
                  {error}
                </p>
              ) : null}

              <button type="submit" className={styles.primaryButton} disabled={busy}>
                <span>
                  {busy
                    ? "Procesando..."
                    : mode === "login"
                      ? "Entrar a mi panel"
                      : "Crear cuenta y continuar"}
                </span>
                <ArrowRightIcon className={styles.buttonIcon} />
              </button>
            </form>
            </div>
          </Reveal>
        </div>
      </section>

      <section id="about" className={styles.marketingSection}>
        <Reveal className={styles.sectionHeading}>
          <span className={styles.sectionBadge}>Que es AppFoodSpring</span>
          <h2>Un producto para registrar lo que comes, seguir tus metas y tomar mejores decisiones.</h2>
        </Reveal>

        <div className={styles.featureGrid}>
          {productHighlights.map(({ title, description, icon: Icon }, index) => (
            <Reveal key={title} className={styles.featureCard} delay={index * 60}>
              <span className={styles.iconBadge}>
                <Icon className={styles.cardIcon} />
              </span>
              <h3>{title}</h3>
              <p>{description}</p>
            </Reveal>
          ))}
        </div>
      </section>

      <section id="preview" className={styles.marketingSection}>
        <div className={styles.previewGrid}>
          <Reveal className={styles.previewPanel}>
            <div className={styles.previewHeader}>
              <GlobeIcon className={styles.cardIcon} />
              <div>
                <strong>Version web</strong>
                <span>Landing, onboarding y dashboard con trazabilidad real.</span>
              </div>
            </div>
            <div className={styles.previewBars}>
              <div className={styles.previewBar} />
              <div className={styles.previewBarSoft} />
              <div className={styles.previewCardStack}>
                <article />
                <article />
                <article />
              </div>
            </div>
          </Reveal>

          <Reveal className={styles.phoneFrame} delay={100}>
            <div className={styles.phoneHeader}>
              <PhoneIcon className={styles.cardIcon} />
              <span>Companion movil</span>
            </div>
            <div className={styles.phoneTimeline}>
              <article />
              <article />
              <article />
            </div>
          </Reveal>
        </div>
      </section>

      <section id="journey" className={styles.marketingSection}>
        <div className={styles.timelineGrid}>
          {journeySteps.map(({ icon: Icon, title, description }, index) => (
            <Reveal key={title} className={styles.timelineCard} delay={index * 70}>
              <Icon className={styles.cardIcon} />
              <h3>{title}</h3>
              <p>{description}</p>
            </Reveal>
          ))}
        </div>
      </section>

      <section id="downloads" className={styles.marketingSection}>
        <Reveal className={styles.downloadPanel}>
          <div>
            <span className={styles.sectionBadge}>Descargas</span>
            <h2 className={styles.sectionTitle}>Versiones en preparacion para web y movil.</h2>
            <p className={styles.sectionText}>
              Mientras llegan los binarios finales, dejamos placeholders visibles para evitar rutas
              rotas y mantener el flujo de producto consistente.
            </p>
          </div>
          <div className={styles.storePlaceholders}>
            <span>App Store</span>
            <span>Google Play</span>
            <span>Web App</span>
          </div>
        </Reveal>
      </section>

      <footer className={styles.productFooter}>
        <BrandMark compact />
        <div className={styles.footerLinks}>
          <Link href="/plans">Informacion del producto</Link>
          <Link href="/api-testv1">API</Link>
          <a href="#about">Servicios</a>
          <a href="#downloads">Contacto</a>
          <a href="#downloads">Politica de privacidad</a>
          <a href="#downloads">Terminos y condiciones</a>
        </div>
      </footer>

      <div className={styles.assistantDock}>
        {assistantOpen ? (
          <div className={styles.assistantPanel}>
            <div className={styles.assistantPanelHeader}>
              <div>
                <strong>Habla con {AI_COACH_NAME}</strong>
                <span>Funcionalidad proxima</span>
              </div>
              <button
                type="button"
                className={styles.iconButton}
                onClick={() => setAssistantOpen(false)}
                aria-label={`Cerrar panel de ${AI_COACH_NAME}`}
              >
                ×
              </button>
            </div>
            <p>
              {AI_COACH_NAME} todavia no esta conectada a IA real, pero este espacio ya queda listo
              para recomendaciones sobre comida, recetas y progreso.
            </p>
          </div>
        ) : null}
        <button
          type="button"
          className={styles.assistantLauncher}
          onClick={() => setAssistantOpen((current) => !current)}
        >
          Hablar con {AI_COACH_NAME}
        </button>
      </div>
    </main>
  );
}
