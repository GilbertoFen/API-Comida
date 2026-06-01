package com.demoapi.apicomida.models;

import com.demoapi.apicomida.models.base.CreatedAtEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "recipe_post_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"recipe_post_id", "user_id"})
)
@Getter
@Setter
@NoArgsConstructor
public class RecipePostLike extends CreatedAtEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "recipe_post_id", nullable = false)
    private RecipePost recipePost;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;
}
