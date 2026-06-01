package com.demoapi.apicomida.services;

import com.demoapi.apicomida.dtos.FridgeDtos.FridgeItemRequest;
import com.demoapi.apicomida.dtos.FridgeDtos.FridgeItemResponse;
import com.demoapi.apicomida.dtos.FridgeDtos.RecipeMatchResponse;
import com.demoapi.apicomida.exception.ApiException;
import com.demoapi.apicomida.models.Recipe;
import com.demoapi.apicomida.models.RecipeIngredient;
import com.demoapi.apicomida.models.UserAccount;
import com.demoapi.apicomida.models.UserFridgeItem;
import com.demoapi.apicomida.repositories.RecipeIngredientRepository;
import com.demoapi.apicomida.repositories.RecipeRepository;
import com.demoapi.apicomida.repositories.UserFridgeItemRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FridgeService {

    private final UserFridgeItemRepository userFridgeItemRepository;
    private final CurrentUserService currentUserService;
    private final FoodService foodService;
    private final RecipeRepository recipeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;

    public FridgeService(
            UserFridgeItemRepository userFridgeItemRepository,
            CurrentUserService currentUserService,
            FoodService foodService,
            RecipeRepository recipeRepository,
            RecipeIngredientRepository recipeIngredientRepository
    ) {
        this.userFridgeItemRepository = userFridgeItemRepository;
        this.currentUserService = currentUserService;
        this.foodService = foodService;
        this.recipeRepository = recipeRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
    }

    @Transactional
    public FridgeItemResponse create(FridgeItemRequest request) {
        UserFridgeItem item = new UserFridgeItem();
        item.setUser(currentUserService.requireCurrentUser());
        apply(item, request);
        return toResponse(userFridgeItemRepository.save(item));
    }

    public List<FridgeItemResponse> getItems() {
        return userFridgeItemRepository.findByUserOrderByCreatedAtDesc(currentUserService.requireCurrentUser())
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public FridgeItemResponse update(UUID id, FridgeItemRequest request) {
        UserFridgeItem item = requireOwned(id);
        apply(item, request);
        return toResponse(userFridgeItemRepository.save(item));
    }

    @Transactional
    public void delete(UUID id) {
        userFridgeItemRepository.delete(requireOwned(id));
    }

    public List<RecipeMatchResponse> matchRecipes() {
        Set<UUID> availableFoodIds = userFridgeItemRepository.findByUserOrderByCreatedAtDesc(currentUserService.requireCurrentUser())
                .stream().map(item -> item.getFood().getId()).collect(Collectors.toSet());
        List<RecipeMatchResponse> matches = new ArrayList<>();
        for (Recipe recipe : recipeRepository.findByIsPublicTrueOrderByCreatedAtDesc()) {
            List<RecipeIngredient> ingredients = recipeIngredientRepository.findByRecipe(recipe);
            List<String> matched = ingredients.stream()
                    .filter(ingredient -> availableFoodIds.contains(ingredient.getFood().getId()))
                    .map(ingredient -> ingredient.getFood().getName())
                    .toList();
            if (!matched.isEmpty()) {
                matches.add(new RecipeMatchResponse(recipe.getId(), recipe.getTitle(), matched.size(), ingredients.size(), matched));
            }
        }
        return matches;
    }

    private void apply(UserFridgeItem item, FridgeItemRequest request) {
        item.setFood(foodService.requireFood(request.foodId()));
        item.setQuantity(request.quantity());
        item.setUnit(request.unit());
        item.setExpirationDate(request.expirationDate());
    }

    private UserFridgeItem requireOwned(UUID id) {
        UserAccount user = currentUserService.requireCurrentUser();
        return userFridgeItemRepository.findById(id)
                .filter(item -> item.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Fridge item not found"));
    }

    private FridgeItemResponse toResponse(UserFridgeItem item) {
        return new FridgeItemResponse(
                item.getId(),
                item.getFood().getId(),
                item.getFood().getName(),
                item.getQuantity(),
                item.getUnit(),
                item.getExpirationDate()
        );
    }
}
