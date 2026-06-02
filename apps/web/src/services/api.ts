export {
  ApiError,
  clearStoredSession,
  DEFAULT_API_URL,
  executeEndpoint,
  extractSessionFromResponse,
  getStoredSession,
  normalizeBaseUrl,
  requestJson,
  saveStoredSession,
} from "@/services/api-client";
export { endpointGroups } from "@/services/api-playground";
export {
  getAuthenticatedUser,
  login,
  logout,
  refreshSession,
  register,
} from "@/services/auth-service";
export {
  getCurrentUser,
  getOnboarding,
  getUserStats,
  updateCurrentUser,
  createOnboarding,
  updateOnboarding,
  calculateGoals,
} from "@/services/user-service";
export {
  getExercises,
  getFridgeRecipeMatches,
  getHomeFeed,
  getRecipePosts,
  searchFoods,
} from "@/services/home-service";
