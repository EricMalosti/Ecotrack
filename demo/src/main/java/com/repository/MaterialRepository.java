package com.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.Material;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
}