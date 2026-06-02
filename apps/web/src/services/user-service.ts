import { DEFAULT_API_URL, requestJson } from "@/services/api-client";
import type {
  AuthSession,
  GoalCalculation,
  GoalCalculationRequest,
  OnboardingQuestionnaire,
  OnboardingQuestionnaireRequest,
  UpdateProfileRequest,
  User,
  UserStats,
} from "@/types/domain";

export async function getCurrentUser(session: AuthSession, baseUrl = DEFAULT_API_URL): Promise<User> {
  return requestJson<User>({
    path: "/users/me",
    baseUrl,
    session,
    auth: "jwt",
  });
}

export async function updateCurrentUser(
  session: AuthSession,
  payload: UpdateProfileRequest,
  baseUrl = DEFAULT_API_URL,
): Promise<User> {
  return requestJson<User>({
    path: "/users/me",
    method: "PATCH",
    baseUrl,
    session,
    auth: "jwt",
    body: payload,
  });
}

export async function getUserStats(
  session: AuthSession,
  baseUrl = DEFAULT_API_URL,
): Promise<UserStats> {
  return requestJson<UserStats>({
    path: "/users/me/stats",
    baseUrl,
    session,
    auth: "jwt",
  });
}

export async function getOnboarding(
  session: AuthSession,
  baseUrl = DEFAULT_API_URL,
): Promise<OnboardingQuestionnaire | null> {
  return requestJson<OnboardingQuestionnaire | null>({
    path: "/onboarding/me",
    baseUrl,
    session,
    auth: "jwt",
  });
}

export async function createOnboarding(
  session: AuthSession,
  payload: OnboardingQuestionnaireRequest,
  baseUrl = DEFAULT_API_URL,
): Promise<OnboardingQuestionnaire> {
  return requestJson<OnboardingQuestionnaire>({
    path: "/onboarding",
    method: "POST",
    baseUrl,
    session,
    auth: "jwt",
    body: payload,
  });
}

export async function updateOnboarding(
  session: AuthSession,
  payload: OnboardingQuestionnaireRequest,
  baseUrl = DEFAULT_API_URL,
): Promise<OnboardingQuestionnaire> {
  return requestJson<OnboardingQuestionnaire>({
    path: "/onboarding/me",
    method: "PATCH",
    baseUrl,
    session,
    auth: "jwt",
    body: payload,
  });
}

export async function calculateGoals(
  session: AuthSession,
  payload: GoalCalculationRequest,
  baseUrl = DEFAULT_API_URL,
): Promise<GoalCalculation> {
  return requestJson<GoalCalculation>({
    path: "/onboarding/calculate-goals",
    method: "POST",
    baseUrl,
    session,
    auth: "jwt",
    body: payload,
  });
}
