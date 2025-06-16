// NotificacaoService.java
package com.example.loginapp.service;

import com.example.loginapp.dto.NotificacaoDTO;
import com.example.loginapp.entity.Notificacao;
import java.util.List;

public interface NotificacaoService {
    Notificacao criarNotificacao(NotificacaoDTO dto);
    List<Notificacao> getNotificacoesPorUsuario(Long userId);
    void marcarComoLidas(List<Long> ids);
}