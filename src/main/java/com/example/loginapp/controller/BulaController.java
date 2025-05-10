package com.example.loginapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.loginapp.entity.Bula;
import com.example.loginapp.repository.BulaRepository;

@RestController
@RequestMapping("/api/bulas")
public class BulaController {

	private final BulaRepository bulaRepository;

	@Autowired
	public BulaController(BulaRepository bulaRepository) {
		this.bulaRepository = bulaRepository;
	}

	@GetMapping
	public ResponseEntity<List<Bula>> listarTodasBulas() {
		List<Bula> bulas = bulaRepository.findAll();
		return ResponseEntity.ok(bulas);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Bula> buscarBulaPorId(@PathVariable Long id) {
		Optional<Bula> bula = bulaRepository.findById(id);
		return bula.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}
}
