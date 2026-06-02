"use client";

import {
  DEFAULT_API_URL,
  clearStoredSession,
  getCurrentUser,
  getExercises,
  getFridgeRecipeMatches,
  getOnboarding,
  getRecipePosts,
  getStoredSession,
  login,
  logout,
  normalizeBaseUrl,
  register,
  requestJson,
  saveStoredSession,
  searchFoods,
  updateCurrentUser,
  createOnboarding,
  updateOnboarding,
  calculateGoals,
  refreshSession,
} from "@/services/api";
import type {
  AuthSession,
  Exercise,
  Food,
  FridgeRecipeMatch,
  GoalCalculation,
  OnboardingQuestionnaire,
  OnboardingQuestionnaireRequest,
  RecipePost,
  User,
} from "@/types/domain";

export const PRODUCT_API_URL_STORAGE_KEY = "appfoodspring-product-base-url";
export const ONBOARDING_DRAFT_STORAGE_KEY = "appfoodspring-product-onboarding-draft";
export const AI_COACH_NAME = "Nefir";

export type AuthPayload = {
  email: string;
  password: string;
};

export type RegisterPayload = AuthPayload & {
  firstName: string;
  lastName: string;
};

export type ProductOnboardingForm = {
  firstName: string;
  lastName: string;
  mainGoal: string;
  currentWeightKg: number | null;
  targetWeightKg: number | null;
  heightCm: number | null;
  age: number | null;
  gender: string;
  activityLevel: string;
  trainingDaysPerWeek: number | null;
  dietType: string;
  foodRestrictions: string;
  allergies: string;
  preferredFoods: string;
  dislikedFoods: string;
  healthNotes: string;
};

export type GoalPreview = {
  imc?: number | null;
  recommendedCalories?: number | null;
  recommendedProteinG?: number | null;
  recommendedCarbsG?: number | null;
  recommendedFatG?: number | null;
  suggestedGoal?: string | null;
};

export type UserProfileSummary = {
  id: string;
  email: string;
  displayName: string;
  firstName: string;
  lastName: string;
  goal?: string | null;
  activityLevel?: string | null;
  calorieGoal?: number | null;
  proteinGoal?: number | null;
  carbsGoal?: number | null;
  fatGoal?: number | null;
};

export type RecipeMatchCard = {
  id: string;
  title: string;
  matchedIngredients: number;
  totalIngredients: number;
  matchedFoodNames: string[];
};

export type FoodLogEntry = {
  id: string;
  foodId: string;
  foodName: string;
  logDate: string;
  mealType: string;
  quantity: number | null;
  unit: string | null;
  calories: number | null;
  proteinG: number | null;
  carbsG: number | null;
  fatG: number | null;
};

export type DailyFoodSummary = {
  date: string;
  totalCalories: number | null;
  totalProteinG: number | null;
  totalCarbsG: number | null;
  totalFatG: number | null;
  items: FoodLogEntry[];
};

export type HomeSnapshot = {
  profile: UserProfileSummary | null;
  foods: Food[];
  exercises: Exercise[];
  recipePosts: RecipePost[];
  fridgeMatches: RecipeMatchCard[];
  fridgeItems: Array<{ id: string; name: string; quantity?: number | null; unit?: string | null }>;
  dailySummary: DailyFoodSummary | null;
};

export type AuthResolution = {
  session: AuthSession | null;
  destination: "/home" | "/register" | "/";
};

export type OnboardingBootstrap = {
  session: AuthSession;
  user: User;
  questionnaire: OnboardingQuestionnaire | null;
  completed: boolean;
};

export type FoodLogCreateInput = {
  foodId: string;
  mealType: string;
  quantity: number;
  unit: string;
  logDate: string;
  calories?: number | null;
  proteinG?: number | null;
  carbsG?: number | null;
  fatG?: number | null;
};

const placeholderSummary: DailyFoodSummary = {
  date: "",
  totalCalories: null,
  totalProteinG: null,
  totalCarbsG: null,
  totalFatG: null,
  items: [],
};

const baseOnboardingForm: ProductOnboardingForm = {
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

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null && !Array.isArray(value);
}

function asNumber(value: unknown): number | null {
  if (typeof value === "number" && Number.isFinite(value)) {
    return value;
  }

  if (typeof value === "string") {
    const parsed = Number(value);
    return Number.isFinite(parsed) ? parsed : null;
  }

  return null;
}

function sanitizeBaseUrl(baseUrl: string): string {
  return normalizeBaseUrl(baseUrl || DEFAULT_API_URL);
}

function isUnauthorizedError(error: unknown): boolean {
  return isRecord(error) && typeof error.status === "number" && [401, 403].includes(error.status);
}

function mapGoalPreview(goal: GoalCalculation): GoalPreview {
  return {
    imc: asNumber(goal.imc),
    recommendedCalories: asNumber(goal.recommendedCalories),
    recommendedProteinG: asNumber(goal.recommendedProteinG),
    recommendedCarbsG: asNumber(goal.recommendedCarbsG),
    recommendedFatG: asNumber(goal.recommendedFatG),
    suggestedGoal: goal.suggestedGoal,
  };
}

function toDisplayName(user: User): string {
  const firstName = user.profile?.firstName?.trim() ?? "";
  const lastName = user.profile?.lastName?.trim() ?? "";
  return [firstName, lastName].filter(Boolean).join(" ").trim() || user.email;
}

function toProfileSummary(user: User): UserProfileSummary {
  return {
    id: user.id,
    email: user.email,
    displayName: toDisplayName(user),
    firstName: user.profile?.firstName?.trim() ?? "",
    lastName: user.profile?.lastName?.trim() ?? "",
    goal: user.profile?.goal ?? null,
    activityLevel: user.profile?.activityLevel ?? null,
    calorieGoal: asNumber(user.profile?.dailyCalorieGoal),
    proteinGoal: asNumber(user.profile?.dailyProteinGoal),
    carbsGoal: asNumber(user.profile?.dailyCarbsGoal),
    fatGoal: asNumber(user.profile?.dailyFatGoal),
  };
}

function mapRecipeMatches(matches: FridgeRecipeMatch[]): RecipeMatchCard[] {
  return matches.map((item) => ({
    id: item.recipeId,
    title: item.recipeTitle,
    matchedIngredients: item.matchedIngredients,
    totalIngredients: item.totalIngredients,
    matchedFoodNames: item.matchedFoodNames,
  }));
}

function toFoodLogEntry(value: unknown): FoodLogEntry | null {
  if (!isRecord(value) || typeof value.id !== "string" || typeof value.foodId !== "string") {
    return null;
  }

  return {
    id: value.id,
    foodId: value.foodId,
    foodName: typeof value.foodName === "string" ? value.foodName : "Alimento",
    logDate: typeof value.logDate === "string" ? value.logDate : "",
    mealType: typeof value.mealType === "string" ? value.mealType : "meal",
    quantity: asNumber(value.quantity),
    unit: typeof value.unit === "string" ? value.unit : null,
    calories: asNumber(value.calories),
    proteinG: asNumber(value.proteinG),
    carbsG: asNumber(value.carbsG),
    fatG: asNumber(value.fatG),
  };
}

function toDailyFoodSummary(value: unknown): DailyFoodSummary | null {
  if (!isRecord(value)) {
    return null;
  }

  const items = Array.isArray(value.items)
    ? value.items.map(toFoodLogEntry).filter((item): item is FoodLogEntry => item !== null)
    : [];

  return {
    date: typeof value.date === "string" ? value.date : "",
    totalCalories: asNumber(value.totalCalories),
    totalProteinG: asNumber(value.totalProteinG),
    totalCarbsG: asNumber(value.totalCarbsG),
    totalFatG: asNumber(value.totalFatG),
    items,
  };
}

function questionnaireToForm(
  user: User,
  questionnaire: OnboardingQuestionnaire | null,
): ProductOnboardingForm {
  return {
    firstName: user.profile?.firstName ?? "",
    lastName: user.profile?.lastName ?? "",
    mainGoal: questionnaire?.mainGoal ?? user.profile?.goal ?? baseOnboardingForm.mainGoal,
    currentWeightKg: questionnaire?.currentWeightKg ?? user.profile?.weightKg ?? null,
    targetWeightKg: questionnaire?.targetWeightKg ?? user.profile?.targetWeightKg ?? null,
    heightCm: questionnaire?.heightCm ?? user.profile?.heightCm ?? null,
    age: questionnaire?.age ?? user.profile?.age ?? null,
    gender: questionnaire?.gender ?? user.profile?.gender ?? baseOnboardingForm.gender,
    activityLevel:
      questionnaire?.activityLevel ?? user.profile?.activityLevel ?? baseOnboardingForm.activityLevel,
    trainingDaysPerWeek:
      questionnaire?.trainingDaysPerWeek ?? baseOnboardingForm.trainingDaysPerWeek,
    dietType: questionnaire?.dietType ?? baseOnboardingForm.dietType,
    foodRestrictions: questionnaire?.foodRestrictions ?? "",
    allergies: questionnaire?.allergies ?? "",
    preferredFoods: questionnaire?.preferredFoods ?? "",
    dislikedFoods: questionnaire?.dislikedFoods ?? "",
    healthNotes: questionnaire?.healthNotes ?? "",
  };
}

function normalizeOnboardingRequest(form: ProductOnboardingForm): OnboardingQuestionnaireRequest {
  return {
    mainGoal: form.mainGoal,
    currentWeightKg: form.currentWeightKg ?? undefined,
    targetWeightKg: form.targetWeightKg ?? undefined,
    heightCm: form.heightCm ?? undefined,
    age: form.age ?? undefined,
    gender: form.gender || undefined,
    activityLevel: form.activityLevel || undefined,
    trainingDaysPerWeek: form.trainingDaysPerWeek ?? undefined,
    dietType: form.dietType || undefined,
    foodRestrictions: form.foodRestrictions || undefined,
    allergies: form.allergies || undefined,
    preferredFoods: form.preferredFoods || undefined,
    dislikedFoods: form.dislikedFoods || undefined,
    healthNotes: form.healthNotes || undefined,
  };
}

export function isOnboardingComplete(questionnaire: OnboardingQuestionnaire | null): boolean {
  if (!questionnaire?.completedAt) {
    return false;
  }

  return Boolean(
    questionnaire.mainGoal &&
      questionnaire.activityLevel &&
      questionnaire.trainingDaysPerWeek !== null &&
      questionnaire.trainingDaysPerWeek !== undefined &&
      (questionnaire.age ?? 0) > 0 &&
      (questionnaire.currentWeightKg ?? 0) > 0 &&
      (questionnaire.heightCm ?? 0) > 0,
  );
}

export function todayDateString() {
  return new Date().toISOString().slice(0, 10);
}

export function readPreferredApiUrl(): string {
  if (typeof window === "undefined") {
    return DEFAULT_API_URL;
  }

  const stored = window.localStorage.getItem(PRODUCT_API_URL_STORAGE_KEY);
  return sanitizeBaseUrl(stored ?? DEFAULT_API_URL);
}

export function writePreferredApiUrl(baseUrl: string): string {
  const normalized = sanitizeBaseUrl(baseUrl);

  if (typeof window !== "undefined") {
    window.localStorage.setItem(PRODUCT_API_URL_STORAGE_KEY, normalized);
  }

  return normalized;
}

export function readOnboardingDraft(): ProductOnboardingForm | null {
  if (typeof window === "undefined") {
    return null;
  }

  const raw = window.localStorage.getItem(ONBOARDING_DRAFT_STORAGE_KEY);
  if (!raw) {
    return null;
  }

  try {
    const parsed = JSON.parse(raw) as unknown;
    if (!isRecord(parsed)) {
      return null;
    }

    return {
      ...baseOnboardingForm,
      ...parsed,
    } as ProductOnboardingForm;
  } catch {
    return null;
  }
}

export function writeOnboardingDraft(form: ProductOnboardingForm) {
  if (typeof window === "undefined") {
    return;
  }

  window.localStorage.setItem(ONBOARDING_DRAFT_STORAGE_KEY, JSON.stringify(form));
}

export function clearOnboardingDraft() {
  if (typeof window === "undefined") {
    return;
  }

  window.localStorage.removeItem(ONBOARDING_DRAFT_STORAGE_KEY);
}

export async function loginWithBackend(baseUrl: string, payload: AuthPayload): Promise<AuthSession> {
  const session = await login(payload, sanitizeBaseUrl(baseUrl));
  saveStoredSession(session);
  return session;
}

export async function registerWithBackend(
  baseUrl: string,
  payload: RegisterPayload,
): Promise<AuthSession> {
  const session = await register(payload, sanitizeBaseUrl(baseUrl));
  saveStoredSession(session);
  return session;
}

export async function refreshSessionIfNeeded(
  baseUrl: string,
  session: AuthSession | null,
): Promise<AuthSession | null> {
  if (!session) {
    return null;
  }

  try {
    await getCurrentUser(session, sanitizeBaseUrl(baseUrl));
    return session;
  } catch (error) {
    if (!isUnauthorizedError(error)) {
      throw error;
    }
  }

  try {
    const nextSession = await refreshSession(session, sanitizeBaseUrl(baseUrl));
    saveStoredSession(nextSession);
    return nextSession;
  } catch {
    clearStoredSession();
    return null;
  }
}

export async function resolvePostAuthDestination(
  baseUrl: string,
  session: AuthSession,
): Promise<AuthResolution> {
  const safeSession = await refreshSessionIfNeeded(baseUrl, session);
  if (!safeSession) {
    return {
      session: null,
      destination: "/",
    };
  }

  const questionnaire = await getOnboarding(safeSession, sanitizeBaseUrl(baseUrl)).catch(() => null);

  return {
    session: safeSession,
    destination: isOnboardingComplete(questionnaire) ? "/home" : "/register",
  };
}

export async function loadOnboardingBootstrap(
  baseUrl: string,
  session: AuthSession | null,
): Promise<OnboardingBootstrap | null> {
  const safeSession = await refreshSessionIfNeeded(baseUrl, session);
  if (!safeSession) {
    return null;
  }

  const [user, questionnaire] = await Promise.all([
    getCurrentUser(safeSession, sanitizeBaseUrl(baseUrl)),
    getOnboarding(safeSession, sanitizeBaseUrl(baseUrl)).catch(() => null),
  ]);

  return {
    session: safeSession,
    user,
    questionnaire,
    completed: isOnboardingComplete(questionnaire),
  };
}

export function composeOnboardingForm(
  user: User,
  questionnaire: OnboardingQuestionnaire | null,
): ProductOnboardingForm {
  const remote = questionnaireToForm(user, questionnaire);
  const draft = readOnboardingDraft();
  return draft ? { ...remote, ...draft } : remote;
}

export async function saveOnboardingFlow(
  baseUrl: string,
  session: AuthSession,
  form: ProductOnboardingForm,
  hasExistingQuestionnaire: boolean,
) {
  const resolvedBaseUrl = sanitizeBaseUrl(baseUrl);

  await updateCurrentUser(
    session,
    {
      firstName: form.firstName,
      lastName: form.lastName,
    },
    resolvedBaseUrl,
  );

  const payload = normalizeOnboardingRequest(form);
  const questionnaire = hasExistingQuestionnaire
    ? await updateOnboarding(session, payload, resolvedBaseUrl)
    : await createOnboarding(session, payload, resolvedBaseUrl);

  clearOnboardingDraft();
  return questionnaire;
}

export async function calculateGoalPreview(
  baseUrl: string,
  session: AuthSession,
  form: ProductOnboardingForm,
): Promise<GoalPreview> {
  const preview = await calculateGoals(
    session,
    {
      weightKg: form.currentWeightKg ?? 0,
      targetWeightKg: form.targetWeightKg ?? form.currentWeightKg ?? 0,
      heightCm: form.heightCm ?? 0,
      age: form.age ?? 0,
      gender: form.gender,
      activityLevel: form.activityLevel,
      mainGoal: form.mainGoal,
    },
    sanitizeBaseUrl(baseUrl),
  );

  return mapGoalPreview(preview);
}

export async function loadDailyFoodSummary(
  baseUrl: string,
  session: AuthSession,
  date: string,
): Promise<DailyFoodSummary> {
  const result = await requestJson<unknown>({
    path: "/food-logs/summary",
    baseUrl: sanitizeBaseUrl(baseUrl),
    session,
    auth: "jwt",
    query: { date },
  }).catch(() => placeholderSummary);

  return toDailyFoodSummary(result) ?? placeholderSummary;
}

export async function searchFoodsForLog(
  baseUrl: string,
  session: AuthSession,
  query: string,
): Promise<Food[]> {
  return searchFoods(session, query, sanitizeBaseUrl(baseUrl));
}

export async function createFoodLogEntry(
  baseUrl: string,
  session: AuthSession,
  input: FoodLogCreateInput,
): Promise<FoodLogEntry | null> {
  const result = await requestJson<unknown>({
    path: "/food-logs",
    method: "POST",
    baseUrl: sanitizeBaseUrl(baseUrl),
    session,
    auth: "jwt",
    body: input,
  });

  return toFoodLogEntry(result);
}

export async function loadHomeSnapshot(
  baseUrl: string,
  session: AuthSession,
  date = todayDateString(),
): Promise<HomeSnapshot> {
  const resolvedBaseUrl = sanitizeBaseUrl(baseUrl);
  const [user, foods, exercises, recipePosts, fridgeItems, fridgeMatches, dailySummary] = await Promise.all([
    getCurrentUser(session, resolvedBaseUrl),
    requestJson<Food[]>({
      path: "/foods",
      baseUrl: resolvedBaseUrl,
      session,
      auth: "jwt",
    }).catch(() => []),
    getExercises(session, resolvedBaseUrl).catch(() => []),
    getRecipePosts(session, resolvedBaseUrl).catch(() => []),
    requestJson<Array<{ id: string; name: string; quantity?: number; unit?: string }>>({
      path: "/fridge/items",
      baseUrl: resolvedBaseUrl,
      session,
      auth: "jwt",
    }).catch(() => []),
    getFridgeRecipeMatches(session, resolvedBaseUrl).catch(() => []),
    loadDailyFoodSummary(resolvedBaseUrl, session, date),
  ]);

  return {
    profile: toProfileSummary(user),
    foods,
    exercises,
    recipePosts,
    fridgeItems,
    fridgeMatches: mapRecipeMatches(fridgeMatches),
    dailySummary,
  };
}

export async function logoutFromBackend(baseUrl: string, session: AuthSession | null) {
  if (session) {
    await logout(session, sanitizeBaseUrl(baseUrl)).catch(() => undefined);
  }

  clearStoredSession();
}

export function getActiveSession(): AuthSession | null {
  return getStoredSession();
}
