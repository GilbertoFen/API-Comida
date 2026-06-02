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
  user?: User | null;
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

export type UserProfile = {
  firstName: string | null;
  lastName: string | null;
  age: number | null;
  gender: string | null;
  weightKg: number | null;
  heightCm: number | null;
  activityLevel: string | null;
  goal: string | null;
  imc: number | null;
  targetWeightKg: number | null;
  dailyCalorieGoal: number | null;
  dailyProteinGoal: number | null;
  dailyCarbsGoal: number | null;
  dailyFatGoal: number | null;
};

export type User = {
  id: string;
  email: string;
  isActive: boolean;
  profile: UserProfile | null;
};

export type AuthResponse = {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  user: User;
};

export type LoginRequest = {
  email: string;
  password: string;
};

export type RegisterRequest = LoginRequest & {
  firstName: string;
  lastName: string;
};

export type UpdateProfileRequest = {
  firstName?: string;
  lastName?: string;
  age?: number;
  gender?: string;
  weightKg?: number;
  heightCm?: number;
  activityLevel?: string;
  goal?: string;
  targetWeightKg?: number;
  dailyCalorieGoal?: number;
  dailyProteinGoal?: number;
  dailyCarbsGoal?: number;
  dailyFatGoal?: number;
};

export type UserStats = {
  totalFoodLogs: number;
  totalRecipes: number;
  totalMealPlans: number;
  totalWorkoutPlans: number;
  totalWorkoutLogs: number;
  totalDailyNotes: number;
};

export type OnboardingQuestionnaireRequest = {
  mainGoal?: string;
  currentWeightKg?: number;
  targetWeightKg?: number;
  heightCm?: number;
  age?: number;
  gender?: string;
  activityLevel?: string;
  trainingDaysPerWeek?: number;
  dietType?: string;
  foodRestrictions?: string;
  allergies?: string;
  preferredFoods?: string;
  dislikedFoods?: string;
  healthNotes?: string;
};

export type OnboardingQuestionnaire = OnboardingQuestionnaireRequest & {
  id: string;
  completedAt: string | null;
};

export type GoalCalculationRequest = {
  weightKg: number;
  targetWeightKg: number;
  heightCm: number;
  age: number;
  gender: string;
  activityLevel: string;
  mainGoal: string;
};

export type GoalCalculation = {
  imc: number;
  recommendedCalories: number;
  recommendedProteinG: number;
  recommendedCarbsG: number;
  recommendedFatG: number;
  suggestedGoal: string;
};

export type Food = {
  id: string;
  name: string;
  brand: string | null;
  barcode: string | null;
  externalSource: string | null;
  externalId: string | null;
  category: string | null;
  servingSize: number | null;
  servingUnit: string | null;
  caloriesPer100g: number | null;
  proteinPer100g: number | null;
  carbsPer100g: number | null;
  fatPer100g: number | null;
  fiberPer100g: number | null;
  sugarPer100g: number | null;
  sodiumPer100g: number | null;
  verified: boolean | null;
};

export type Exercise = {
  id: string;
  name: string;
  muscleGroup: string | null;
  equipment: string | null;
  difficulty: string | null;
  description: string | null;
  recommendedSets: number | null;
  recommendedReps: string | null;
  recommendedTimeSeconds: number | null;
};

export type RecipePost = {
  id: string;
  recipeId: string;
  userId: string;
  title: string;
  content: string | null;
  likesCount: number;
  commentsCount: number;
};

export type FridgeRecipeMatch = {
  recipeId: string;
  recipeTitle: string;
  matchedIngredients: number;
  totalIngredients: number;
  matchedFoodNames: string[];
};

export type HomeFeed = {
  user: User;
  stats: UserStats;
  topRecipePosts: RecipePost[];
  fridgeMatches: FridgeRecipeMatch[];
  suggestedFoods: Food[];
  exercises: Exercise[];
};
