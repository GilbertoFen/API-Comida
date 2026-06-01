package com.demoapi.apicomida.models;

import com.demoapi.apicomida.models.base.BaseUuidEntity;
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
@Table(name = "workout_log_exercises")
@Getter
@Setter
@NoArgsConstructor
public class WorkoutLogExercise extends BaseUuidEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "workout_log_id", nullable = false)
    private WorkoutLog workoutLog;

    @ManyToOne(optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "sets_done")
    private Integer setsDone;

    @Column(name = "reps_done")
    private String repsDone;

    @Column(name = "weight_kg", precision = 10, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "ai_recommended_duration_seconds")
    private Integer aiRecommendedDurationSeconds;

    @Column(name = "ai_score")
    private Integer aiScore;

    @Column(name = "user_notes", columnDefinition = "TEXT")
    private String userNotes;
}
