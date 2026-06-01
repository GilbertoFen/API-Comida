package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.OnboardingQuestionnaire;
import com.demoapi.apicomida.models.UserAccount;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OnboardingQuestionnaireRepository extends JpaRepository<OnboardingQuestionnaire, UUID> {
    Optional<OnboardingQuestionnaire> findFirstByUserOrderByCreatedAtDesc(UserAccount user);
    List<OnboardingQuestionnaire> findByUserOrderByCreatedAtDesc(UserAccount user);
}
