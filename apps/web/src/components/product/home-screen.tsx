"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect, useMemo, useState } from "react";
import { BrandMark } from "./brand-mark";
import {
  AI_COACH_NAME,
  createFoodLogEntry,
  getActiveSession,
  loadDailyFoodSummary,
  loadHomeSnapshot,
  loadOnboardingBootstrap,
  logoutFromBackend,
  readPreferredApiUrl,
  searchFoodsForLog,
  todayDateString,
  type DailyFoodSummary,
  type HomeSnapshot,
} from "./backend";
import styles from "./product-shell.module.css";
import {
  BarChartIcon,
  CloseIcon,
  DumbbellIcon,
  MenuIcon,
  SparkIcon,
  UserCircleIcon,
  UtensilsIcon,
} from "./icons";
import { Reveal } from "./reveal";
import type { Food } from "@/types/domain";

const mealTypes = [
  { value: "breakfast", label: "Desayuno" },
  { value: "lunch", label: "Comida" },
  { value: "dinner", label: "Cena" },
  { value: "snack", label: "Snack" },
];

const unitOptions = [
  { value: "g", label: "g" },
  { value: "oz", label: "oz" },
  { value: "ml", label: "ml" },
  { value: "taza", label: "taza" },
  { value: "pieza", label: "pieza" },
  { value: "porcion", label: "porcion" },
];

const emptySnapshot: HomeSnapshot = {
  profile: null,
  foods: [],
  exercises: [],
  recipePosts: [],
  fridgeMatches: [],
  fridgeItems: [],
  dailySummary: null,
};

function formatDateLabel() {
  return new Intl.DateTimeFormat("es-MX", {
    weekday: "long",
    day: "numeric",
    month: "long",
  }).format(new Date());
}

function valueOrDash(value: number | string | null | undefined) {
  if (value === null || value === undefined || value === "") {
    return "--";
  }

  return value;
}

function roundMacro(value: number | null) {
  return value === null ? null : Math.round(value * 10) / 10;
}

function quantityToBaseAmount(food: Food | null, quantity: number, unit: string) {
  if (!food) {
    return 0;
  }

  switch (unit) {
    case "g":
    case "ml":
      return quantity;
    case "oz":
      return quantity * 28.35;
    case "taza":
      return quantity * 240;
    case "pieza":
    case "porcion":
      return quantity * (food.servingSize ?? 100);
    default:
      return quantity;
  }
}

export function HomeScreen() {
  const router = useRouter();
  const [session, setSession] = useState(getActiveSession);
  const [baseUrl] = useState(readPreferredApiUrl);
  const [snapshot, setSnapshot] = useState<HomeSnapshot>(emptySnapshot);
  const [loading, setLoading] = useState(true);
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [assistantOpen, setAssistantOpen] = useState(false);
  const [logModalOpen, setLogModalOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [searchBusy, setSearchBusy] = useState(false);
  const [searchError, setSearchError] = useState<string | null>(null);
  const [searchResults, setSearchResults] = useState<Food[]>([]);
  const [selectedFood, setSelectedFood] = useState<Food | null>(null);
  const [mealType, setMealType] = useState("breakfast");
  const [unit, setUnit] = useState("g");
  const [quantity, setQuantity] = useState("100");
  const [toast, setToast] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    async function bootstrap() {
      setLoading(true);
      setError(null);

      try {
        const onboardingState = await loadOnboardingBootstrap(baseUrl, session);
        if (!onboardingState) {
          setSession(null);
          router.replace("/");
          return;
        }

        if (!onboardingState.completed) {
          router.replace("/register");
          return;
        }

        if (!cancelled) {
          setSession(onboardingState.session);
        }

        const nextSnapshot = await loadHomeSnapshot(baseUrl, onboardingState.session, todayDateString());
        if (!cancelled) {
          setSnapshot(nextSnapshot);
        }
      } catch (loadError) {
        if (!cancelled) {
          setError(loadError instanceof Error ? loadError.message : "No se pudo cargar el home.");
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    bootstrap();
    return () => {
      cancelled = true;
    };
  }, [baseUrl, router, session]);

  useEffect(() => {
    if (!toast) {
      return;
    }

    const timer = window.setTimeout(() => setToast(null), 4000);
    return () => window.clearTimeout(timer);
  }, [toast]);

  const todaySummary: DailyFoodSummary | null = snapshot.dailySummary;
  const consumedCalories = todaySummary?.totalCalories ?? 0;
  const targetCalories = snapshot.profile?.calorieGoal ?? null;
  const progress = targetCalories && targetCalories > 0 ? Math.min((consumedCalories / targetCalories) * 100, 100) : 0;
  const lastFood = todaySummary?.items.at(-1) ?? null;
  const displayName = snapshot.profile?.firstName || snapshot.profile?.displayName || "Tu panel";

  const selectedFoodMacros = useMemo(() => {
    if (!selectedFood) {
      return {
        calories: null,
        protein: null,
        carbs: null,
        fat: null,
        sodium: null,
        portionText: null as string | null,
      };
    }

    const numericQuantity = Number(quantity);
    if (!Number.isFinite(numericQuantity) || numericQuantity <= 0) {
      return {
        calories: null,
        protein: null,
        carbs: null,
        fat: null,
        sodium: null,
        portionText: null as string | null,
      };
    }

    const baseAmount = quantityToBaseAmount(selectedFood, numericQuantity, unit);
    const factor = baseAmount / 100;

    return {
      calories: roundMacro((selectedFood.caloriesPer100g ?? 0) * factor),
      protein: roundMacro((selectedFood.proteinPer100g ?? 0) * factor),
      carbs: roundMacro((selectedFood.carbsPer100g ?? 0) * factor),
      fat: roundMacro((selectedFood.fatPer100g ?? 0) * factor),
      sodium: roundMacro((selectedFood.sodiumPer100g ?? 0) * factor),
      portionText: `${numericQuantity} ${unit}`,
    };
  }, [quantity, selectedFood, unit]);

  async function handleLogout() {
    await logoutFromBackend(baseUrl, session);
    setSession(null);
    router.push("/");
  }

  async function handleSearch() {
    if (!session || !searchQuery.trim()) {
      setSearchResults([]);
      setSearchError(searchQuery.trim() ? "Necesitas una sesion valida." : null);
      return;
    }

    setSearchBusy(true);
    setSearchError(null);

    try {
      const results = await searchFoodsForLog(baseUrl, session, searchQuery.trim());
      setSearchResults(results);
    } catch (searchFailure) {
      setSearchError(
        searchFailure instanceof Error ? searchFailure.message : "No se pudieron buscar alimentos.",
      );
    } finally {
      setSearchBusy(false);
    }
  }

  async function refreshDailySummary() {
    if (!session) {
      return;
    }

    const nextSummary = await loadDailyFoodSummary(baseUrl, session, todayDateString());
    setSnapshot((current) => ({ ...current, dailySummary: nextSummary }));
  }

  async function handleFoodLogSubmit() {
    if (!session || !selectedFood) {
      return;
    }

    const numericQuantity = Number(quantity);
    if (!Number.isFinite(numericQuantity) || numericQuantity <= 0) {
      setSearchError("La cantidad debe ser mayor a 0.");
      return;
    }

    setSearchBusy(true);
    setSearchError(null);

    try {
      await createFoodLogEntry(baseUrl, session, {
        foodId: selectedFood.id,
        mealType,
        quantity: numericQuantity,
        unit,
        logDate: todayDateString(),
        calories: selectedFoodMacros.calories,
        proteinG: selectedFoodMacros.protein,
        carbsG: selectedFoodMacros.carbs,
        fatG: selectedFoodMacros.fat,
      });
      await refreshDailySummary();
      setToast("Alimento registrado exitosamente.");
      setLogModalOpen(false);
      setSelectedFood(null);
      setSearchResults([]);
      setSearchQuery("");
      setQuantity("100");
      setUnit("g");
    } catch (logError) {
      setSearchError(logError instanceof Error ? logError.message : "No se pudo registrar el alimento.");
    } finally {
      setSearchBusy(false);
    }
  }

  const sidebarLinks = [
    { label: "Dashboard", href: "/home", active: true },
    { label: "Mis entrenamientos", href: "/workouts", active: false },
    { label: "Plan alimenticio", href: "/meal-plan", active: false },
    { label: "Recetas", href: "/home#recipes", active: false },
    { label: "Refrigerador", href: "/home#fridge", active: false },
    { label: "Perfil", href: "/profile", active: false },
    { label: "Ajustes", href: "/settings", active: false },
    { label: "Mi plan", href: "/my-plan", active: false },
    { label: "Notas y progreso", href: "/progress", active: false },
  ];

  return (
    <main className={styles.productPage}>
      <div className={styles.pageGlow} aria-hidden="true" />

      <header className={styles.appTopbar}>
        <div className={styles.topbarLeading}>
          <button
            type="button"
            className={styles.iconButton}
            onClick={() => setSidebarOpen((current) => !current)}
            aria-label={sidebarOpen ? "Cerrar menu lateral" : "Abrir menu lateral"}
          >
            {sidebarOpen ? <CloseIcon className={styles.cardIcon} /> : <MenuIcon className={styles.cardIcon} />}
          </button>
          <Link href="/home">
            <BrandMark compact />
          </Link>
        </div>

        <nav className={styles.homeNav} aria-label="Acciones privadas">
          <Link href="/profile">Ir a perfil</Link>
          <button type="button" className={styles.navPillButton} onClick={() => setAssistantOpen(true)}>
            Hablar con {AI_COACH_NAME}
          </button>
          <Link href="/settings">Ajustes</Link>
          <Link href="/my-plan">Mi plan</Link>
          <button type="button" className={styles.navPillButton} onClick={handleLogout}>
            Cerrar sesion
          </button>
        </nav>

        <button type="button" className={styles.profileButton}>
          <UserCircleIcon className={styles.cardIcon} />
          <span>{displayName}</span>
        </button>
      </header>

      <section className={styles.homeLayout}>
        <aside className={[styles.sidebar, sidebarOpen ? styles.sidebarOpen : ""].filter(Boolean).join(" ")}>
          <div className={styles.sidebarCard}>
            <div className={styles.sidebarTitleRow}>
              <strong>Explora tu panel</strong>
              <span className={styles.sidebarPill}>privado</span>
            </div>
            <nav className={styles.sidebarNav}>
              {sidebarLinks.map((item) => (
                <Link
                  key={item.label}
                  href={item.href}
                  className={item.active ? styles.sidebarLinkActive : undefined}
                  onClick={() => setSidebarOpen(false)}
                >
                  {item.label}
                </Link>
              ))}
            </nav>
          </div>

          <div className={styles.sidebarCard}>
            <strong>Estado de sesion</strong>
            <p>{snapshot.profile?.email ?? "Sin perfil cargado"}</p>
            <button type="button" className={styles.secondaryButton} onClick={handleLogout}>
              Cerrar sesion
            </button>
          </div>

          <div className={styles.sidebarCard}>
            <strong>Asistente</strong>
            <p>{AI_COACH_NAME} queda visible como acceso rapido aunque aun este en modo proximo.</p>
            <button type="button" className={styles.secondaryButton} onClick={() => setAssistantOpen(true)}>
              Abrir panel
            </button>
          </div>
        </aside>

        <div className={styles.homeMain}>
          <Reveal delay={40}>
            <section className={styles.heroSummaryPanel}>
              {loading ? (
                <div className={styles.loadingColumn}>
                  <div className={styles.loadingBadge} />
                  <div className={styles.loadingTitle} />
                  <div className={styles.loadingLine} />
                  <div className={styles.loadingLineShort} />
                </div>
              ) : (
                <>
                  <div>
                    <span className={styles.sectionBadge}>Bienvenida</span>
                    <h1 className={styles.sectionTitle}>Hola, {displayName}. Hoy es un buen dia para acercarte a tu meta.</h1>
                    <p className={styles.sectionText}>
                      {formatDateLabel()} · Objetivo actual: {snapshot.profile?.goal ?? "sin definir"}.
                    </p>
                  </div>

                  <div className={styles.metricStrip}>
                    <article>
                      <span>Foods disponibles</span>
                      <strong>{snapshot.foods.length}</strong>
                    </article>
                    <article>
                      <span>Ejercicios visibles</span>
                      <strong>{snapshot.exercises.length}</strong>
                    </article>
                    <article>
                      <span>Recipe posts</span>
                      <strong>{snapshot.recipePosts.length}</strong>
                    </article>
                  </div>
                </>
              )}
            </section>
          </Reveal>

          <Reveal delay={80}>
            <section id="nutrition" className={styles.contentPanel}>
              <div className={styles.sectionHeaderRow}>
                <div>
                  <span className={styles.sectionBadge}>Plan alimenticio diario</span>
                  <h2>Calorias, progreso visual y ultimo alimento registrado.</h2>
                </div>
                <div className={styles.actionCluster}>
                  <button type="button" className={styles.primaryButton} onClick={() => setLogModalOpen(true)}>
                    <span>Registrar alimento</span>
                  </button>
                  <Link href="/meal-plan" className={styles.secondaryButton}>
                    Ver dia completo
                  </Link>
                </div>
              </div>

              <div className={styles.nutritionGrid}>
                <article className={styles.summaryFeatureCard}>
                  <span className={styles.iconBadge}>
                    <UtensilsIcon className={styles.cardIcon} />
                  </span>
                  <h3>Consumo de hoy</h3>
                  <div className={styles.goalMetrics}>
                    <article>
                      <span>Consumidas</span>
                      <strong>{valueOrDash(todaySummary?.totalCalories)}</strong>
                    </article>
                    <article>
                      <span>Objetivo</span>
                      <strong>{valueOrDash(targetCalories)}</strong>
                    </article>
                    <article>
                      <span>Proteina</span>
                      <strong>{valueOrDash(todaySummary?.totalProteinG)} g</strong>
                    </article>
                    <article>
                      <span>Carbos</span>
                      <strong>{valueOrDash(todaySummary?.totalCarbsG)} g</strong>
                    </article>
                  </div>
                  <div className={styles.progressRail}>
                    <span style={{ width: `${progress}%` }} />
                  </div>
                  <p className={styles.helperText}>
                    {targetCalories
                      ? `${Math.round(progress)}% de tu objetivo diario.`
                      : "Completa tus metas caloricas para ver el progreso."}
                  </p>
                </article>

                <article className={styles.summaryFeatureCard}>
                  <span className={styles.iconBadge}>
                    <BarChartIcon className={styles.cardIcon} />
                  </span>
                  <h3>Ultimo registro</h3>
                  {lastFood ? (
                    <div className={styles.summaryList}>
                      <article>
                        <UtensilsIcon className={styles.cardIcon} />
                        <div>
                          <strong>{lastFood.foodName}</strong>
                          <span>
                            {valueOrDash(lastFood.quantity)} {lastFood.unit ?? ""} · {lastFood.mealType}
                          </span>
                        </div>
                      </article>
                      <article>
                        <SparkIcon className={styles.cardIcon} />
                        <div>
                          <strong>{valueOrDash(lastFood.calories)} kcal</strong>
                          <span>
                            P {valueOrDash(lastFood.proteinG)} · C {valueOrDash(lastFood.carbsG)} · G{" "}
                            {valueOrDash(lastFood.fatG)}
                          </span>
                        </div>
                      </article>
                    </div>
                  ) : (
                    <p className={styles.sectionText}>
                      Aun no tienes alimentos cargados hoy. Usa el modal para buscar y registrar uno.
                    </p>
                  )}
                </article>
              </div>
            </section>
          </Reveal>

          <Reveal delay={120}>
            <section className={styles.contentPanel}>
              <div className={styles.dualFeatureGrid}>
                <article className={styles.summaryFeatureCard}>
                  <span className={styles.iconBadge}>
                    <DumbbellIcon className={styles.cardIcon} />
                  </span>
                  <h3>Mis entrenamientos</h3>
                  <ul className={styles.miniList}>
                    {(snapshot.exercises.length
                      ? snapshot.exercises.slice(0, 4)
                      : [{ id: "empty-exercise", name: "Aun no hay ejercicios visibles" }]).map((item) => (
                      <li key={item.id}>{item.name}</li>
                    ))}
                  </ul>
                  <Link href="/workouts" className={styles.secondaryButton}>
                    Ver modulo
                  </Link>
                </article>

                <article id="ai" className={styles.summaryFeatureCard}>
                  <span className={styles.iconBadge}>
                    <SparkIcon className={styles.cardIcon} />
                  </span>
                  <h3>Habla con {AI_COACH_NAME}</h3>
                  <p>
                    {AI_COACH_NAME} sigue en modo funcionalidad proxima, pero esta tarjeta ya
                    resume el contexto que usara: onboarding, logs, recetas y contenido del refri.
                  </p>
                  <button type="button" className={styles.secondaryButton} onClick={() => setAssistantOpen(true)}>
                    Abrir chatbot flotante
                  </button>
                </article>
              </div>
            </section>
          </Reveal>

          <Reveal delay={160}>
            <section className={styles.ctaPanel}>
              <div className={styles.ctaCardStack}>
                <article id="fridge" className={styles.ctaCard}>
                  <div className={styles.ctaCardHeader}>
                    <span className={styles.iconBadge}>
                      <UtensilsIcon className={styles.cardIcon} />
                    </span>
                    <div>
                      <strong>Refrigerador</strong>
                      <p>Ingredientes cargados y modulo listo para ampliarse.</p>
                    </div>
                  </div>
                  <ul className={styles.miniList}>
                    {(snapshot.fridgeItems.length
                      ? snapshot.fridgeItems.slice(0, 4)
                      : [{ id: "empty-fridge", name: "Tu refri aun no tiene items cargados" }]).map((item) => (
                      <li key={item.id}>
                        {item.name}
                        {item.quantity ? ` · ${item.quantity}${item.unit ? ` ${item.unit}` : ""}` : ""}
                      </li>
                    ))}
                  </ul>
                  <Link href="/progress" className={styles.secondaryButton}>
                    Ver notas y progreso
                  </Link>
                </article>

                <article id="recipes" className={styles.ctaCard}>
                  <div className={styles.ctaCardHeader}>
                    <span className={styles.iconBadge}>
                      <SparkIcon className={styles.cardIcon} />
                    </span>
                    <div>
                      <strong>Recetas sugeridas</strong>
                      <p>Coincidencias del refri y posts para decidir que cocinar.</p>
                    </div>
                  </div>
                  <ul className={styles.matchList}>
                    {(snapshot.fridgeMatches.length
                      ? snapshot.fridgeMatches.slice(0, 4)
                      : [{ id: "empty-match", title: "Aun no hay recetas sugeridas", matchedIngredients: 0, totalIngredients: 0, matchedFoodNames: [] }]).map((item) => (
                      <li key={item.id}>
                        <div>
                          <span>{item.title}</span>
                          {item.totalIngredients ? (
                            <small>
                              {item.matchedIngredients}/{item.totalIngredients} ingredientes
                            </small>
                          ) : null}
                        </div>
                        {item.matchedFoodNames.length ? <p>{item.matchedFoodNames.join(", ")}</p> : null}
                      </li>
                    ))}
                  </ul>
                  <Link href="/my-plan" className={styles.secondaryButton}>
                    Ir a mi plan
                  </Link>
                </article>
              </div>
            </section>
          </Reveal>
        </div>
      </section>

      <footer className={styles.productFooter}>
        <div>
          <strong>AppFoodSpring</strong>
          <p>Home protegido con JWT, onboarding obligatorio y modulos placeholder conectados al diseño real.</p>
        </div>
        <div className={styles.storePlaceholders}>
          <span>Perfil</span>
          <span>Ajustes</span>
          <span>Mi plan</span>
        </div>
      </footer>

      {toast ? <div className={styles.toast}>{toast}</div> : null}

      {assistantOpen ? (
        <div className={styles.assistantDock}>
          <div className={styles.assistantPanel}>
            <div className={styles.assistantPanelHeader}>
              <div>
                <strong>{AI_COACH_NAME}</strong>
                <span>Funcionalidad proxima</span>
              </div>
              <button type="button" className={styles.iconButton} onClick={() => setAssistantOpen(false)}>
                ×
              </button>
            </div>
            <p>
              {AI_COACH_NAME} aparecera aqui como chatbot flotante cuando se conecte la capa de IA
              real. Mientras tanto, este acceso no bloquea formularios ni acciones principales.
            </p>
          </div>
        </div>
      ) : (
        <div className={styles.assistantDock}>
          <button type="button" className={styles.assistantLauncher} onClick={() => setAssistantOpen(true)}>
            Hablar con {AI_COACH_NAME}
          </button>
        </div>
      )}

      {logModalOpen ? (
        <div className={styles.modalBackdrop} role="dialog" aria-modal="true" aria-labelledby="food-log-modal-title">
          <div className={styles.modalPanel}>
            <div className={styles.modalHeader}>
              <div>
                <span className={styles.sectionBadge}>Registrar alimento</span>
                <h2 id="food-log-modal-title" className={styles.sectionTitle}>
                  Busca un alimento o receta para tu dia.
                </h2>
              </div>
              <button type="button" className={styles.iconButton} onClick={() => setLogModalOpen(false)}>
                <CloseIcon className={styles.cardIcon} />
              </button>
            </div>

            {!selectedFood ? (
              <div className={styles.modalContent}>
                <div className={styles.searchRow}>
                  <input
                    className={styles.input}
                    value={searchQuery}
                    onChange={(event) => setSearchQuery(event.target.value)}
                    placeholder="Busca pollo, avena, yogur..."
                  />
                  <button type="button" className={styles.primaryButton} onClick={handleSearch} disabled={searchBusy}>
                    <span>{searchBusy ? "Buscando..." : "Buscar"}</span>
                  </button>
                </div>

                {searchError ? <p className={styles.formError}>{searchError}</p> : null}

                <div className={styles.searchResults}>
                  {searchBusy ? (
                    <div className={styles.loadingColumn}>
                      <div className={styles.loadingLine} />
                      <div className={styles.loadingLine} />
                      <div className={styles.loadingLineShort} />
                    </div>
                  ) : searchResults.length ? (
                    searchResults.map((item) => (
                      <button
                        key={item.id}
                        type="button"
                        className={styles.searchResultCard}
                        onClick={() => setSelectedFood(item)}
                      >
                        <div>
                          <strong>{item.name}</strong>
                          <span>{item.brand ?? "Marca no especificada"}</span>
                        </div>
                        <small>{valueOrDash(item.caloriesPer100g)} kcal / 100g</small>
                      </button>
                    ))
                  ) : (
                    <p className={styles.sectionText}>
                      {searchQuery ? "No se encontraron resultados." : "Escribe un texto para comenzar."}
                    </p>
                  )}
                </div>
              </div>
            ) : (
              <div className={styles.modalContent}>
                <button type="button" className={styles.inlineLinkButton} onClick={() => setSelectedFood(null)}>
                  Volver a resultados
                </button>

                <div className={styles.foodDetailCard}>
                  <div className={styles.foodDetailHeader}>
                    <div className={styles.foodPlaceholder}>SVG</div>
                    <div>
                      <h3>{selectedFood.name}</h3>
                      <p>{selectedFood.brand ?? "Marca no especificada"}</p>
                    </div>
                  </div>

                  <div className={styles.goalMetrics}>
                    <article>
                      <span>Calorias</span>
                      <strong>{valueOrDash(selectedFoodMacros.calories)}</strong>
                    </article>
                    <article>
                      <span>Proteinas</span>
                      <strong>{valueOrDash(selectedFoodMacros.protein)} g</strong>
                    </article>
                    <article>
                      <span>Carbohidratos</span>
                      <strong>{valueOrDash(selectedFoodMacros.carbs)} g</strong>
                    </article>
                    <article>
                      <span>Grasas</span>
                      <strong>{valueOrDash(selectedFoodMacros.fat)} g</strong>
                    </article>
                    <article>
                      <span>Sodio</span>
                      <strong>{valueOrDash(selectedFoodMacros.sodium)} mg</strong>
                    </article>
                    <article>
                      <span>Porcion base</span>
                      <strong>
                        {valueOrDash(selectedFood.servingSize)}
                        {selectedFood.servingUnit ? ` ${selectedFood.servingUnit}` : ""}
                      </strong>
                    </article>
                  </div>
                </div>

                <div className={styles.gridThree}>
                  <label className={styles.field}>
                    <span>Comida del dia</span>
                    <select className={styles.select} value={mealType} onChange={(event) => setMealType(event.target.value)}>
                      {mealTypes.map((item) => (
                        <option key={item.value} value={item.value}>
                          {item.label}
                        </option>
                      ))}
                    </select>
                  </label>
                  <label className={styles.field}>
                    <span>Unidad</span>
                    <select className={styles.select} value={unit} onChange={(event) => setUnit(event.target.value)}>
                      {unitOptions.map((item) => (
                        <option key={item.value} value={item.value}>
                          {item.label}
                        </option>
                      ))}
                    </select>
                  </label>
                  <label className={styles.field}>
                    <span>Cantidad</span>
                    <input className={styles.input} value={quantity} onChange={(event) => setQuantity(event.target.value)} />
                  </label>
                </div>

                {selectedFoodMacros.portionText ? (
                  <p className={styles.helperText}>
                    Calculado para {selectedFoodMacros.portionText}: {valueOrDash(selectedFoodMacros.calories)} kcal,
                    {` `}P {valueOrDash(selectedFoodMacros.protein)} / C {valueOrDash(selectedFoodMacros.carbs)} / G{" "}
                    {valueOrDash(selectedFoodMacros.fat)}.
                  </p>
                ) : null}

                {searchError ? <p className={styles.formError}>{searchError}</p> : null}

                <div className={styles.modalActions}>
                  <button type="button" className={styles.secondaryButton} onClick={() => setLogModalOpen(false)}>
                    Cancelar
                  </button>
                  <button type="button" className={styles.primaryButton} onClick={handleFoodLogSubmit} disabled={searchBusy}>
                    <span>{searchBusy ? "Registrando..." : "Registrar alimento"}</span>
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      ) : null}

      {error ? <div className={styles.inlineAlert}>{error}</div> : null}
    </main>
  );
}
