// NotificacaoServiceImpl.java
package com.example.loginapp.service;

import com.example.loginapp.dto.NotificacaoDTO;
import com.example.loginapp.entity.Notificacao;
import com.example.loginapp.entity.User;
import com.example.loginapp.repository.NotificacaoRepository;
import com.example.loginapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class NotificacaoServiceImpl implements NotificacaoService {

    @Autowired private NotificacaoRepository notificacaoRepository;
    @Autowired private UserRepository userRepository;

    @Override
    public Notificacao criarNotificacao(NotificacaoDTO dto) {
      User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
      Notificacao notificacao = new Notificacao();
      notificacao.setUser(user);
      notificacao.setMensagem(dto.getMensagem());
      notificacao.setTipo(dto.getTipo());
      notificacao.setTimestamp(LocalDateTime.now(ZoneOffset.UTC));
      return notificacaoRepository.save(notificacao);
  }

    @Override
    public List<Notificacao> getNotificacoesPorUsuario(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return notificacaoRepository.findByUserOrderByTimestampDesc(user);
    }

    @Override
    public void marcarComoLidas(List<Long> ids) {
        List<Notificacao> notificacoes = notificacaoRepository.findAllById(ids);
        notificacoes.forEach(n -> n.setLida(true));
        notificacaoRepository.saveAll(notificacoes);
    }
}