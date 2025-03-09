package com.demoapi.apicomida.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @NotBlank
    private String password;

    @Positive
    private float weight;

    @Positive
    private float height;

    @NotNull
    private int exerciseLevel;


    @OneToMany(mappedBy = "idUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeModel> recipes = new ArrayList<>();
}
