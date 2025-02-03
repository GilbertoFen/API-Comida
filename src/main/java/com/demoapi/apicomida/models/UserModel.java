package com.demoapi.apicomida.models;

import com.demoapi.apicomida.util.Exercise;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
@Table(name = "users")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idUser;

    @NotBlank
    private String name;

    @Min(18)
    @Max(100)
    private int age;

    @NotBlank
    @Email
    private String email;

    @Min(8)
    @NotBlank
    private String password;

    @Positive
    private float weight;

    @Positive
    private float height;

    @NotBlank
    private int exerciseLevel;

    @OneToMany(mappedBy = "idUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealModel> meals = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeModel> recipes = new ArrayList<>();

    public UserModel() {}
}
