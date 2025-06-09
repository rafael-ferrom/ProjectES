package com.example.loginapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.loginapp.dto.MedicamentoDTO;
import com.example.loginapp.entity.Bula;
import com.example.loginapp.entity.Frequencia;
import com.example.loginapp.entity.Medicamento;
import com.example.loginapp.entity.User;
import com.example.loginapp.entity.Dose;
import com.example.loginapp.repository.BulaRepository;
import com.example.loginapp.repository.MedicamentoRepository;
import com.example.loginapp.repository.UserRepository;
import com.example.loginapp.repository.DoseRepository;

import jakarta.transaction.Transactional;

@Service
public class MedicamentoServiceImpl implements MedicamentoService {

  private final MedicamentoRepository medicamentoRepository;
  private final UserRepository userRepository;
  private final BulaRepository bulaRepository;
  private final DoseRepository doseRepository;

  @Autowired
  public MedicamentoServiceImpl(MedicamentoRepository medicamentoRepository, UserRepository userRepository, BulaRepository bulaRepository, DoseRepository doseRepository) {
    this.medicamentoRepository = medicamentoRepository;
    this.userRepository = userRepository;
    this.bulaRepository = bulaRepository;
    this.doseRepository = doseRepository;
  }

  @Override
  @Transactional
  public Medicamento adicionarMedicamento(MedicamentoDTO dto) {
    User user = userRepository.findById(dto.getUserId())
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

    Bula bula = bulaRepository.findById(dto.getBulaId())
        .orElseThrow(() -> new RuntimeException("Bula não encontrada."));

    Frequencia frequencia = new Frequencia(dto.getDataInicio(), dto.getDataTermino(), dto.getVezesPorDia());

    Medicamento medicamento = new Medicamento(dto.getNome(), dto.getDosagem(), frequencia, bula, dto.getTipo(),
        user);

    if (dto.getInstrucoes() != null && !dto.getInstrucoes().isEmpty()) {
      for (String instrucaoTexto : dto.getInstrucoes()) {
        medicamento.adicionarInstrucao(instrucaoTexto);
      }
    }

    return medicamentoRepository.save(medicamento);
  }

	@Override
	public List<Medicamento> listarPorUsuario(Long userId) {
		Optional<User> userOpt = userRepository.findById(userId);
		if (userOpt.isEmpty())
			throw new RuntimeException("Usuário não encontrado.");

		return medicamentoRepository.findByUser(userOpt.get());
	}

	@Override
	public Optional<Medicamento> buscarPorId(Long id) {
		return medicamentoRepository.findById(id);
	}

	@Override
  @Transactional
  public void registrarDose(Long medicamentoId) {
    Medicamento medicamento = medicamentoRepository.findById(medicamentoId)
        .orElseThrow(() -> new RuntimeException("Medicamento não encontrado."));
    
    Dose novaDose = new Dose(medicamento);
    doseRepository.save(novaDose);
  }
}
