package com.demoapi.apicomida.services;

import com.demoapi.apicomida.dtos.AiDtos.AiResponse;
import com.demoapi.apicomida.dtos.AiDtos.EstimateCaloriesRequest;
import com.demoapi.apicomida.dtos.AiDtos.GenericAiRequest;
import com.demoapi.apicomida.models.AiRequest;
import com.demoapi.apicomida.models.UserAccount;
import com.demoapi.apicomida.repositories.AiRequestRepository;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AiService {

    private final AiRequestRepository aiRequestRepository;
    private final CurrentUserService currentUserService;
    private final boolean aiEnabled;

    public AiService(
            AiRequestRepository aiRequestRepository,
            CurrentUserService currentUserService,
            @Value("${app.ai.enabled:true}") boolean aiEnabled
    ) {
        this.aiRequestRepository = aiRequestRepository;
        this.currentUserService = currentUserService;
        this.aiEnabled = aiEnabled;
    }

    @Transactional
    public AiResponse estimateCalories(EstimateCaloriesRequest request) {
        Map<String, Object> output = new LinkedHashMap<>();
        output.put("estimatedCalories", request.prompt().split(" ").length * 35);
        output.put("estimatedProteinG", BigDecimal.valueOf(request.prompt().split(" ").length * 2L));
        output.put("mode", aiEnabled ? "heuristic-fallback" : "disabled-fallback");
        return persist("estimate_calories", Map.of("prompt", request.prompt()), output);
    }

    @Transactional
    public AiResponse generateMealPlan(GenericAiRequest request) {
        return persist("generate_meal_plan", request.input(), Map.of("message", "Meal plan generation scaffold ready", "aiEnabled", aiEnabled));
    }

    @Transactional
    public AiResponse generateWorkoutPlan(GenericAiRequest request) {
        return persist("generate_workout_plan", request.input(), Map.of("message", "Workout plan generation scaffold ready", "aiEnabled", aiEnabled));
    }

    @Transactional
    public AiResponse recommendRecipes(GenericAiRequest request) {
        return persist("recommend_recipe", request.input(), Map.of("message", "Recipe recommendation scaffold ready", "aiEnabled", aiEnabled));
    }

    @Transactional
    public AiResponse analyzeProgress(GenericAiRequest request) {
        return persist("analyze_progress", request.input(), Map.of("message", "Progress analysis scaffold ready", "aiEnabled", aiEnabled));
    }

    public List<AiResponse> getHistory() {
        return aiRequestRepository.findByUserOrderByCreatedAtDesc(currentUserService.requireCurrentUser())
                .stream().map(this::toResponse).toList();
    }

    public AiResponse getById(UUID id) {
        UserAccount user = currentUserService.requireCurrentUser();
        return aiRequestRepository.findById(id)
                .filter(request -> request.getUser().getId().equals(user.getId()))
                .map(this::toResponse)
                .orElseThrow(() -> new com.demoapi.apicomida.exception.ApiException(org.springframework.http.HttpStatus.NOT_FOUND, "AI request not found"));
    }

    private AiResponse persist(String requestType, Map<String, Object> input, Map<String, Object> output) {
        AiRequest aiRequest = new AiRequest();
        aiRequest.setUser(currentUserService.requireCurrentUser());
        aiRequest.setRequestType(requestType);
        aiRequest.setInputData(input);
        aiRequest.setOutputData(output);
        aiRequest.setStatus(aiEnabled ? "processed" : "fallback");
        return toResponse(aiRequestRepository.save(aiRequest));
    }

    private AiResponse toResponse(AiRequest request) {
        return new AiResponse(request.getId(), request.getRequestType(), request.getStatus(), request.getOutputData());
    }
}
