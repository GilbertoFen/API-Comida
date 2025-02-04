package com.demoapi.apicomida.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "recipes")
public class RecipeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private UserModel idUser;

    @ManyToMany
    @JoinTable(
            name = "recipe_ingredients",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "food_id")
    )
    private List<FoodModel> ingredients;

    private String description;

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

    public RecipeModel() {}
}