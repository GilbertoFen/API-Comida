export type Food = {
  id: number;
  country: string;
  category: string;
  name: string;
  quantity: number;
  unit: number;
  calories: number;
  protein: number;
  carb: number;
  fat: number;
  sugar: number;
  sodium: number;
};

export type Recipe = {
  id: number;
  name: string;
  description: string;
  instructions: string;
  ingredients: Food[];
  calories: number;
  protein: number;
  carb: number;
  fat: number;
  sugar: number;
  sodium: number;
  userId: number;
};

export type User = {
  id: number;
  name: string;
  age: number;
  email: string;
  weight: number;
  height: number;
  exerciseLevel: number;
};

export type RecipePayload = {
  name: string;
  description: string;
  instructions: string;
  ingredientIds: number[];
  idUser: number;
};
