package com.example.loginapp.controller;

import com.example.loginapp.dto.CompraDTO;
import com.example.loginapp.entity.EstoqueMedicamento;
import com.example.loginapp.service.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estoque")
@CrossOrigin(origins = "http://localhost:8081")
public class EstoqueController {

    private final MedicamentoService medicamentoService;

    @Autowired
    public EstoqueController(MedicamentoService medicamentoService) {
        this.medicamentoService = medicamentoService;
    }

    @PostMapping("/comprar")
    public ResponseEntity<List<EstoqueMedicamento>> comprarMedicamento(@RequestBody CompraDTO compraDTO) {
        try {
            List<EstoqueMedicamento> novasCaixas = medicamentoService.comprarMedicamento(compraDTO);
            return ResponseEntity.ok(novasCaixas);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<EstoqueMedicamento>> listarEstoque(@PathVariable Long userId) {
        try {
            List<EstoqueMedicamento> estoque = medicamentoService.listarEstoquePorUsuario(userId);
            return ResponseEntity.ok(estoque);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}