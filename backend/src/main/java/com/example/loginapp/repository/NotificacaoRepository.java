package com.example.loginapp.repository;

import com.example.loginapp.entity.Notificacao;
import com.example.loginapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByUserOrderByTimestampDesc(User user);
}