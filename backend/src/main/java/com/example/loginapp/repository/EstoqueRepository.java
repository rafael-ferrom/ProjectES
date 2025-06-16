package com.example.loginapp.repository;

import com.example.loginapp.entity.Bula;
import com.example.loginapp.entity.EstoqueMedicamento;
import com.example.loginapp.entity.EstoqueStatus;
import com.example.loginapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<EstoqueMedicamento, Long> {

    List<EstoqueMedicamento> findByUser(User user);
    
    List<EstoqueMedicamento> findByUserAndBula(User user, Bula bula);

    // Encontra o estoque disponível para um usuário e bula, ordenado pela data de validade mais próxima
    List<EstoqueMedicamento> findByUserAndBulaAndStatusAndDataValidadeAfter(
        User user, Bula bula, EstoqueStatus status, LocalDate data, org.springframework.data.domain.Sort sort
    );
}