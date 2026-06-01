package com.demoapi.apicomida.models;

import com.demoapi.apicomida.models.base.BaseUuidEntity;
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
@Table(name = "meal_plan_days")
@Getter
@Setter
@NoArgsConstructor
public class MealPlanDay extends BaseUuidEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "meal_plan_id", nullable = false)
    private MealPlan mealPlan;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    @Column(name = "day_date")
    private LocalDate dayDate;

    @Column(name = "total_calories", precision = 10, scale = 2)
    private BigDecimal totalCalories;

    @Column(name = "total_protein_g", precision = 10, scale = 2)
    private BigDecimal totalProteinG;

    @Column(name = "total_carbs_g", precision = 10, scale = 2)
    private BigDecimal totalCarbsG;

    @Column(name = "total_fat_g", precision = 10, scale = 2)
    private BigDecimal totalFatG;
}
