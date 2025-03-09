package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<UserModel, Long> {
    UserModel findByName(String username);
    UserModel findByEmail(String email);
    Optional<UserModel> findById(long id);
}
