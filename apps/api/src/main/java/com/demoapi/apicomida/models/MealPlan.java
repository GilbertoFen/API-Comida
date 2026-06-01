package com.demoapi.apicomida.models;

import com.demoapi.apicomida.models.base.TimestampedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "meal_plans")
@Getter
@Setter
@NoArgsConstructor
public class MealPlan extends TimestampedEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "daily_calorie_target", precision = 10, scale = 2)
    private BigDecimal dailyCalorieTarget;

    @Column(name = "is_ai_generated", nullable = false)
    private boolean isAiGenerated;

    @Column(name = "has_excess_calories", nullable = false)
    private boolean hasExcessCalories;

    @Column(name = "has_low_protein", nullable = false)
    private boolean hasLowProtein;

    @Column(name = "has_low_calories", nullable = false)
    private boolean hasLowCalories;
}
