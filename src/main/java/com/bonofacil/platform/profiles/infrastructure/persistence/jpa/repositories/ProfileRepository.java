package com.bonofacil.platform.profiles.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import  com.bonofacil.platform.profiles.domain.model.aggregates.Profile;
import  com.bonofacil.platform.profiles.domain.model.valueobjects.EmailAddress;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
  Optional<Profile> findByEmail(EmailAddress emailAddress);
}
