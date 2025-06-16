package com.example.loginapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.loginapp.entity.Instrucao;
import com.example.loginapp.entity.Medicamento;

public interface InstrucaoRepository extends JpaRepository<Instrucao, Long>{

	List<Instrucao> findByMedicamento(Medicamento medicamento);
}
