package com.example.loginapp.controller;

import com.example.loginapp.dto.NotificacaoDTO;
import com.example.loginapp.entity.Notificacao;
import com.example.loginapp.service.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notificacoes")
@CrossOrigin(origins = "http://localhost:8081")
public class NotificacaoController {

    @Autowired private NotificacaoService notificacaoService;

    @PostMapping
    public ResponseEntity<Notificacao> criarNotificacao(@RequestBody NotificacaoDTO dto) {
        return ResponseEntity.ok(notificacaoService.criarNotificacao(dto));
    }

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<Notificacao>> getNotificacoes(@PathVariable Long userId) {
        return ResponseEntity.ok(notificacaoService.getNotificacoesPorUsuario(userId));
    }

    @PostMapping("/marcar-como-lidas")
    public ResponseEntity<Void> marcarComoLidas(@RequestBody List<Long> ids) {
        notificacaoService.marcarComoLidas(ids);
        return ResponseEntity.ok().build();
    }
}