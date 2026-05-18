package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.Premio;

@Repository
public interface PremioRepository extends JpaRepository<Premio, Long> {
}
