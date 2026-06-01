package com.demoapi.apicomida.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demoapi.apicomida.dtos.OnboardingDtos.GoalCalculationRequest;
import com.demoapi.apicomida.dtos.OnboardingDtos.GoalCalculationResponse;
import com.demoapi.apicomida.dtos.OnboardingDtos.QuestionnaireRequest;
import com.demoapi.apicomida.dtos.OnboardingDtos.QuestionnaireResponse;
import com.demoapi.apicomida.models.OnboardingQuestionnaire;
import com.demoapi.apicomida.models.UserAccount;
import com.demoapi.apicomida.models.UserProfile;
import com.demoapi.apicomida.repositories.OnboardingQuestionnaireRepository;
import com.demoapi.apicomida.repositories.UserProfileRepository;

@Service
public class OnboardingService {

    private final CurrentUserService currentUserService;
    private final OnboardingQuestionnaireRepository questionnaireRepository;
    private final UserProfileRepository userProfileRepository;

    public OnboardingService(
            CurrentUserService currentUserService,
            OnboardingQuestionnaireRepository questionnaireRepository,
            UserProfileRepository userProfileRepository
    ) {
        this.currentUserService = currentUserService;
        this.questionnaireRepository = questionnaireRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public QuestionnaireResponse create(QuestionnaireRequest request) {
        UserAccount user = currentUserService.requireCurrentUser();
        OnboardingQuestionnaire questionnaire = new OnboardingQuestionnaire();
        apply(questionnaire, request);
        questionnaire.setUser(user);
        questionnaire.setCompletedAt(LocalDateTime.now());
        questionnaire = questionnaireRepository.save(questionnaire);
        syncProfile(user, request);
        return toResponse(questionnaire);
    }

    public QuestionnaireResponse getMe() {
        return questionnaireRepository.findFirstByUserOrderByCreatedAtDesc(currentUserService.requireCurrentUser())
                .map(this::toResponse)
                .orElse(null);
    }

    @Transactional
    public QuestionnaireResponse updateMe(QuestionnaireRequest request) {
        UserAccount user = currentUserService.requireCurrentUser();
        OnboardingQuestionnaire questionnaire = questionnaireRepository.findFirstByUserOrderByCreatedAtDesc(user)
                .orElseGet(OnboardingQuestionnaire::new);
        questionnaire.setUser(user);
        apply(questionnaire, request);
        questionnaire.setCompletedAt(LocalDateTime.now());
        questionnaire = questionnaireRepository.save(questionnaire);
        syncProfile(user, request);
        return toResponse(questionnaire);
    }

    public GoalCalculationResponse calculateGoals(GoalCalculationRequest request) {
        BigDecimal heightMeters = request.heightCm().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal imc = request.weightKg().divide(heightMeters.multiply(heightMeters), 2, RoundingMode.HALF_UP);
        double bmr = ("male".equalsIgnoreCase(request.gender()) ? 10 : 10) * request.weightKg().doubleValue()
                + 6.25 * request.heightCm().doubleValue()
                - 5 * request.age()
                + ("male".equalsIgnoreCase(request.gender()) ? 5 : -161);
        double activityFactor = switch (request.activityLevel() == null ? "" : request.activityLevel()) {
            case "light" -> 1.375;
            case "moderate" -> 1.55;
            case "active" -> 1.725;
            case "very_active" -> 1.9;
            default -> 1.2;
        };
        double calories = bmr * activityFactor;
        String goal = request.mainGoal() == null ? "maintain_weight" : request.mainGoal();
        if ("lose_weight".equals(goal)) calories -= 400;
        if ("gain_muscle".equals(goal)) calories += 250;
        BigDecimal caloriesValue = BigDecimal.valueOf(calories).setScale(0, RoundingMode.HALF_UP);
       BigDecimal protein = request.weightKg()
            .multiply(new BigDecimal("2.0"))
            .setScale(2, RoundingMode.HALF_UP);

        BigDecimal fat = request.weightKg()
            .multiply(new BigDecimal("0.8"))
            .setScale(2, RoundingMode.HALF_UP);BigDecimal carbs = caloriesValue
                .subtract(protein.multiply(BigDecimal.valueOf(4)))
                .subtract(fat.multiply(BigDecimal.valueOf(9)))
                .divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP);
        return new GoalCalculationResponse(imc, caloriesValue, protein, carbs, fat, goal);
    }

    private void syncProfile(UserAccount user, QuestionnaireRequest request) {
        UserProfile profile = userProfileRepository.findByUser(user).orElseGet(() -> {
            UserProfile created = new UserProfile();
            created.setUser(user);
            return created;
        });
        profile.setAge(request.age());
        profile.setGender(request.gender());
        profile.setWeightKg(request.currentWeightKg());
        profile.setHeightCm(request.heightCm());
        profile.setActivityLevel(request.activityLevel());
        profile.setGoal(request.mainGoal());
        profile.setTargetWeightKg(request.targetWeightKg());
        if (request.currentWeightKg() != null && request.heightCm() != null && request.heightCm().signum() > 0) {
            profile.setImc(calculateGoals(new GoalCalculationRequest(
                    request.currentWeightKg(),
                    request.targetWeightKg(),
                    request.heightCm(),
                    request.age(),
                    request.gender(),
                    request.activityLevel(),
                    request.mainGoal()
            )).imc());
        }
        userProfileRepository.save(profile);
    }

    private void apply(OnboardingQuestionnaire questionnaire, QuestionnaireRequest request) {
        questionnaire.setMainGoal(request.mainGoal());
        questionnaire.setCurrentWeightKg(request.currentWeightKg());
        questionnaire.setTargetWeightKg(request.targetWeightKg());
        questionnaire.setHeightCm(request.heightCm());
        questionnaire.setAge(request.age());
        questionnaire.setGender(request.gender());
        questionnaire.setActivityLevel(request.activityLevel());
        questionnaire.setTrainingDaysPerWeek(request.trainingDaysPerWeek());
        questionnaire.setDietType(request.dietType());
        questionnaire.setFoodRestrictions(request.foodRestrictions());
        questionnaire.setAllergies(request.allergies());
        questionnaire.setPreferredFoods(request.preferredFoods());
        questionnaire.setDislikedFoods(request.dislikedFoods());
        questionnaire.setHealthNotes(request.healthNotes());
    }

    private QuestionnaireResponse toResponse(OnboardingQuestionnaire questionnaire) {
        return new QuestionnaireResponse(
                questionnaire.getId(),
                questionnaire.getMainGoal(),
                questionnaire.getCurrentWeightKg(),
                questionnaire.getTargetWeightKg(),
                questionnaire.getHeightCm(),
                questionnaire.getAge(),
                questionnaire.getGender(),
                questionnaire.getActivityLevel(),
                questionnaire.getTrainingDaysPerWeek(),
                questionnaire.getDietType(),
                questionnaire.getFoodRestrictions(),
                questionnaire.getAllergies(),
                questionnaire.getPreferredFoods(),
                questionnaire.getDislikedFoods(),
                questionnaire.getHealthNotes(),
                questionnaire.getCompletedAt()
        );
    }
}
