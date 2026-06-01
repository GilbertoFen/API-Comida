package com.demoapi.apicomida.models;

import com.demoapi.apicomida.models.base.TimestampedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
public class UserProfile extends TimestampedEntity {

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserAccount user;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private Integer age;
    private String gender;

    @Column(name = "weight_kg", precision = 10, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "height_cm", precision = 10, scale = 2)
    private BigDecimal heightCm;

    @Column(name = "activity_level")
    private String activityLevel;

    private String goal;

    @Column(precision = 10, scale = 2)
    private BigDecimal imc;

    @Column(name = "target_weight_kg", precision = 10, scale = 2)
    private BigDecimal targetWeightKg;

    @Column(name = "daily_calorie_goal", precision = 10, scale = 2)
    private BigDecimal dailyCalorieGoal;

    @Column(name = "daily_protein_goal", precision = 10, scale = 2)
    private BigDecimal dailyProteinGoal;

    @Column(name = "daily_carbs_goal", precision = 10, scale = 2)
    private BigDecimal dailyCarbsGoal;

    @Column(name = "daily_fat_goal", precision = 10, scale = 2)
    private BigDecimal dailyFatGoal;
}
