package com.demoapi.apicomida.services;

import com.demoapi.apicomida.dtos.FoodDtos.CustomFoodRequest;
import com.demoapi.apicomida.dtos.FoodDtos.FoodRequest;
import com.demoapi.apicomida.dtos.FoodDtos.FoodResponse;
import com.demoapi.apicomida.dtos.OpenFoodFactsDtos.OpenFoodFactsNutrimentsResponse;
import com.demoapi.apicomida.dtos.OpenFoodFactsDtos.OpenFoodFactsProductResponse;
import com.demoapi.apicomida.exception.ApiException;
import com.demoapi.apicomida.models.Food;
import com.demoapi.apicomida.models.UserAccount;
import com.demoapi.apicomida.repositories.FoodRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FoodService {

    private static final String SOURCE_MANUAL = "MANUAL";
    private static final String SOURCE_OPEN_FOOD_FACTS = "OPEN_FOOD_FACTS";
    private static final Logger log = LoggerFactory.getLogger(FoodService.class);

    private final FoodRepository foodRepository;
    private final CurrentUserService currentUserService;
    private final OpenFoodFactsClient openFoodFactsClient;
    private final ObjectMapper objectMapper;

    public FoodService(
            FoodRepository foodRepository,
            CurrentUserService currentUserService,
            OpenFoodFactsClient openFoodFactsClient,
            ObjectMapper objectMapper
    ) {
        this.foodRepository = foodRepository;
        this.currentUserService = currentUserService;
        this.openFoodFactsClient = openFoodFactsClient;
        this.objectMapper = objectMapper;
    }

    public List<FoodResponse> getFoods() {
        return foodRepository.findAccessibleFoods(currentUser()).stream().map(this::toResponse).toList();
    }

    public FoodResponse getFood(UUID id) {
        return toResponse(requireAccessibleFood(id));
    }

    @Transactional
    public FoodResponse create(FoodRequest request) {
        Food food = new Food();
        apply(food, request, currentUser(), request.externalSource() == null ? SOURCE_MANUAL : request.externalSource());
        return toResponse(foodRepository.save(food));
    }

    @Transactional
    public FoodResponse createCustomFood(CustomFoodRequest request) {
        Food food = new Food();
        food.setUser(currentUser());
        food.setName(request.name().trim());
        food.setBrand(normalizeNullable(request.brand()));
        food.setCategory(defaultCategory(request.category()));
        food.setServingSize(request.servingSize());
        food.setServingUnit(normalizeNullable(request.servingUnit()));
        food.setCaloriesPer100g(request.caloriesPer100g());
        food.setProteinPer100g(request.proteinPer100g());
        food.setCarbsPer100g(request.carbsPer100g());
        food.setFatPer100g(request.fatPer100g());
        food.setFiberPer100g(request.fiberPer100g());
        food.setSugarPer100g(request.sugarPer100g());
        food.setSodiumPer100g(request.sodiumPer100g());
        food.setExternalSource(SOURCE_MANUAL);
        food.setVerified(false);
        return toResponse(foodRepository.save(food));
    }

    @Transactional
    public FoodResponse update(UUID id, FoodRequest request) {
        Food food = requireAccessibleFood(id);
        apply(food, request, food.getUser(), request.externalSource() == null ? food.getExternalSource() : request.externalSource());
        return toResponse(foodRepository.save(food));
    }

    @Transactional
    public void delete(UUID id) {
        foodRepository.delete(requireAccessibleFood(id));
    }

    @Transactional
    public List<FoodResponse> searchFoods(String query) {
        String normalizedQuery = requireQuery(query);
        UserAccount user = currentUser();

        List<Food> localResults = foodRepository.searchLocal(user, normalizedQuery);
        if (!localResults.isEmpty()) {
            return localResults.stream().map(this::toResponse).toList();
        }

        try {
            List<Food> savedFoods = openFoodFactsClient.search(normalizedQuery).stream()
                    .filter(this::hasMinimumNutrition)
                    .map(this::normalizeOpenFoodFactsProduct)
                    .map(product -> saveExternalSafely(product, normalizedQuery))
                    .filter(Objects::nonNull)
                    .toList();
            return savedFoods.stream().map(this::toResponse).toList();
        } catch (ExternalServiceException exception) {
            log.warn("Open Food Facts search fallback activated for query '{}'", normalizedQuery, exception);
            return List.of();
        } catch (RuntimeException exception) {
            log.warn("Open Food Facts search processing fallback activated for query '{}'", normalizedQuery, exception);
            return List.of();
        }
    }

    @Transactional
    public FoodResponse findByBarcode(String barcode) {
        String normalizedBarcode = requireBarcode(barcode);
        UserAccount user = currentUser();

        List<Food> localFoods = foodRepository.findAccessibleByBarcode(user, normalizedBarcode);
        if (!localFoods.isEmpty()) {
            return toResponse(localFoods.get(0));
        }

        OpenFoodFactsProductResponse external;
        try {
            external = openFoodFactsClient.findByBarcode(normalizedBarcode)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Food not found by barcode"));
        } catch (ExternalServiceException exception) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "Open Food Facts is unavailable");
        }

        if (!hasMinimumNutrition(external)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Food found but without enough nutrition data");
        }

        return toResponse(saveIfNotExists(normalizeOpenFoodFactsProduct(external)));
    }

    public List<FoodResponse> getByCategory(String category) {
        return foodRepository.findByCategoryIgnoreCaseAndAccessible(currentUser(), category)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Food requireAccessibleFood(UUID id) {
        UserAccount user = currentUser();
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Food not found"));
        if (food.getUser() != null && !food.getUser().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Food not found");
        }
        return food;
    }

    private void apply(Food food, FoodRequest request, UserAccount user, String externalSource) {
        food.setUser(user);
        food.setName(request.name().trim());
        food.setBrand(normalizeNullable(request.brand()));
        food.setCategory(defaultCategory(request.category()));
        food.setServingSize(request.servingSize());
        food.setServingUnit(normalizeNullable(request.servingUnit()));
        food.setCaloriesPer100g(request.caloriesPer100g());
        food.setProteinPer100g(request.proteinPer100g());
        food.setCarbsPer100g(request.carbsPer100g());
        food.setFatPer100g(request.fatPer100g());
        food.setFiberPer100g(request.fiberPer100g());
        food.setSugarPer100g(request.sugarPer100g());
        food.setSodiumPer100g(request.sodiumPer100g());
        food.setBarcode(normalizeNullable(request.barcode()));
        food.setExternalSource(externalSource);
        food.setVerified(false);
    }

    private FoodResponse toResponse(Food food) {
        return new FoodResponse(
                food.getId(),
                food.getName(),
                food.getBrand(),
                food.getBarcode(),
                food.getExternalSource(),
                food.getExternalId(),
                food.getCategory(),
                food.getServingSize(),
                food.getServingUnit(),
                food.getCaloriesPer100g(),
                food.getProteinPer100g(),
                food.getCarbsPer100g(),
                food.getFatPer100g(),
                food.getFiberPer100g(),
                food.getSugarPer100g(),
                food.getSodiumPer100g(),
                food.isVerified()
        );
    }

    private String requireQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Query must not be empty");
        }
        return query.trim();
    }

    private String requireBarcode(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Barcode must not be empty");
        }
        return barcode.trim();
    }

    private UserAccount currentUser() {
        return currentUserService.requireCurrentUser();
    }

    private boolean hasMinimumNutrition(OpenFoodFactsProductResponse product) {
        if (product == null || normalizeNullable(product.code()) == null || normalizeNullable(product.productName()) == null) {
            return false;
        }
        OpenFoodFactsNutrimentsResponse nutriments = product.nutriments();
        if (nutriments == null) {
            return false;
        }
        return nutriments.energyKcal100g() != null
                || nutriments.proteins100g() != null
                || nutriments.carbohydrates100g() != null
                || nutriments.fat100g() != null
                || nutriments.fiber100g() != null
                || nutriments.sugars100g() != null
                || nutriments.sodium100g() != null;
    }

    private Food normalizeOpenFoodFactsProduct(OpenFoodFactsProductResponse product) {
        Food food = new Food();
        food.setName(product.productName().trim());
        food.setBrand(normalizeNullable(product.brands()));
        food.setCategory(extractCategory(product.categories()));
        food.setBarcode(normalizeNullable(product.code()));
        food.setExternalSource(SOURCE_OPEN_FOOD_FACTS);
        food.setExternalId(normalizeNullable(product.code()));
        food.setVerified(false);
        food.setServingSize(BigDecimal.valueOf(100));
        food.setServingUnit("g");

        OpenFoodFactsNutrimentsResponse nutriments = product.nutriments();
        if (nutriments != null) {
            food.setCaloriesPer100g(nutriments.energyKcal100g());
            food.setProteinPer100g(nutriments.proteins100g());
            food.setCarbsPer100g(nutriments.carbohydrates100g());
            food.setFatPer100g(nutriments.fat100g());
            food.setFiberPer100g(nutriments.fiber100g());
            food.setSugarPer100g(nutriments.sugars100g());
            food.setSodiumPer100g(nutriments.sodium100g());
        }

        food.setRawData(serializeRawData(product.rawData()));
        return food;
    }

    private Food saveIfNotExists(Food normalizedFood) {
        if (normalizedFood == null) {
            return null;
        }

        if (normalizedFood.getExternalId() != null) {
            Food existingByExternal = foodRepository.findByExternalSourceAndExternalId(
                    normalizedFood.getExternalSource(),
                    normalizedFood.getExternalId()
            ).orElse(null);
            if (existingByExternal != null) {
                return existingByExternal;
            }
        }

        if (normalizedFood.getBarcode() != null) {
            Food existingByBarcode = foodRepository.findByBarcode(normalizedFood.getBarcode()).orElse(null);
            if (existingByBarcode != null) {
                return existingByBarcode;
            }
        }

        normalizedFood.setUser(null);
        return foodRepository.save(normalizedFood);
    }

    private Food saveExternalSafely(Food normalizedFood, String query) {
        try {
            return saveIfNotExists(normalizedFood);
        } catch (RuntimeException exception) {
            String externalId = normalizedFood != null ? normalizedFood.getExternalId() : null;
            log.warn("Skipping Open Food Facts product during search '{}' due to processing error. externalId={}",
                    query,
                    externalId,
                    exception);
            return null;
        }
    }

    private String serializeRawData(Object rawData) {
        if (rawData == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(rawData);
        } catch (JsonProcessingException exception) {
            return null;
        }
    }

    private String normalizeNullable(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private String defaultCategory(String category) {
        String normalized = normalizeNullable(category);
        return normalized == null ? "Uncategorized" : normalized;
    }

    private String extractCategory(String categories) {
        String normalized = normalizeNullable(categories);
        if (normalized == null) {
            return "Uncategorized";
        }
        String[] parts = normalized.split(",");
        return parts.length == 0 ? normalized : parts[0].trim();
    }
}
