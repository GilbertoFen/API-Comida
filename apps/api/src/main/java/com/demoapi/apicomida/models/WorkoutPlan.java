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
@Table(name = "workout_plans")
@Getter
@Setter
@NoArgsConstructor
public class WorkoutPlan extends TimestampedEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String goal;

    @Column(name = "days_per_week")
    private Integer daysPerWeek;

    @Column(name = "is_ai_generated", nullable = false)
    private boolean isAiGenerated;
}
