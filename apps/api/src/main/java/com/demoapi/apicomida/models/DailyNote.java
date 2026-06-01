package com.demoapi.apicomida.models;

import com.demoapi.apicomida.models.base.TimestampedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "daily_notes")
@Getter
@Setter
@NoArgsConstructor
public class DailyNote extends TimestampedEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Column(name = "note_date", nullable = false)
    private LocalDate noteDate;

    private String mood;

    @Column(name = "energy_level")
    private Integer energyLevel;

    @Column(name = "sleep_hours", precision = 10, scale = 2)
    private BigDecimal sleepHours;

    @Column(columnDefinition = "TEXT")
    private String content;
}
