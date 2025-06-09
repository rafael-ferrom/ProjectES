package com.example.loginapp.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import com.example.loginapp.dto.MedicamentoDTO;
import com.example.loginapp.entity.Bula;
import com.example.loginapp.entity.Frequencia;
import com.example.loginapp.entity.Instrucao;
import com.example.loginapp.entity.Medicamento;
import com.example.loginapp.service.MedicamentoService;

@RestController
@RequestMapping("/api/medicamentos")
@CrossOrigin(origins = "http://localhost:8081")
public class MedicamentoController {

	private final MedicamentoService medicamentoService;

	@Autowired
	public MedicamentoController(MedicamentoService medicamentoService) {
		this.medicamentoService = medicamentoService;
	}

	@PostMapping
	public ResponseEntity<Medicamento> adicionar(@RequestBody MedicamentoDTO dto) {
		Medicamento medicamento = medicamentoService.adicionarMedicamento(dto);
		return ResponseEntity.ok(medicamento);
	}

	@GetMapping("/usuario/{userId}")
	public ResponseEntity<List<Medicamento>> listar(@PathVariable Long userId) {
		List<Medicamento> lista = medicamentoService.listarPorUsuario(userId);
		return ResponseEntity.ok(lista);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Medicamento> buscarPorId(@PathVariable Long id) {
		Optional<Medicamento> medicamento = medicamentoService.buscarPorId(id);
		return medicamento.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/{id}/frequencia")
	public ResponseEntity<Frequencia> buscarFrequencia(@PathVariable Long id) {
		Optional<Medicamento> medicamento = medicamentoService.buscarPorId(id);
		return medicamento.map(med -> ResponseEntity.ok(med.getFrequencia()))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/{id}/bula")
	public ResponseEntity<Bula> buscarBula(@PathVariable Long id) {
		Optional<Medicamento> medicamento = medicamentoService.buscarPorId(id);
		return medicamento.map(med -> ResponseEntity.ok(med.getBula()))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/{id}/instrucoes")
	public ResponseEntity<List<String>> buscarInstrucoes(@PathVariable Long id) {
		Optional<Medicamento> medicamento = medicamentoService.buscarPorId(id);
		return medicamento.map(med -> {
			List<String> instrucoes = med.getInstrucoes().stream().map(Instrucao::getDescricao)
					.collect(Collectors.toList());
			return ResponseEntity.ok(instrucoes);
		}).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}/iniciar-tratamento")
  public ResponseEntity<Medicamento> iniciarTratamento(@PathVariable Long id) {
    try {
      Medicamento medicamentoAtualizado = medicamentoService.iniciarTratamento(id);
      return ResponseEntity.ok(medicamentoAtualizado);
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }
}
