package com.demoapi.apicomida.models;

import com.demoapi.apicomida.models.base.CreatedAtEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "onboarding_questionnaires")
@Getter
@Setter
@NoArgsConstructor
public class OnboardingQuestionnaire extends CreatedAtEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Column(name = "main_goal")
    private String mainGoal;

    @Column(name = "current_weight_kg", precision = 10, scale = 2)
    private BigDecimal currentWeightKg;

    @Column(name = "target_weight_kg", precision = 10, scale = 2)
    private BigDecimal targetWeightKg;

    @Column(name = "height_cm", precision = 10, scale = 2)
    private BigDecimal heightCm;

    private Integer age;
    private String gender;

    @Column(name = "activity_level")
    private String activityLevel;

    @Column(name = "training_days_per_week")
    private Integer trainingDaysPerWeek;

    @Column(name = "diet_type")
    private String dietType;

    @Column(name = "food_restrictions", columnDefinition = "TEXT")
    private String foodRestrictions;

    @Column(columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "preferred_foods", columnDefinition = "TEXT")
    private String preferredFoods;

    @Column(name = "disliked_foods", columnDefinition = "TEXT")
    private String dislikedFoods;

    @Column(name = "health_notes", columnDefinition = "TEXT")
    private String healthNotes;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
