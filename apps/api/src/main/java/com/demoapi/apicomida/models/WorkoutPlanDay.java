package com.demoapi.apicomida.models;

import com.demoapi.apicomida.models.base.BaseUuidEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "workout_plan_days")
@Getter
@Setter
@NoArgsConstructor
public class WorkoutPlanDay extends BaseUuidEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "workout_plan_id", nullable = false)
    private WorkoutPlan workoutPlan;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    @Column(nullable = false)
    private String title;

    @Column(name = "focus_area")
    private String focusArea;
}
