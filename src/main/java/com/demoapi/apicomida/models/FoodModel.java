package com.demoapi.apicomida.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "products")
public class FoodModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String country;

    @NotBlank
    private String category;

    @NotBlank
    private String name;

    @Positive
    private Double quantity;

    @NotNull
    private int unit;

    @PositiveOrZero
    private Double calories;

    @PositiveOrZero
    private Double protein;

    @PositiveOrZero
    private Double carb;

    @PositiveOrZero
    private Double fat;

    @PositiveOrZero
    private Double sugar;

    @PositiveOrZero
    private Double sodium;

    @ManyToMany(mappedBy = "ingredients")
    private List<RecipeModel> recipes = new ArrayList<>();

    public FoodModel() {
    }
}
