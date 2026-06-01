package com.demoapi.apicomida.services;

import com.demoapi.apicomida.dtos.UserDtos.ProfileResponse;
import com.demoapi.apicomida.dtos.UserDtos.UpdateProfileRequest;
import com.demoapi.apicomida.dtos.UserDtos.UserResponse;
import com.demoapi.apicomida.dtos.UserDtos.UserStatsResponse;
import com.demoapi.apicomida.models.UserAccount;
import com.demoapi.apicomida.models.UserProfile;
import com.demoapi.apicomida.repositories.DailyNoteRepository;
import com.demoapi.apicomida.repositories.MealPlanRepository;
import com.demoapi.apicomida.repositories.RecipeRepository;
import com.demoapi.apicomida.repositories.UserFoodLogRepository;
import com.demoapi.apicomida.repositories.UserProfileRepository;
import com.demoapi.apicomida.repositories.WorkoutLogRepository;
import com.demoapi.apicomida.repositories.WorkoutPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserProfileRepository userProfileRepository;
    private final CurrentUserService currentUserService;
    private final UserFoodLogRepository userFoodLogRepository;
    private final RecipeRepository recipeRepository;
    private final MealPlanRepository mealPlanRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final DailyNoteRepository dailyNoteRepository;

    public UserService(
            UserProfileRepository userProfileRepository,
            CurrentUserService currentUserService,
            UserFoodLogRepository userFoodLogRepository,
            RecipeRepository recipeRepository,
            MealPlanRepository mealPlanRepository,
            WorkoutPlanRepository workoutPlanRepository,
            WorkoutLogRepository workoutLogRepository,
            DailyNoteRepository dailyNoteRepository
    ) {
        this.userProfileRepository = userProfileRepository;
        this.currentUserService = currentUserService;
        this.userFoodLogRepository = userFoodLogRepository;
        this.recipeRepository = recipeRepository;
        this.mealPlanRepository = mealPlanRepository;
        this.workoutPlanRepository = workoutPlanRepository;
        this.workoutLogRepository = workoutLogRepository;
        this.dailyNoteRepository = dailyNoteRepository;
    }

    public UserResponse getMe() {
        return toUserResponse(currentUserService.requireCurrentUser());
    }

    @Transactional
    public UserResponse updateMe(UpdateProfileRequest request) {
        UserAccount user = currentUserService.requireCurrentUser();
        UserProfile profile = userProfileRepository.findByUser(user).orElseGet(() -> {
            UserProfile created = new UserProfile();
            created.setUser(user);
            return created;
        });

        if (request.firstName() != null) profile.setFirstName(request.firstName());
        if (request.lastName() != null) profile.setLastName(request.lastName());
        if (request.age() != null) profile.setAge(request.age());
        if (request.gender() != null) profile.setGender(request.gender());
        if (request.weightKg() != null) profile.setWeightKg(request.weightKg());
        if (request.heightCm() != null) profile.setHeightCm(request.heightCm());
        if (request.activityLevel() != null) profile.setActivityLevel(request.activityLevel());
        if (request.goal() != null) profile.setGoal(request.goal());
        if (request.targetWeightKg() != null) profile.setTargetWeightKg(request.targetWeightKg());
        if (request.dailyCalorieGoal() != null) profile.setDailyCalorieGoal(request.dailyCalorieGoal());
        if (request.dailyProteinGoal() != null) profile.setDailyProteinGoal(request.dailyProteinGoal());
        if (request.dailyCarbsGoal() != null) profile.setDailyCarbsGoal(request.dailyCarbsGoal());
        if (request.dailyFatGoal() != null) profile.setDailyFatGoal(request.dailyFatGoal());

        return toUserResponse(userProfileRepository.save(profile).getUser());
    }

    @Transactional
    public void deleteMe() {
        UserAccount user = currentUserService.requireCurrentUser();
        user.setActive(false);
    }

    public UserStatsResponse getStats() {
        UserAccount user = currentUserService.requireCurrentUser();
        return new UserStatsResponse(
                userFoodLogRepository.findByUserAndLogDateBetweenOrderByLogDateAscCreatedAtAsc(
                        user,
                        java.time.LocalDate.of(2000, 1, 1),
                        java.time.LocalDate.of(2999, 1, 1)
                ).size(),
                recipeRepository.findByUserOrderByCreatedAtDesc(user).size(),
                mealPlanRepository.findByUserOrderByCreatedAtDesc(user).size(),
                workoutPlanRepository.findByUserOrderByCreatedAtDesc(user).size(),
                workoutLogRepository.findByUserAndLogDateBetweenOrderByLogDateAscCreatedAtAsc(
                        user,
                        java.time.LocalDate.of(2000, 1, 1),
                        java.time.LocalDate.of(2999, 1, 1)
                ).size(),
                dailyNoteRepository.findByUserAndNoteDateBetweenOrderByNoteDateAscCreatedAtAsc(
                        user,
                        java.time.LocalDate.of(2000, 1, 1),
                        java.time.LocalDate.of(2999, 1, 1)
                ).size()
        );
    }

    public UserResponse toUserResponse(UserAccount user) {
        UserProfile profile = userProfileRepository.findByUser(user).orElse(null);
        return new UserResponse(user.getId(), user.getEmail(), user.isActive(), toProfileResponse(profile));
    }

    public ProfileResponse toProfileResponse(UserProfile profile) {
        if (profile == null) {
            return null;
        }
        return new ProfileResponse(
                profile.getFirstName(),
                profile.getLastName(),
                profile.getAge(),
                profile.getGender(),
                profile.getWeightKg(),
                profile.getHeightCm(),
                profile.getActivityLevel(),
                profile.getGoal(),
                profile.getImc(),
                profile.getTargetWeightKg(),
                profile.getDailyCalorieGoal(),
                profile.getDailyProteinGoal(),
                profile.getDailyCarbsGoal(),
                profile.getDailyFatGoal()
        );
    }
}
