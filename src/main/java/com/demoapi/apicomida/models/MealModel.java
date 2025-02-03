package com.demoapi.apicomida.models;

import com.demoapi.apicomida.models.DTO.RecipeDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "meals")
public class MealModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private UserModel idUser;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "meal_id")
    private List<RecipeModel> recipes = new ArrayList<>();

    private String date;
    private double totalCalories;

    public MealModel() {}
}
