"use client";

import { FormEvent, useEffect, useState } from "react";
import { createRecipe, getFoods, getRecipes, getUsers } from "@/services/api";
import type { Food, Recipe, User } from "@/types/domain";

const initialForm = {
  name: "",
  description: "",
  instructions: "",
};

export function Dashboard() {
  const [foods, setFoods] = useState<Food[]>([]);
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [foodSearch, setFoodSearch] = useState("");
  const [recipeSearch, setRecipeSearch] = useState("");
  const [selectedIngredients, setSelectedIngredients] = useState<number[]>([]);
  const [form, setForm] = useState(initialForm);
  const [status, setStatus] = useState<string>("Cargando datos iniciales...");
  const [error, setError] = useState<string>("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function loadInitialData() {
    try {
      setError("");
      setStatus("Sincronizando alimentos, recetas y usuarios...");
      const [foodData, recipeData, userData] = await Promise.all([
        getFoods(),
        getRecipes(),
        getUsers(),
      ]);
      setFoods(foodData);
      setRecipes(recipeData);
      setUsers(userData);
      setStatus("Datos listos para explorar y crear recetas.");
    } catch (loadError) {
      setError(loadError instanceof Error ? loadError.message : "No fue posible cargar la informacion.");
      setStatus("No se pudo conectar con la API.");
    }
  }

  useEffect(() => {
    void loadInitialData();
  }, []);

  async function handleFoodSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    try {
      const data = await getFoods(foodSearch);
      setFoods(data);
      setError("");
    } catch (searchError) {
      setError(searchError instanceof Error ? searchError.message : "No fue posible buscar alimentos.");
    }
  }

  async function handleRecipeSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    try {
      const data = await getRecipes(recipeSearch);
      setRecipes(data);
      setError("");
    } catch (searchError) {
      setError(searchError instanceof Error ? searchError.message : "No fue posible buscar recetas.");
    }
  }

  function toggleIngredient(id: number) {
    setSelectedIngredients((current) =>
      current.includes(id) ? current.filter((item) => item !== id) : [...current, id],
    );
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!users.length) {
      setError("No hay usuarios disponibles para asociar la receta.");
      return;
    }

    if (selectedIngredients.length === 0) {
      setError("Selecciona al menos un alimento como ingrediente.");
      return;
    }

    try {
      setIsSubmitting(true);
      setError("");
      const createdRecipe = await createRecipe({
        ...form,
        ingredientIds: selectedIngredients,
        idUser: users[0].id,
      });
      setRecipes((current) => [createdRecipe, ...current]);
      setForm(initialForm);
      setSelectedIngredients([]);
      setStatus(`Receta "${createdRecipe.name}" creada correctamente.`);
    } catch (submitError) {
      setError(submitError instanceof Error ? submitError.message : "No fue posible crear la receta.");
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="page-shell">
      <section className="hero">
        <div className="hero-copy">
          <span className="eyebrow">AppFoodSpring</span>
          <h1>Explora alimentos, sigue recetas y conecta el frontend con Spring Boot.</h1>
          <p>
            Esta version en Next.js reemplaza la antigua interfaz HTML y usa la API del monorepo
            para consultar alimentos, revisar recetas existentes y crear nuevas combinaciones.
          </p>
          <div className="hero-stats">
            <article>
              <strong>{foods.length}</strong>
              <span>alimentos visibles</span>
            </article>
            <article>
              <strong>{recipes.length}</strong>
              <span>recetas cargadas</span>
            </article>
            <article>
              <strong>{users.length}</strong>
              <span>usuarios activos</span>
            </article>
          </div>
        </div>
        <aside className="status-panel">
          <h2>Estado de integracion</h2>
          <p>{status}</p>
          <p className={error ? "message error" : "message"}>{error || "Sin errores de conexion."}</p>
          <button className="ghost-button" type="button" onClick={() => void loadInitialData()}>
            Recargar datos
          </button>
        </aside>
      </section>

      <section className="dashboard-grid">
        <article className="panel">
          <div className="panel-heading">
            <h2>Alimentos</h2>
            <p>Busca por nombre o revisa la informacion nutrimental disponible.</p>
          </div>
          <form className="search-row" onSubmit={handleFoodSearch}>
            <input
              value={foodSearch}
              onChange={(event) => setFoodSearch(event.target.value)}
              placeholder="Ej. avocado"
            />
            <button type="submit">Buscar</button>
          </form>
          <div className="card-list">
            {foods.map((food) => (
              <article key={food.id} className="info-card">
                <div className="tag">{food.category}</div>
                <h3>{food.name}</h3>
                <p>{food.country}</p>
                <dl>
                  <div>
                    <dt>Calorias</dt>
                    <dd>{food.calories}</dd>
                  </div>
                  <div>
                    <dt>Proteina</dt>
                    <dd>{food.protein} g</dd>
                  </div>
                  <div>
                    <dt>Carbohidratos</dt>
                    <dd>{food.carb} g</dd>
                  </div>
                  <div>
                    <dt>Grasa</dt>
                    <dd>{food.fat} g</dd>
                  </div>
                </dl>
              </article>
            ))}
          </div>
        </article>

        <article className="panel">
          <div className="panel-heading">
            <h2>Recetas</h2>
            <p>Consulta ideas existentes y revisa el resumen nutrimental calculado por el backend.</p>
          </div>
          <form className="search-row" onSubmit={handleRecipeSearch}>
            <input
              value={recipeSearch}
              onChange={(event) => setRecipeSearch(event.target.value)}
              placeholder="Ej. chicken bowl"
            />
            <button type="submit">Buscar</button>
          </form>
          <div className="recipe-list">
            {recipes.map((recipe) => (
              <article key={recipe.id} className="recipe-card">
                <div className="recipe-head">
                  <h3>{recipe.name}</h3>
                  <span>{recipe.calories.toFixed(1)} kcal</span>
                </div>
                <p>{recipe.description}</p>
                <p className="muted">{recipe.instructions}</p>
                <div className="chip-row">
                  {recipe.ingredients.map((ingredient) => (
                    <span key={`${recipe.id}-${ingredient.id}`} className="chip">
                      {ingredient.name}
                    </span>
                  ))}
                </div>
              </article>
            ))}
          </div>
        </article>
      </section>

      <section className="panel form-panel">
        <div className="panel-heading">
          <h2>Nueva receta</h2>
          <p>Selecciona ingredientes y guarda una receta usando el primer usuario disponible.</p>
        </div>
        <form className="recipe-form" onSubmit={handleSubmit}>
          <div className="input-grid">
            <label>
              Nombre
              <input
                value={form.name}
                onChange={(event) => setForm((current) => ({ ...current, name: event.target.value }))}
                placeholder="Bowl de energia"
                required
              />
            </label>
            <label>
              Descripcion
              <input
                value={form.description}
                onChange={(event) => setForm((current) => ({ ...current, description: event.target.value }))}
                placeholder="Receta rapida y balanceada"
                required
              />
            </label>
          </div>
          <label>
            Instrucciones
            <textarea
              value={form.instructions}
              onChange={(event) => setForm((current) => ({ ...current, instructions: event.target.value }))}
              placeholder="Mezcla, cocina y sirve."
              rows={4}
              required
            />
          </label>

          <div className="ingredient-picker">
            {foods.map((food) => (
              <button
                key={food.id}
                type="button"
                className={selectedIngredients.includes(food.id) ? "ingredient-chip active" : "ingredient-chip"}
                onClick={() => toggleIngredient(food.id)}
              >
                {food.name}
              </button>
            ))}
          </div>

          <div className="form-footer">
            <p>
              Usuario asignado:
              {" "}
              <strong>{users[0]?.name ?? "Sin usuario"}</strong>
            </p>
            <button type="submit" disabled={isSubmitting}>
              {isSubmitting ? "Guardando..." : "Crear receta"}
            </button>
          </div>
        </form>
      </section>
    </main>
  );
}
