package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.UserModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<UserModel> findByEmail(String email);
}
