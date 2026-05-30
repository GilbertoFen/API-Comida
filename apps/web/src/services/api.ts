import type { Food, Recipe, RecipePayload, User } from "@/types/domain";

const API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api";

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_URL}${path}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...(init?.headers ?? {}),
    },
    cache: "no-store",
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || "Request failed");
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}

export async function getFoods(name?: string): Promise<Food[]> {
  const query = name?.trim() ? `?name=${encodeURIComponent(name.trim())}` : "";
  return request<Food[]>(`/food${query}`);
}

export async function getRecipes(name?: string): Promise<Recipe[]> {
  const query = name?.trim() ? `?name=${encodeURIComponent(name.trim())}` : "";
  return request<Recipe[]>(`/recipes${query}`);
}

export async function getUsers(): Promise<User[]> {
  return request<User[]>("/users");
}

export async function createRecipe(payload: RecipePayload): Promise<Recipe> {
  return request<Recipe>("/recipes", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}
