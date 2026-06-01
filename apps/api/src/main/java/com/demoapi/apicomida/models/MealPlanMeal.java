package com.demoapi.apicomida.models;

import com.demoapi.apicomida.models.base.BaseUuidEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "meal_plan_meals")
@Getter
@Setter
@NoArgsConstructor
public class MealPlanMeal extends BaseUuidEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "meal_plan_day_id", nullable = false)
    private MealPlanDay mealPlanDay;

    @Column(name = "meal_type", nullable = false)
    private String mealType;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "food_id")
    private Food food;

    @Column(precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(precision = 10, scale = 2)
    private BigDecimal calories;

    @Column(name = "protein_g", precision = 10, scale = 2)
    private BigDecimal proteinG;

    @Column(name = "carbs_g", precision = 10, scale = 2)
    private BigDecimal carbsG;

    @Column(name = "fat_g", precision = 10, scale = 2)
    private BigDecimal fatG;
}
