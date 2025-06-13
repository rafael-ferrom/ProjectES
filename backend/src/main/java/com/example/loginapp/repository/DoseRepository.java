package com.example.loginapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.loginapp.entity.Dose;

public interface DoseRepository extends JpaRepository<Dose, Long> {
}