package com.demoapi.apicomida.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demoapi.apicomida.dtos.MealPlanDtos.MealPlanAnalysisResponse;
import com.demoapi.apicomida.dtos.MealPlanDtos.MealPlanDayRequest;
import com.demoapi.apicomida.dtos.MealPlanDtos.MealPlanDayResponse;
import com.demoapi.apicomida.dtos.MealPlanDtos.MealPlanMealRequest;
import com.demoapi.apicomida.dtos.MealPlanDtos.MealPlanMealResponse;
import com.demoapi.apicomida.dtos.MealPlanDtos.MealPlanRequest;
import com.demoapi.apicomida.dtos.MealPlanDtos.MealPlanResponse;
import com.demoapi.apicomida.exception.ApiException;
import com.demoapi.apicomida.models.MealPlan;
import com.demoapi.apicomida.models.MealPlanDay;
import com.demoapi.apicomida.models.MealPlanMeal;
import com.demoapi.apicomida.models.UserAccount;
import com.demoapi.apicomida.models.UserProfile;
import com.demoapi.apicomida.repositories.MealPlanDayRepository;
import com.demoapi.apicomida.repositories.MealPlanMealRepository;
import com.demoapi.apicomida.repositories.MealPlanRepository;
import com.demoapi.apicomida.repositories.UserProfileRepository;

@Service
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final MealPlanDayRepository mealPlanDayRepository;
    private final MealPlanMealRepository mealPlanMealRepository;
    private final CurrentUserService currentUserService;
    private final RecipeService recipeService;
    private final FoodService foodService;
    private final UserProfileRepository userProfileRepository;

    public MealPlanService(
            MealPlanRepository mealPlanRepository,
            MealPlanDayRepository mealPlanDayRepository,
            MealPlanMealRepository mealPlanMealRepository,
            CurrentUserService currentUserService,
            RecipeService recipeService,
            FoodService foodService,
            UserProfileRepository userProfileRepository) {
        this.mealPlanRepository = mealPlanRepository;
        this.mealPlanDayRepository = mealPlanDayRepository;
        this.mealPlanMealRepository = mealPlanMealRepository;
        this.currentUserService = currentUserService;
        this.recipeService = recipeService;
        this.foodService = foodService;
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public MealPlanResponse create(MealPlanRequest request) {
        MealPlan plan = new MealPlan();
        plan.setUser(currentUserService.requireCurrentUser());
        apply(plan, request);
        return toResponse(mealPlanRepository.save(plan));
    }

    public List<MealPlanResponse> getMine() {
        return mealPlanRepository.findByUserOrderByCreatedAtDesc(currentUserService.requireCurrentUser())
                .stream().map(this::toResponse).toList();
    }

    public MealPlanResponse getById(UUID id) {
        return toResponse(requireOwnedPlan(id));
    }

    @Transactional
    public MealPlanResponse update(UUID id, MealPlanRequest request) {
        MealPlan plan = requireOwnedPlan(id);
        apply(plan, request);
        return toResponse(mealPlanRepository.save(plan));
    }

    @Transactional
    public void delete(UUID id) {
        mealPlanRepository.delete(requireOwnedPlan(id));
    }

    @Transactional
    public MealPlanDayResponse addDay(UUID planId, MealPlanDayRequest request) {
        MealPlanDay day = new MealPlanDay();
        day.setMealPlan(requireOwnedPlan(planId));
        apply(day, request);
        return toDayResponse(mealPlanDayRepository.save(day));
    }

    public List<MealPlanDayResponse> getDays(UUID planId) {
        return mealPlanDayRepository.findByMealPlanOrderByDayNumberAsc(requireOwnedPlan(planId))
                .stream().map(this::toDayResponse).toList();
    }

    @Transactional
    public MealPlanDayResponse updateDay(UUID dayId, MealPlanDayRequest request) {
        MealPlanDay day = requireOwnedDay(dayId);
        apply(day, request);
        return toDayResponse(mealPlanDayRepository.save(day));
    }

    @Transactional
    public void deleteDay(UUID dayId) {
        mealPlanDayRepository.delete(requireOwnedDay(dayId));
    }

    @Transactional
    public MealPlanMealResponse addMeal(UUID dayId, MealPlanMealRequest request) {
        MealPlanMeal meal = new MealPlanMeal();
        meal.setMealPlanDay(requireOwnedDay(dayId));
        apply(meal, request);
        return toMealResponse(mealPlanMealRepository.save(meal));
    }

    @Transactional
    public MealPlanMealResponse updateMeal(UUID mealId, MealPlanMealRequest request) {
        MealPlanMeal meal = requireOwnedMeal(mealId);
        apply(meal, request);
        return toMealResponse(mealPlanMealRepository.save(meal));
    }

    @Transactional
    public void deleteMeal(UUID mealId) {
        mealPlanMealRepository.delete(requireOwnedMeal(mealId));
    }

    @Transactional
    public MealPlanAnalysisResponse analyze(UUID planId) {
        MealPlan plan = requireOwnedPlan(planId);
        List<MealPlanDay> days = mealPlanDayRepository.findByMealPlanOrderByDayNumberAsc(plan);
        BigDecimal totalCalories = BigDecimal.ZERO;
        BigDecimal totalProtein = BigDecimal.ZERO;
        for (MealPlanDay day : days) {
            totalCalories = totalCalories
                    .add(day.getTotalCalories() == null ? BigDecimal.ZERO : day.getTotalCalories());
            totalProtein = totalProtein.add(day.getTotalProteinG() == null ? BigDecimal.ZERO : day.getTotalProteinG());
        }
        BigDecimal averageCalories = days.isEmpty() ? BigDecimal.ZERO
                : totalCalories.divide(BigDecimal.valueOf(days.size()), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal averageProtein = days.isEmpty() ? BigDecimal.ZERO
                : totalProtein.divide(BigDecimal.valueOf(days.size()), 2, java.math.RoundingMode.HALF_UP);

        UserProfile profile = userProfileRepository.findByUser(plan.getUser()).orElse(null);
        BigDecimal targetCalories = profile != null && profile.getDailyCalorieGoal() != null
                ? profile.getDailyCalorieGoal()
                : plan.getDailyCalorieTarget();
        BigDecimal targetProtein = profile != null ? profile.getDailyProteinGoal() : null;

        boolean excess = targetCalories != null
                && averageCalories.compareTo(targetCalories.add(BigDecimal.valueOf(150))) > 0;
        boolean lowCalories = targetCalories != null
                && averageCalories.compareTo(targetCalories.subtract(BigDecimal.valueOf(150))) < 0;
        boolean lowProtein = targetProtein != null && averageProtein.compareTo(targetProtein) < 0;
        plan.setHasExcessCalories(excess);
        plan.setHasLowCalories(lowCalories);
        plan.setHasLowProtein(lowProtein);
        mealPlanRepository.save(plan);

        String profileGoal = profile != null ? profile.getGoal() : null;
        boolean matchesGoal = !(excess && "lose_weight".equalsIgnoreCase(profileGoal));
        return new MealPlanAnalysisResponse(excess, lowCalories, lowProtein, matchesGoal, averageCalories,
                averageProtein);
    }

    private void apply(MealPlan plan, MealPlanRequest request) {
        plan.setTitle(request.title());
        plan.setDescription(request.description());
        plan.setStartDate(request.startDate());
        plan.setEndDate(request.endDate());
        plan.setDailyCalorieTarget(request.dailyCalorieTarget());
        plan.setAiGenerated(request.isAiGenerated());
    }

    private void apply(MealPlanDay day, MealPlanDayRequest request) {
        day.setDayNumber(request.dayNumber());
        day.setDayDate(request.dayDate());
        day.setTotalCalories(request.totalCalories());
        day.setTotalProteinG(request.totalProteinG());
        day.setTotalCarbsG(request.totalCarbsG());
        day.setTotalFatG(request.totalFatG());
    }

    private void apply(MealPlanMeal meal, MealPlanMealRequest request) {
        meal.setMealType(request.mealType());
        meal.setQuantity(request.quantity());
        meal.setNotes(request.notes());
        meal.setCalories(request.calories());
        meal.setProteinG(request.proteinG());
        meal.setCarbsG(request.carbsG());
        meal.setFatG(request.fatG());
        meal.setRecipe(request.recipeId() != null ? recipeService.requireVisible(request.recipeId()) : null);
        meal.setFood(request.foodId() != null ? foodService.requireFood(request.foodId()) : null);
    }

    private MealPlan requireOwnedPlan(UUID id) {
        UserAccount user = currentUserService.requireCurrentUser();
        return mealPlanRepository.findById(id)
                .filter(plan -> plan.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Meal plan not found"));
    }

    private MealPlanDay requireOwnedDay(UUID id) {
        UserAccount user = currentUserService.requireCurrentUser();
        return mealPlanDayRepository.findById(id)
                .filter(day -> day.getMealPlan().getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Meal plan day not found"));
    }

    private MealPlanMeal requireOwnedMeal(UUID id) {
        UserAccount user = currentUserService.requireCurrentUser();
        return mealPlanMealRepository.findById(id)
                .filter(meal -> meal.getMealPlanDay().getMealPlan().getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Meal plan meal not found"));
    }

    private MealPlanResponse toResponse(MealPlan plan) {
        return new MealPlanResponse(
                plan.getId(),
                plan.getTitle(),
                plan.getDescription(),
                plan.getStartDate(),
                plan.getEndDate(),
                plan.getDailyCalorieTarget(),
                plan.isAiGenerated(),
                plan.isHasExcessCalories(),
                plan.isHasLowProtein(),
                plan.isHasLowCalories());
    }

    private MealPlanDayResponse toDayResponse(MealPlanDay day) {
        return new MealPlanDayResponse(
                day.getId(),
                day.getDayNumber(),
                day.getDayDate(),
                day.getTotalCalories(),
                day.getTotalProteinG(),
                day.getTotalCarbsG(),
                day.getTotalFatG(),
                mealPlanMealRepository.findByMealPlanDay(day).stream().map(this::toMealResponse).toList());
    }

    private MealPlanMealResponse toMealResponse(MealPlanMeal meal) {
        return new MealPlanMealResponse(
                meal.getId(),
                meal.getMealType(),
                meal.getRecipe() != null ? meal.getRecipe().getId() : null,
                meal.getFood() != null ? meal.getFood().getId() : null,
                meal.getQuantity(),
                meal.getNotes(),
                meal.getCalories(),
                meal.getProteinG(),
                meal.getCarbsG(),
                meal.getFatG());
    }
}
