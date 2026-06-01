package com.demoapi.apicomida.models;

import com.demoapi.apicomida.models.base.CreatedAtEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "workout_logs")
@Getter
@Setter
@NoArgsConstructor
public class WorkoutLog extends CreatedAtEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @ManyToOne
    @JoinColumn(name = "workout_plan_id")
    private WorkoutPlan workoutPlan;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "total_duration_minutes")
    private Integer totalDurationMinutes;

    @Column(name = "user_rating")
    private Integer userRating;

    @Column(name = "ai_rating")
    private Integer aiRating;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
