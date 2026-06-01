package com.demoapi.apicomida.services;

import com.demoapi.apicomida.dtos.RecipeDtos.RecipeIngredientRequest;
import com.demoapi.apicomida.dtos.RecipeDtos.RecipeIngredientResponse;
import com.demoapi.apicomida.dtos.RecipeDtos.RecipeRequest;
import com.demoapi.apicomida.dtos.RecipeDtos.RecipeResponse;
import com.demoapi.apicomida.exception.ApiException;
import com.demoapi.apicomida.models.Food;
import com.demoapi.apicomida.models.Recipe;
import com.demoapi.apicomida.models.RecipeIngredient;
import com.demoapi.apicomida.models.UserAccount;
import com.demoapi.apicomida.repositories.RecipeIngredientRepository;
import com.demoapi.apicomida.repositories.RecipeRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final CurrentUserService currentUserService;
    private final FoodService foodService;

    public RecipeService(
            RecipeRepository recipeRepository,
            RecipeIngredientRepository recipeIngredientRepository,
            CurrentUserService currentUserService,
            FoodService foodService
    ) {
        this.recipeRepository = recipeRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.currentUserService = currentUserService;
        this.foodService = foodService;
    }

    @Transactional
    public RecipeResponse create(RecipeRequest request) {
        Recipe recipe = new Recipe();
        recipe.setUser(currentUserService.requireCurrentUser());
        apply(recipe, request);
        return toResponse(recipeRepository.save(recipe));
    }

    public List<RecipeResponse> getAll() {
        return recipeRepository.findAll().stream().map(this::toResponse).toList();
    }

    public RecipeResponse getById(UUID id) {
        return toResponse(requireVisible(id));
    }

    public List<RecipeResponse> getPublicRecipes() {
        return recipeRepository.findByIsPublicTrueOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    public List<RecipeResponse> getMine() {
        return recipeRepository.findByUserOrderByCreatedAtDesc(currentUserService.requireCurrentUser())
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public RecipeResponse update(UUID id, RecipeRequest request) {
        Recipe recipe = requireOwned(id);
        apply(recipe, request);
        return toResponse(recipeRepository.save(recipe));
    }

    @Transactional
    public void delete(UUID id) {
        Recipe recipe = requireOwned(id);
        recipeIngredientRepository.deleteByRecipe(recipe);
        recipeRepository.delete(recipe);
    }

    @Transactional
    public RecipeIngredientResponse addIngredient(UUID recipeId, RecipeIngredientRequest request) {
        RecipeIngredient ingredient = new RecipeIngredient();
        ingredient.setRecipe(requireOwned(recipeId));
        applyIngredient(ingredient, request);
        RecipeIngredient saved = recipeIngredientRepository.save(ingredient);
        recalculate(saved.getRecipe());
        return toIngredientResponse(saved);
    }

    @Transactional
    public RecipeIngredientResponse updateIngredient(UUID recipeId, UUID ingredientId, RecipeIngredientRequest request) {
        Recipe recipe = requireOwned(recipeId);
        RecipeIngredient ingredient = recipeIngredientRepository.findByIdAndRecipe(ingredientId, recipe)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Recipe ingredient not found"));
        applyIngredient(ingredient, request);
        RecipeIngredient saved = recipeIngredientRepository.save(ingredient);
        recalculate(recipe);
        return toIngredientResponse(saved);
    }

    @Transactional
    public void deleteIngredient(UUID recipeId, UUID ingredientId) {
        Recipe recipe = requireOwned(recipeId);
        RecipeIngredient ingredient = recipeIngredientRepository.findByIdAndRecipe(ingredientId, recipe)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Recipe ingredient not found"));
        recipeIngredientRepository.delete(ingredient);
        recalculate(recipe);
    }

    @Transactional
    public RecipeResponse calculateNutrition(UUID recipeId) {
        return toResponse(recalculate(requireOwned(recipeId)));
    }

    public Recipe requireOwned(UUID id) {
        UserAccount user = currentUserService.requireCurrentUser();
        return recipeRepository.findById(id)
                .filter(recipe -> recipe.getUser() != null && recipe.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Recipe not found"));
    }

    public Recipe requireVisible(UUID id) {
        UserAccount user = currentUserService.requireCurrentUser();
        return recipeRepository.findById(id)
                .filter(recipe -> recipe.isPublic()
                        || (recipe.getUser() != null && recipe.getUser().getId().equals(user.getId())))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Recipe not found"));
    }

    private void apply(Recipe recipe, RecipeRequest request) {
        recipe.setTitle(request.title());
        recipe.setDescription(request.description());
        recipe.setInstructions(request.instructions());
        recipe.setPrepTimeMinutes(request.prepTimeMinutes());
        recipe.setCookTimeMinutes(request.cookTimeMinutes());
        recipe.setServings(request.servings());
        recipe.setPublic(request.isPublic());
        recipe.setAiGenerated(request.isAiGenerated());
        recipe.setHighCalorie(request.isHighCalorie());
        recipe.setHighProtein(request.isHighProtein());
        recipe.setLowCarb(request.isLowCarb());
    }

    private void applyIngredient(RecipeIngredient ingredient, RecipeIngredientRequest request) {
        Food food = foodService.requireAccessibleFood(request.foodId());
        ingredient.setFood(food);
        ingredient.setQuantity(request.quantity());
        ingredient.setUnit(request.unit());
        ingredient.setCalories(request.calories() != null ? request.calories() : food.getCaloriesPer100g());
        ingredient.setProteinG(request.proteinG() != null ? request.proteinG() : food.getProteinPer100g());
        ingredient.setCarbsG(request.carbsG() != null ? request.carbsG() : food.getCarbsPer100g());
        ingredient.setFatG(request.fatG() != null ? request.fatG() : food.getFatPer100g());
    }

    private Recipe recalculate(Recipe recipe) {
        List<RecipeIngredient> ingredients = recipeIngredientRepository.findByRecipe(recipe);
        recipe.setTotalCalories(sum(ingredients.stream().map(RecipeIngredient::getCalories).toList()));
        recipe.setTotalProteinG(sum(ingredients.stream().map(RecipeIngredient::getProteinG).toList()));
        recipe.setTotalCarbsG(sum(ingredients.stream().map(RecipeIngredient::getCarbsG).toList()));
        recipe.setTotalFatG(sum(ingredients.stream().map(RecipeIngredient::getFatG).toList()));
        return recipeRepository.save(recipe);
    }

    private RecipeResponse toResponse(Recipe recipe) {
        List<RecipeIngredientResponse> ingredients = recipeIngredientRepository.findByRecipe(recipe)
                .stream().map(this::toIngredientResponse).toList();
        return new RecipeResponse(
                recipe.getId(),
                recipe.getUser() != null ? recipe.getUser().getId() : null,
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.getInstructions(),
                recipe.getPrepTimeMinutes(),
                recipe.getCookTimeMinutes(),
                recipe.getServings(),
                recipe.getTotalCalories(),
                recipe.getTotalProteinG(),
                recipe.getTotalCarbsG(),
                recipe.getTotalFatG(),
                recipe.isPublic(),
                recipe.isAiGenerated(),
                recipe.isHighCalorie(),
                recipe.isHighProtein(),
                recipe.isLowCarb(),
                ingredients
        );
    }

    private RecipeIngredientResponse toIngredientResponse(RecipeIngredient ingredient) {
        return new RecipeIngredientResponse(
                ingredient.getId(),
                ingredient.getFood().getId(),
                ingredient.getFood().getName(),
                ingredient.getQuantity(),
                ingredient.getUnit(),
                ingredient.getCalories(),
                ingredient.getProteinG(),
                ingredient.getCarbsG(),
                ingredient.getFatG()
        );
    }

    private BigDecimal sum(List<BigDecimal> values) {
        return values.stream().filter(java.util.Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
