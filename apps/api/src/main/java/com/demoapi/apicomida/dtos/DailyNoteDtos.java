package com.demoapi.apicomida.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class DailyNoteDtos {

    private DailyNoteDtos() {
    }

    public record DailyNoteRequest(
            LocalDate noteDate,
            String mood,
            Integer energyLevel,
            BigDecimal sleepHours,
            String content
    ) {
    }

    public record DailyNoteResponse(
            UUID id,
            LocalDate noteDate,
            String mood,
            Integer energyLevel,
            BigDecimal sleepHours,
            String content
    ) {
    }
}
