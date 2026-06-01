package com.demoapi.apicomida.models;

import com.demoapi.apicomida.models.base.TimestampedEntity;
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
@Table(name = "recipes")
@Getter
@Setter
@NoArgsConstructor
public class Recipe extends TimestampedEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "prep_time_minutes")
    private Integer prepTimeMinutes;

    @Column(name = "cook_time_minutes")
    private Integer cookTimeMinutes;

    private Integer servings;

    @Column(name = "total_calories", precision = 10, scale = 2)
    private BigDecimal totalCalories;

    @Column(name = "total_protein_g", precision = 10, scale = 2)
    private BigDecimal totalProteinG;

    @Column(name = "total_carbs_g", precision = 10, scale = 2)
    private BigDecimal totalCarbsG;

    @Column(name = "total_fat_g", precision = 10, scale = 2)
    private BigDecimal totalFatG;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @Column(name = "is_ai_generated", nullable = false)
    private boolean isAiGenerated;

    @Column(name = "is_high_calorie", nullable = false)
    private boolean isHighCalorie;

    @Column(name = "is_high_protein", nullable = false)
    private boolean isHighProtein;

    @Column(name = "is_low_carb", nullable = false)
    private boolean isLowCarb;
}
