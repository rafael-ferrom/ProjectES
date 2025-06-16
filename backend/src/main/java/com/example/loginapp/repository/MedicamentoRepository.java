package com.example.loginapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.loginapp.entity.Medicamento;
import com.example.loginapp.entity.User;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
	 List<Medicamento> findByUser(User user);
}
