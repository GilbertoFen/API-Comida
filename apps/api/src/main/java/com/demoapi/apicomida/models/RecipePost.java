package com.demoapi.apicomida.models;

import com.demoapi.apicomida.models.base.TimestampedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "recipe_posts")
@Getter
@Setter
@NoArgsConstructor
public class RecipePost extends TimestampedEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "likes_count", nullable = false)
    private int likesCount;

    @Column(name = "comments_count", nullable = false)
    private int commentsCount;
}
