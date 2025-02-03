package com.demoapi.apicomida.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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
    private long id;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private UserModel user;

    private String name;
    private String description;
    private String instructions;

    @ManyToMany
    @JoinTable(
            name = "recipe_ingredients",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "food_id")
    )
    private List<FoodModel> ingredients = new ArrayList<>();

    public RecipeModel() {}
}

