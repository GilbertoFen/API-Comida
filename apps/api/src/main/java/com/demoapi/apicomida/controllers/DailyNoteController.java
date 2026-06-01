package com.demoapi.apicomida.controllers;

import com.demoapi.apicomida.dtos.DailyNoteDtos.DailyNoteRequest;
import com.demoapi.apicomida.services.DailyNoteService;
import com.demoapi.apicomida.util.ApiResponses;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DailyNoteController {

    private final DailyNoteService dailyNoteService;

    public DailyNoteController(DailyNoteService dailyNoteService) {
        this.dailyNoteService = dailyNoteService;
    }

    @PostMapping("/daily-notes")
    public Object create(@Valid @RequestBody DailyNoteRequest request) {
        return dailyNoteService.create(request);
    }

    @GetMapping("/daily-notes")
    public Object getByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return dailyNoteService.getByDate(date);
    }

    @GetMapping("/daily-notes/range")
    public Object getByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return dailyNoteService.getByRange(startDate, endDate);
    }

    @GetMapping("/daily-notes/{id}")
    public Object getById(@PathVariable UUID id) {
        return dailyNoteService.getById(id);
    }

    @PatchMapping("/daily-notes/{id}")
    public Object update(@PathVariable UUID id, @Valid @RequestBody DailyNoteRequest request) {
        return dailyNoteService.update(id, request);
    }

    @DeleteMapping("/daily-notes/{id}")
    public Object delete(@PathVariable UUID id) {
        dailyNoteService.delete(id);
        return ApiResponses.message("Daily note deleted");
    }
}
