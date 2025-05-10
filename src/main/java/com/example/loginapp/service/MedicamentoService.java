package com.example.loginapp.service;

import java.util.List;
import java.util.Optional;

import com.example.loginapp.dto.MedicamentoDTO;
import com.example.loginapp.entity.Medicamento;

public interface MedicamentoService {
	
	Medicamento adicionarMedicamento(MedicamentoDTO dto);
    List<Medicamento> listarPorUsuario(Long userId);
    Optional<Medicamento> buscarPorId(Long id);
}
