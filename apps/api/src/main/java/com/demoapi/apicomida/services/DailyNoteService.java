package com.demoapi.apicomida.services;

import com.demoapi.apicomida.dtos.DailyNoteDtos.DailyNoteRequest;
import com.demoapi.apicomida.dtos.DailyNoteDtos.DailyNoteResponse;
import com.demoapi.apicomida.exception.ApiException;
import com.demoapi.apicomida.models.DailyNote;
import com.demoapi.apicomida.models.UserAccount;
import com.demoapi.apicomida.repositories.DailyNoteRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DailyNoteService {

    private final DailyNoteRepository dailyNoteRepository;
    private final CurrentUserService currentUserService;

    public DailyNoteService(DailyNoteRepository dailyNoteRepository, CurrentUserService currentUserService) {
        this.dailyNoteRepository = dailyNoteRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public DailyNoteResponse create(DailyNoteRequest request) {
        DailyNote note = new DailyNote();
        note.setUser(currentUserService.requireCurrentUser());
        apply(note, request);
        return toResponse(dailyNoteRepository.save(note));
    }

    public List<DailyNoteResponse> getByDate(LocalDate date) {
        return dailyNoteRepository.findByUserAndNoteDateOrderByCreatedAtDesc(currentUserService.requireCurrentUser(), date)
                .stream().map(this::toResponse).toList();
    }

    public List<DailyNoteResponse> getByRange(LocalDate startDate, LocalDate endDate) {
        return dailyNoteRepository.findByUserAndNoteDateBetweenOrderByNoteDateAscCreatedAtAsc(
                currentUserService.requireCurrentUser(), startDate, endDate
        ).stream().map(this::toResponse).toList();
    }

    public DailyNoteResponse getById(UUID id) {
        return toResponse(requireOwned(id));
    }

    @Transactional
    public DailyNoteResponse update(UUID id, DailyNoteRequest request) {
        DailyNote note = requireOwned(id);
        apply(note, request);
        return toResponse(dailyNoteRepository.save(note));
    }

    @Transactional
    public void delete(UUID id) {
        dailyNoteRepository.delete(requireOwned(id));
    }

    private void apply(DailyNote note, DailyNoteRequest request) {
        note.setNoteDate(request.noteDate());
        note.setMood(request.mood());
        note.setEnergyLevel(request.energyLevel());
        note.setSleepHours(request.sleepHours());
        note.setContent(request.content());
    }

    private DailyNote requireOwned(UUID id) {
        UserAccount user = currentUserService.requireCurrentUser();
        return dailyNoteRepository.findById(id)
                .filter(note -> note.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Daily note not found"));
    }

    private DailyNoteResponse toResponse(DailyNote note) {
        return new DailyNoteResponse(
                note.getId(),
                note.getNoteDate(),
                note.getMood(),
                note.getEnergyLevel(),
                note.getSleepHours(),
                note.getContent()
        );
    }
}
