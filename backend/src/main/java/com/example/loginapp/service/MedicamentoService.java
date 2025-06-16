package com.example.loginapp.service;

import com.example.loginapp.dto.CompraDTO;
import com.example.loginapp.dto.MedicamentoDTO;
import com.example.loginapp.entity.EstoqueMedicamento;
import com.example.loginapp.entity.Medicamento;

import java.util.List;
import java.util.Optional;

public interface MedicamentoService {
    Medicamento adicionarMedicamento(MedicamentoDTO dto);
    List<Medicamento> listarPorUsuario(Long userId);
    Optional<Medicamento> buscarPorId(Long id);
    void registrarDose(Long medicamentoId);

    List<EstoqueMedicamento> comprarMedicamento(CompraDTO compraDTO);
    List<EstoqueMedicamento> listarEstoquePorUsuario(Long userId);
}