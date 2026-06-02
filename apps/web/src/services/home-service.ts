import { DEFAULT_API_URL, requestJson } from "@/services/api-client";
import type {
  AuthSession,
  Exercise,
  Food,
  FridgeRecipeMatch,
  HomeFeed,
  RecipePost,
} from "@/types/domain";
import { getCurrentUser, getUserStats } from "@/services/user-service";

export async function searchFoods(
  session: AuthSession,
  query: string,
  baseUrl = DEFAULT_API_URL,
): Promise<Food[]> {
  return requestJson<Food[]>({
    path: "/foods/search",
    baseUrl,
    session,
    auth: "jwt",
    query: { query },
  });
}

export async function getExercises(
  session: AuthSession,
  baseUrl = DEFAULT_API_URL,
): Promise<Exercise[]> {
  return requestJson<Exercise[]>({
    path: "/exercises",
    baseUrl,
    session,
    auth: "jwt",
  });
}

export async function getRecipePosts(
  session: AuthSession,
  baseUrl = DEFAULT_API_URL,
): Promise<RecipePost[]> {
  return requestJson<RecipePost[]>({
    path: "/recipe-posts",
    baseUrl,
    session,
    auth: "jwt",
  });
}

export async function getFridgeRecipeMatches(
  session: AuthSession,
  baseUrl = DEFAULT_API_URL,
): Promise<FridgeRecipeMatch[]> {
  return requestJson<FridgeRecipeMatch[]>({
    path: "/fridge/match-recipes",
    method: "POST",
    baseUrl,
    session,
    auth: "jwt",
  });
}

export async function getHomeFeed(
  session: AuthSession,
  baseUrl = DEFAULT_API_URL,
): Promise<HomeFeed> {
  const [user, stats, topRecipePosts, fridgeMatches, suggestedFoods, exercises] =
    await Promise.all([
      getCurrentUser(session, baseUrl),
      getUserStats(session, baseUrl),
      getRecipePosts(session, baseUrl),
      getFridgeRecipeMatches(session, baseUrl).catch(() => []),
      searchFoods(session, "protein", baseUrl).catch(() => []),
      getExercises(session, baseUrl).catch(() => []),
    ]);

  return {
    user,
    stats,
    topRecipePosts: [...topRecipePosts].sort((a, b) => b.likesCount - a.likesCount).slice(0, 6),
    fridgeMatches: fridgeMatches.slice(0, 4),
    suggestedFoods: suggestedFoods.slice(0, 6),
    exercises: exercises.slice(0, 6),
  };
}
