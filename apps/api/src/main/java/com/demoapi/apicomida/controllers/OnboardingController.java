package com.demoapi.apicomida.controllers;

import com.demoapi.apicomida.dtos.OnboardingDtos.GoalCalculationRequest;
import com.demoapi.apicomida.dtos.OnboardingDtos.QuestionnaireRequest;
import com.demoapi.apicomida.services.OnboardingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OnboardingController {

    private final OnboardingService onboardingService;

    public OnboardingController(OnboardingService onboardingService) {
        this.onboardingService = onboardingService;
    }

    @PostMapping("/onboarding")
    public Object create(@Valid @RequestBody QuestionnaireRequest request) {
        return onboardingService.create(request);
    }

    @GetMapping("/onboarding/me")
    public Object getMe() {
        return onboardingService.getMe();
    }

    @PatchMapping("/onboarding/me")
    public Object updateMe(@Valid @RequestBody QuestionnaireRequest request) {
        return onboardingService.updateMe(request);
    }

    @PostMapping("/onboarding/calculate-goals")
    public Object calculateGoals(@Valid @RequestBody GoalCalculationRequest request) {
        return onboardingService.calculateGoals(request);
    }
}
