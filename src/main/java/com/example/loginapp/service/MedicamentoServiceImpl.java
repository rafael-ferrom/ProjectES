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
import com.example.loginapp.repository.MedicamentoRepository;
import com.example.loginapp.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class MedicamentoServiceImpl implements MedicamentoService {

	private final MedicamentoRepository medicamentoRepository;
	private final UserRepository userRepository;

	@Autowired
	public MedicamentoServiceImpl(MedicamentoRepository medicamentoRepository, UserRepository userRepository) {
		this.medicamentoRepository = medicamentoRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public Medicamento adicionarMedicamento(MedicamentoDTO dto) {
		Optional<User> userOpt = userRepository.findById(dto.getUserId());
		if (userOpt.isEmpty())
			throw new RuntimeException("Usuário não encontrado.");

		User user = userOpt.get();

		// Criar objeto Frequencia
		Frequencia frequencia = new Frequencia(dto.getDataInicio(), dto.getDataTermino(), dto.getVezesPorDia());

		// Criar objeto Bula
		Bula bula = new Bula(dto.getNomeComercial(), dto.getPrincipioAtivo(), dto.getConcentracao(),
				dto.getFormaFarmaceutica(), dto.getApresentacao(), dto.getFabricante());

		// Criar o medicamento com os novos objetos
		Medicamento medicamento = new Medicamento(dto.getNome(), dto.getDosagem(), frequencia, bula, dto.getTipo(),
				user);

		// Adicionar as instruções
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

}
