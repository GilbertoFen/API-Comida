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
@Table(name = "recipe_ingredients")
@Getter
@Setter
@NoArgsConstructor
public class RecipeIngredient extends BaseUuidEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ManyToOne(optional = false)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Column(precision = 10, scale = 2)
    private BigDecimal quantity;

    private String unit;

    @Column(precision = 10, scale = 2)
    private BigDecimal calories;

    @Column(name = "protein_g", precision = 10, scale = 2)
    private BigDecimal proteinG;

    @Column(name = "carbs_g", precision = 10, scale = 2)
    private BigDecimal carbsG;

    @Column(name = "fat_g", precision = 10, scale = 2)
    private BigDecimal fatG;
}
