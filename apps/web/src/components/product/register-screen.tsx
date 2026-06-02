"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect, useMemo, useState } from "react";
import { BrandMark } from "./brand-mark";
import {
  calculateGoalPreview,
  clearOnboardingDraft,
  composeOnboardingForm,
  getActiveSession,
  loadOnboardingBootstrap,
  readPreferredApiUrl,
  saveOnboardingFlow,
  writeOnboardingDraft,
  type GoalPreview,
  type ProductOnboardingForm,
} from "./backend";
import styles from "./product-shell.module.css";
import {
  ArrowRightIcon,
  BarChartIcon,
  ChevronLeftIcon,
  CheckCircleIcon,
  DumbbellIcon,
  SparkIcon,
  UtensilsIcon,
} from "./icons";
import { Reveal } from "./reveal";

const goalOptions = [
  { value: "lose_weight", label: "Perder peso", icon: BarChartIcon },
  { value: "gain_muscle", label: "Ganar musculo", icon: DumbbellIcon },
  { value: "maintain_weight", label: "Mantener peso", icon: SparkIcon },
  { value: "body_recomposition", label: "Recomposicion corporal", icon: CheckCircleIcon },
  { value: "improve_health", label: "Mejorar salud", icon: UtensilsIcon },
];

const activityOptions = [
  { value: "sedentary", label: "Sedentario" },
  { value: "light", label: "Ligero" },
  { value: "moderate", label: "Moderado" },
  { value: "active", label: "Activo" },
  { value: "very_active", label: "Muy activo" },
];

const dietOptions = [
  { value: "balanced", label: "Balanceada" },
  { value: "high_protein", label: "Alta en proteina" },
  { value: "vegetarian", label: "Vegetariana" },
  { value: "low_carb", label: "Low carb" },
];

const genderOptions = [
  { value: "female", label: "Femenino" },
  { value: "male", label: "Masculino" },
  { value: "other", label: "Otro" },
];

const stepTitles = [
  "Datos generales y objetivo",
  "Datos fisicos y alimentacion",
  "Resumen y calculo estimado",
];

const emptyForm: ProductOnboardingForm = {
  firstName: "",
  lastName: "",
  mainGoal: "maintain_weight",
  currentWeightKg: null,
  targetWeightKg: null,
  heightCm: null,
  age: null,
  gender: "female",
  activityLevel: "moderate",
  trainingDaysPerWeek: 3,
  dietType: "balanced",
  foodRestrictions: "",
  allergies: "",
  preferredFoods: "",
  dislikedFoods: "",
  healthNotes: "",
};

function numberValue(value: string): number | null {
  if (!value.trim()) {
    return null;
  }

  const parsed = Number(value);
  return Number.isFinite(parsed) ? parsed : null;
}

function valueOrFallback(value: string | number | null | undefined, fallback = "--") {
  if (value === null || value === undefined || value === "") {
    return fallback;
  }

  return String(value);
}

function validateStep(step: number, form: ProductOnboardingForm): string | null {
  if (step === 0) {
    if (!form.firstName.trim()) return "El nombre es obligatorio.";
    if (!form.lastName.trim()) return "El apellido es obligatorio.";
    if (!form.mainGoal) return "Selecciona un objetivo principal.";
    if (!form.activityLevel) return "Selecciona tu nivel de actividad.";
    if (form.trainingDaysPerWeek === null || form.trainingDaysPerWeek < 0 || form.trainingDaysPerWeek > 7) {
      return "Los dias de actividad deben estar entre 0 y 7.";
    }
  }

  if (step === 1) {
    if (!form.age || form.age <= 0) return "La edad debe ser mayor a 0.";
    if (!form.currentWeightKg || form.currentWeightKg <= 0) return "El peso actual debe ser mayor a 0.";
    if (!form.heightCm || form.heightCm <= 0) return "La altura debe ser mayor a 0.";
    if (form.targetWeightKg !== null && form.targetWeightKg <= 0) {
      return "El peso objetivo debe ser mayor a 0.";
    }
  }

  return null;
}

export function RegisterScreen() {
  const router = useRouter();
  const [baseUrl] = useState(readPreferredApiUrl);
  const [step, setStep] = useState(0);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [calculating, setCalculating] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [goalPreview, setGoalPreview] = useState<GoalPreview | null>(null);
  const [form, setForm] = useState<ProductOnboardingForm>(emptyForm);
  const [hasExistingQuestionnaire, setHasExistingQuestionnaire] = useState(false);
  const [draftNotice, setDraftNotice] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    async function bootstrap() {
      setLoading(true);
      setError(null);

      try {
        const state = await loadOnboardingBootstrap(baseUrl, getActiveSession());
        if (!state) {
          router.replace("/");
          return;
        }

        if (state.completed) {
          clearOnboardingDraft();
          router.replace("/home");
          return;
        }

        if (!cancelled) {
          setForm(composeOnboardingForm(state.user, state.questionnaire));
          setHasExistingQuestionnaire(Boolean(state.questionnaire?.id));
          setDraftNotice(
            "Tus respuestas se conservan localmente mientras completas el flujo. El acceso a /home se valida con el estado real del backend.",
          );
        }
      } catch (bootstrapError) {
        if (!cancelled) {
          setError(
            bootstrapError instanceof Error
              ? bootstrapError.message
              : "No se pudo cargar el onboarding obligatorio.",
          );
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
  }, [baseUrl, router]);

  useEffect(() => {
    if (!loading) {
      writeOnboardingDraft(form);
    }
  }, [form, loading]);

  const completion = useMemo(() => ((step + 1) / stepTitles.length) * 100, [step]);

  function updateField<Key extends keyof ProductOnboardingForm>(
    field: Key,
    value: ProductOnboardingForm[Key],
  ) {
    setForm((current) => ({ ...current, [field]: value }));
  }

  async function handleGoalCalculation() {
    const stepError = validateStep(0, form) ?? validateStep(1, form);
    if (stepError) {
      setError(stepError);
      return;
    }

    const session = getActiveSession();
    if (!session) {
      router.replace("/");
      return;
    }

    setCalculating(true);
    setError(null);

    try {
      const preview = await calculateGoalPreview(baseUrl, session, form);
      setGoalPreview(preview);
      setStep(2);
    } catch (previewError) {
      setError(
        previewError instanceof Error ? previewError.message : "No se pudieron calcular las metas.",
      );
    } finally {
      setCalculating(false);
    }
  }

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const blockingError =
      validateStep(0, form) ?? validateStep(1, form) ?? validateStep(2, form);
    if (blockingError) {
      setError(blockingError);
      return;
    }

    const session = getActiveSession();
    if (!session) {
      router.replace("/");
      return;
    }

    setSubmitting(true);
    setError(null);

    try {
      await saveOnboardingFlow(baseUrl, session, form, hasExistingQuestionnaire);
      router.push("/home");
    } catch (submitError) {
      setError(
        submitError instanceof Error
          ? submitError.message
          : "No se pudo guardar el cuestionario.",
      );
    } finally {
      setSubmitting(false);
    }
  }

  function goNextStep() {
    const stepError = validateStep(step, form);
    if (stepError) {
      setError(stepError);
      return;
    }

    setError(null);
    if (step === 1) {
      void handleGoalCalculation();
      return;
    }

    setStep((current) => Math.min(stepTitles.length - 1, current + 1));
  }

  return (
    <main className={styles.productPage}>
      <div className={styles.pageGlow} aria-hidden="true" />

      <header className={styles.subpageHeader}>
        <Link href="/" className={styles.inlineLink}>
          <ChevronLeftIcon className={styles.inlineIcon} />
          Volver a la landing
        </Link>
        <BrandMark compact />
        <span className={styles.progressHint}>
          Paso {step + 1} de {stepTitles.length}
        </span>
      </header>

      <section className={styles.registerStack}>
        <Reveal className={styles.noticePanel} delay={30}>
          <span className={styles.sectionBadge}>Onboarding obligatorio</span>
          <h1 className={styles.sectionTitle}>Completa tus 3 secciones para desbloquear `/home`.</h1>
          <p className={styles.sectionText}>
            Si recargas durante el cuestionario seguiras viendo este flujo. El acceso al home se
            resuelve contra backend y el borrador local evita que pierdas tus respuestas.
          </p>
          {draftNotice ? <p className={styles.helperText}>{draftNotice}</p> : null}
        </Reveal>

        <Reveal delay={90}>
          <form className={styles.questionnairePanel} onSubmit={handleSubmit}>
            <div className={styles.progressBar}>
              <span style={{ width: `${completion}%` }} />
            </div>
            <span className={styles.sectionBadge}>Cuestionario inicial</span>
            <h2 className={styles.sectionTitle}>{stepTitles[step]}</h2>
            <p className={styles.sectionText}>
              Cada bloque refina metas, calculo calorico y personalizacion alimentaria.
            </p>

            {loading ? (
              <div className={styles.loadingColumn}>
                <div className={styles.loadingLine} />
                <div className={styles.loadingLine} />
                <div className={styles.loadingLineShort} />
              </div>
            ) : (
              <div className={styles.stepCarousel}>
                <div className={styles.stepTrack} style={{ transform: `translateX(-${step * 100}%)` }}>
                  <section className={styles.stepCard}>
                    <div className={styles.gridTwo}>
                      <label className={styles.field}>
                        <span>Nombre</span>
                        <input
                          className={styles.input}
                          value={form.firstName}
                          onChange={(event) => updateField("firstName", event.target.value)}
                          required
                        />
                      </label>
                      <label className={styles.field}>
                        <span>Apellido</span>
                        <input
                          className={styles.input}
                          value={form.lastName}
                          onChange={(event) => updateField("lastName", event.target.value)}
                          required
                        />
                      </label>
                    </div>

                    <div className={styles.optionGrid}>
                      {goalOptions.map(({ value, label, icon: Icon }) => (
                        <button
                          key={value}
                          type="button"
                          className={form.mainGoal === value ? styles.optionCardActive : styles.optionCard}
                          onClick={() => updateField("mainGoal", value)}
                        >
                          <Icon className={styles.cardIcon} />
                          <span>{label}</span>
                        </button>
                      ))}
                    </div>

                    <div className={styles.gridTwo}>
                      <label className={styles.field}>
                        <span>Nivel de actividad</span>
                        <select
                          className={styles.select}
                          value={form.activityLevel}
                          onChange={(event) => updateField("activityLevel", event.target.value)}
                        >
                          {activityOptions.map((item) => (
                            <option key={item.value} value={item.value}>
                              {item.label}
                            </option>
                          ))}
                        </select>
                      </label>

                      <label className={styles.field}>
                        <span>Dias de actividad por semana</span>
                        <input
                          className={styles.input}
                          type="number"
                          min="0"
                          max="7"
                          value={form.trainingDaysPerWeek ?? ""}
                          onChange={(event) =>
                            updateField("trainingDaysPerWeek", numberValue(event.target.value))
                          }
                        />
                      </label>
                    </div>
                  </section>

                  <section className={styles.stepCard}>
                    <div className={styles.gridThree}>
                      <label className={styles.field}>
                        <span>Edad</span>
                        <input
                          className={styles.input}
                          type="number"
                          min="1"
                          value={form.age ?? ""}
                          onChange={(event) => updateField("age", numberValue(event.target.value))}
                        />
                      </label>
                      <label className={styles.field}>
                        <span>Peso actual (kg)</span>
                        <input
                          className={styles.input}
                          inputMode="decimal"
                          value={form.currentWeightKg ?? ""}
                          onChange={(event) =>
                            updateField("currentWeightKg", numberValue(event.target.value))
                          }
                        />
                      </label>
                      <label className={styles.field}>
                        <span>Altura (cm)</span>
                        <input
                          className={styles.input}
                          inputMode="decimal"
                          value={form.heightCm ?? ""}
                          onChange={(event) => updateField("heightCm", numberValue(event.target.value))}
                        />
                      </label>
                    </div>

                    <div className={styles.gridTwo}>
                      <label className={styles.field}>
                        <span>Peso objetivo (kg)</span>
                        <input
                          className={styles.input}
                          inputMode="decimal"
                          value={form.targetWeightKg ?? ""}
                          onChange={(event) =>
                            updateField("targetWeightKg", numberValue(event.target.value))
                          }
                        />
                      </label>
                      <label className={styles.field}>
                        <span>Sexo o genero</span>
                        <select
                          className={styles.select}
                          value={form.gender}
                          onChange={(event) => updateField("gender", event.target.value)}
                        >
                          {genderOptions.map((item) => (
                            <option key={item.value} value={item.value}>
                              {item.label}
                            </option>
                          ))}
                        </select>
                      </label>
                    </div>

                    <div className={styles.gridTwo}>
                      <label className={styles.field}>
                        <span>Restricciones alimenticias</span>
                        <input
                          className={styles.input}
                          value={form.foodRestrictions}
                          onChange={(event) => updateField("foodRestrictions", event.target.value)}
                        />
                      </label>
                      <label className={styles.field}>
                        <span>Alergias</span>
                        <input
                          className={styles.input}
                          value={form.allergies}
                          onChange={(event) => updateField("allergies", event.target.value)}
                        />
                      </label>
                    </div>

                    <label className={styles.field}>
                      <span>Preferencias alimenticias</span>
                      <textarea
                        className={styles.textarea}
                        value={form.preferredFoods}
                        onChange={(event) => updateField("preferredFoods", event.target.value)}
                      />
                    </label>

                    <label className={styles.field}>
                      <span>Alimentos que no te gustan</span>
                      <textarea
                        className={styles.textarea}
                        value={form.dislikedFoods}
                        onChange={(event) => updateField("dislikedFoods", event.target.value)}
                      />
                    </label>
                  </section>

                  <section className={styles.stepCard}>
                    <div className={styles.gridTwo}>
                      <label className={styles.field}>
                        <span>Tipo de alimentacion</span>
                        <select
                          className={styles.select}
                          value={form.dietType}
                          onChange={(event) => updateField("dietType", event.target.value)}
                        >
                          {dietOptions.map((item) => (
                            <option key={item.value} value={item.value}>
                              {item.label}
                            </option>
                          ))}
                        </select>
                      </label>

                      <label className={styles.field}>
                        <span>Notas de salud</span>
                        <input
                          className={styles.input}
                          value={form.healthNotes}
                          onChange={(event) => updateField("healthNotes", event.target.value)}
                        />
                      </label>
                    </div>

                    <div className={styles.goalPreviewCardInline}>
                      <div className={styles.goalPreviewHeader}>
                        <SparkIcon className={styles.cardIcon} />
                        <div>
                          <strong>Calculo desde backend</strong>
                          <span>Usa `/onboarding/calculate-goals` para estimar IMC y macros.</span>
                        </div>
                      </div>

                      <button
                        type="button"
                        className={styles.secondaryButton}
                        onClick={handleGoalCalculation}
                        disabled={calculating}
                      >
                        {calculating ? "Calculando..." : "Recalcular metas"}
                      </button>
                    </div>
                  </section>
                </div>
              </div>
            )}

            {error ? (
              <p className={styles.formError} role="alert">
                {error}
              </p>
            ) : null}

            <div className={styles.questionnaireActions}>
              <button
                type="button"
                className={styles.secondaryButton}
                onClick={() => setStep((current) => Math.max(0, current - 1))}
                disabled={step === 0 || loading}
              >
                Anterior
              </button>

              {step < stepTitles.length - 1 ? (
                <button type="button" className={styles.primaryButton} onClick={goNextStep} disabled={loading}>
                  <span>{step === 1 ? "Continuar al resumen" : "Siguiente"}</span>
                  <ArrowRightIcon className={styles.buttonIcon} />
                </button>
              ) : (
                <button type="submit" className={styles.primaryButton} disabled={submitting || loading}>
                  <span>{submitting ? "Guardando..." : "Finalizar onboarding"}</span>
                  <ArrowRightIcon className={styles.buttonIcon} />
                </button>
              )}
            </div>
          </form>
        </Reveal>

        <Reveal className={styles.registerSummarySection} delay={150}>
          <div className={styles.sectionHeadingCompact}>
            <span className={styles.sectionBadge}>Resumen editable</span>
            <h2>Vuelve a cualquier bloque antes de finalizar.</h2>
            <p className={styles.sectionText}>
              Cada tarjeta te deja regresar a la seccion correspondiente sin perder el contexto.
            </p>
          </div>

          <div className={styles.registerSummaryGrid}>
            <button type="button" className={styles.summaryCardButton} onClick={() => setStep(0)}>
              <article className={styles.summaryCard}>
                <div className={styles.goalPreviewHeader}>
                  <DumbbellIcon className={styles.cardIcon} />
                  <div>
                    <strong>1. Datos generales</strong>
                    <span>Nombre, objetivo y ritmo semanal.</span>
                  </div>
                </div>
                <div className={styles.summaryList}>
                  <article>
                    <BarChartIcon className={styles.cardIcon} />
                    <div>
                      <strong>Objetivo</strong>
                      <span>{valueOrFallback(goalOptions.find((item) => item.value === form.mainGoal)?.label)}</span>
                    </div>
                  </article>
                  <article>
                    <CheckCircleIcon className={styles.cardIcon} />
                    <div>
                      <strong>Actividad</strong>
                      <span>
                        {valueOrFallback(activityOptions.find((item) => item.value === form.activityLevel)?.label)} ·{" "}
                        {valueOrFallback(form.trainingDaysPerWeek)} dias
                      </span>
                    </div>
                  </article>
                </div>
              </article>
            </button>

            <button type="button" className={styles.summaryCardButton} onClick={() => setStep(1)}>
              <article className={styles.summaryCard}>
                <div className={styles.goalPreviewHeader}>
                  <BarChartIcon className={styles.cardIcon} />
                  <div>
                    <strong>2. Datos fisicos</strong>
                    <span>Edad, peso, altura y preferencias base.</span>
                  </div>
                </div>
                <div className={styles.goalMetrics}>
                  <article>
                    <span>Edad</span>
                    <strong>{valueOrFallback(form.age)}</strong>
                  </article>
                  <article>
                    <span>Peso actual</span>
                    <strong>{valueOrFallback(form.currentWeightKg)}</strong>
                  </article>
                  <article>
                    <span>Peso objetivo</span>
                    <strong>{valueOrFallback(form.targetWeightKg)}</strong>
                  </article>
                  <article>
                    <span>Altura</span>
                    <strong>{valueOrFallback(form.heightCm)}</strong>
                  </article>
                </div>
              </article>
            </button>

            <button type="button" className={styles.summaryCardButton} onClick={() => setStep(2)}>
              <article className={styles.summaryCard}>
                <div className={styles.goalPreviewHeader}>
                  <UtensilsIcon className={styles.cardIcon} />
                  <div>
                    <strong>3. Preferencias y resumen</strong>
                    <span>Restricciones, tipo de dieta y notas finales.</span>
                  </div>
                </div>
                <div className={styles.summaryList}>
                  <article>
                    <UtensilsIcon className={styles.cardIcon} />
                    <div>
                      <strong>Preferidos</strong>
                      <span>{valueOrFallback(form.preferredFoods, "Sin preferencias aun")}</span>
                    </div>
                  </article>
                  <article>
                    <SparkIcon className={styles.cardIcon} />
                    <div>
                      <strong>Restricciones y alergias</strong>
                      <span>
                        {valueOrFallback(form.foodRestrictions, "Sin restricciones")} ·{" "}
                        {valueOrFallback(form.allergies, "Sin alergias")}
                      </span>
                    </div>
                  </article>
                </div>
              </article>
            </button>

            <article className={styles.goalPreviewCard}>
              <div className={styles.goalPreviewHeader}>
                <SparkIcon className={styles.cardIcon} />
                <div>
                  <strong>Calculo estimado</strong>
                  <span>Editable: si cambias datos, puedes recalcular antes de terminar.</span>
                </div>
              </div>

              {calculating ? (
                <div className={styles.loadingMetricGrid} aria-hidden="true">
                  <div className={styles.loadingMetricCard} />
                  <div className={styles.loadingMetricCard} />
                  <div className={styles.loadingMetricCard} />
                  <div className={styles.loadingMetricCard} />
                </div>
              ) : goalPreview ? (
                <div className={styles.goalMetrics}>
                  <article>
                    <span>IMC estimado</span>
                    <strong>{valueOrFallback(goalPreview.imc)}</strong>
                  </article>
                  <article>
                    <span>Calorias</span>
                    <strong>{valueOrFallback(goalPreview.recommendedCalories)}</strong>
                  </article>
                  <article>
                    <span>Proteina</span>
                    <strong>{valueOrFallback(goalPreview.recommendedProteinG)} g</strong>
                  </article>
                  <article>
                    <span>Carbohidratos</span>
                    <strong>{valueOrFallback(goalPreview.recommendedCarbsG)} g</strong>
                  </article>
                  <article>
                    <span>Grasas</span>
                    <strong>{valueOrFallback(goalPreview.recommendedFatG)} g</strong>
                  </article>
                  <article>
                    <span>Objetivo sugerido</span>
                    <strong>{valueOrFallback(goalPreview.suggestedGoal)}</strong>
                  </article>
                </div>
              ) : (
                <p className={styles.sectionText}>
                  El resumen aparecera aqui cuando completes los datos fisicos esenciales.
                </p>
              )}
            </article>
          </div>
        </Reveal>
      </section>
    </main>
  );
}
