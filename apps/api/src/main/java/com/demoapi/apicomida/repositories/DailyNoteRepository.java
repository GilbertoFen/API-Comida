package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.DailyNote;
import com.demoapi.apicomida.models.UserAccount;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyNoteRepository extends JpaRepository<DailyNote, UUID> {
    List<DailyNote> findByUserAndNoteDateOrderByCreatedAtDesc(UserAccount user, LocalDate noteDate);
    List<DailyNote> findByUserAndNoteDateBetweenOrderByNoteDateAscCreatedAtAsc(UserAccount user, LocalDate startDate, LocalDate endDate);
}
