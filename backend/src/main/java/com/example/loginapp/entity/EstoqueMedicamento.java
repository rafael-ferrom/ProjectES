package com.example.loginapp.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "estoque_medicamentos")
public class EstoqueMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "bula_id", nullable = false)
    private Bula bula;

    private LocalDate dataAquisicao;
    private LocalDate dataValidade;
    private int quantidadeComprimidos;
    private int quantidadeInicialComprimidos;

    @Enumerated(EnumType.STRING)
    private EstoqueStatus status;

    public EstoqueMedicamento() {
    }

    public EstoqueMedicamento(User user, Bula bula, LocalDate dataAquisicao, LocalDate dataValidade, int quantidadeInicialComprimidos) {
        this.user = user;
        this.bula = bula;
        this.dataAquisicao = dataAquisicao;
        this.dataValidade = dataValidade;
        this.quantidadeInicialComprimidos = quantidadeInicialComprimidos;
        this.quantidadeComprimidos = quantidadeInicialComprimidos;
        this.status = EstoqueStatus.DISPONIVEL;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Bula getBula() {
        return bula;
    }

    public void setBula(Bula bula) {
        this.bula = bula;
    }

    public LocalDate getDataAquisicao() {
        return dataAquisicao;
    }

    public void setDataAquisicao(LocalDate dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public int getQuantidadeComprimidos() {
        return quantidadeComprimidos;
    }

    public void setQuantidadeComprimidos(int quantidadeComprimidos) {
        this.quantidadeComprimidos = quantidadeComprimidos;
    }

    public int getQuantidadeInicialComprimidos() {
        return quantidadeInicialComprimidos;
    }

    public void setQuantidadeInicialComprimidos(int quantidadeInicialComprimidos) {
        this.quantidadeInicialComprimidos = quantidadeInicialComprimidos;
    }

    public EstoqueStatus getStatus() {
        return status;
    }

    public void setStatus(EstoqueStatus status) {
        this.status = status;
    }
}