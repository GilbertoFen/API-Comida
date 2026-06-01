package com.demoapi.apicomida.services;

import com.demoapi.apicomida.dtos.FoodLogDtos.FoodLogRequest;
import com.demoapi.apicomida.dtos.FoodLogDtos.FoodLogResponse;
import com.demoapi.apicomida.dtos.FoodLogDtos.FoodLogSummaryResponse;
import com.demoapi.apicomida.exception.ApiException;
import com.demoapi.apicomida.models.Food;
import com.demoapi.apicomida.models.UserAccount;
import com.demoapi.apicomida.models.UserFoodLog;
import com.demoapi.apicomida.repositories.UserFoodLogRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FoodLogService {

    private final UserFoodLogRepository userFoodLogRepository;
    private final CurrentUserService currentUserService;
    private final FoodService foodService;

    public FoodLogService(
            UserFoodLogRepository userFoodLogRepository,
            CurrentUserService currentUserService,
            FoodService foodService
    ) {
        this.userFoodLogRepository = userFoodLogRepository;
        this.currentUserService = currentUserService;
        this.foodService = foodService;
    }

    @Transactional
    public FoodLogResponse create(FoodLogRequest request) {
        UserFoodLog log = new UserFoodLog();
        log.setUser(currentUserService.requireCurrentUser());
        apply(log, request);
        return toResponse(userFoodLogRepository.save(log));
    }

    public List<FoodLogResponse> getByDate(LocalDate date) {
        return userFoodLogRepository.findByUserAndLogDateOrderByCreatedAtDesc(currentUserService.requireCurrentUser(), date)
                .stream().map(this::toResponse).toList();
    }

    public List<FoodLogResponse> getByRange(LocalDate startDate, LocalDate endDate) {
        return userFoodLogRepository.findByUserAndLogDateBetweenOrderByLogDateAscCreatedAtAsc(
                currentUserService.requireCurrentUser(), startDate, endDate
        ).stream().map(this::toResponse).toList();
    }

    public FoodLogSummaryResponse getSummary(LocalDate date) {
        List<FoodLogResponse> items = getByDate(date);
        return new FoodLogSummaryResponse(
                date,
                sum(items.stream().map(FoodLogResponse::calories).toList()),
                sum(items.stream().map(FoodLogResponse::proteinG).toList()),
                sum(items.stream().map(FoodLogResponse::carbsG).toList()),
                sum(items.stream().map(FoodLogResponse::fatG).toList()),
                items
        );
    }

    @Transactional
    public FoodLogResponse update(UUID id, FoodLogRequest request) {
        UserFoodLog log = requireOwned(id);
        apply(log, request);
        return toResponse(userFoodLogRepository.save(log));
    }

    @Transactional
    public void delete(UUID id) {
        userFoodLogRepository.delete(requireOwned(id));
    }

    private void apply(UserFoodLog log, FoodLogRequest request) {
        Food food = foodService.requireAccessibleFood(request.foodId());
        log.setFood(food);
        log.setLogDate(request.logDate());
        log.setMealType(request.mealType());
        log.setQuantity(request.quantity());
        log.setUnit(request.unit());
        log.setCalories(request.calories() != null ? request.calories() : food.getCaloriesPer100g());
        log.setProteinG(request.proteinG() != null ? request.proteinG() : food.getProteinPer100g());
        log.setCarbsG(request.carbsG() != null ? request.carbsG() : food.getCarbsPer100g());
        log.setFatG(request.fatG() != null ? request.fatG() : food.getFatPer100g());
    }

    private UserFoodLog requireOwned(UUID id) {
        UserAccount user = currentUserService.requireCurrentUser();
        return userFoodLogRepository.findById(id)
                .filter(log -> log.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Food log not found"));
    }

    private FoodLogResponse toResponse(UserFoodLog log) {
        return new FoodLogResponse(
                log.getId(),
                log.getFood().getId(),
                log.getFood().getName(),
                log.getLogDate(),
                log.getMealType(),
                log.getQuantity(),
                log.getUnit(),
                log.getCalories(),
                log.getProteinG(),
                log.getCarbsG(),
                log.getFatG()
        );
    }

    private BigDecimal sum(List<BigDecimal> values) {
        return values.stream().filter(java.util.Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
