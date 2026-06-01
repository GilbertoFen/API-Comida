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
@Table(name = "foods")
@Getter
@Setter
@NoArgsConstructor
public class Food extends TimestampedEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @Column(nullable = false)
    private String name;

    private String brand;

    @Column(nullable = false)
    private String category;

    @Column(name = "serving_size", precision = 10, scale = 2)
    private BigDecimal servingSize;

    @Column(name = "serving_unit")
    private String servingUnit;

    @Column(name = "calories", precision = 10, scale = 2)
    private BigDecimal caloriesPer100g;

    @Column(name = "protein_g", precision = 10, scale = 2)
    private BigDecimal proteinPer100g;

    @Column(name = "carbs_g", precision = 10, scale = 2)
    private BigDecimal carbsPer100g;

    @Column(name = "fat_g", precision = 10, scale = 2)
    private BigDecimal fatPer100g;

    @Column(name = "fiber_g", precision = 10, scale = 2)
    private BigDecimal fiberPer100g;

    @Column(name = "sugar_g", precision = 10, scale = 2)
    private BigDecimal sugarPer100g;

    @Column(name = "sodium_mg", precision = 10, scale = 2)
    private BigDecimal sodiumPer100g;

    private String barcode;

    @Column(name = "source", nullable = false)
    private String externalSource;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;

    @Column(nullable = false)
    private boolean verified;
}
