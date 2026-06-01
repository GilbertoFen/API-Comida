package com.demoapi.apicomida.models;

import com.demoapi.apicomida.models.base.CreatedAtEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor
public class Exercise extends CreatedAtEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "muscle_group")
    private String muscleGroup;

    private String equipment;
    private String difficulty;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "recommended_sets")
    private Integer recommendedSets;

    @Column(name = "recommended_reps")
    private String recommendedReps;

    @Column(name = "recommended_time_seconds")
    private Integer recommendedTimeSeconds;
}
