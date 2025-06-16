package com.example.loginapp.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class MedicamentoDTO {

    private String nome;
    private String dosagem;
    private String tipo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataInicio;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataTermino;
    private int vezesPorDia;
    
    private List<String> instrucoes;
    
    private Long userId;
    private Long bulaId;

    public MedicamentoDTO() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDosagem() {
        return dosagem;
    }

    public void setDosagem(String dosagem) {
        this.dosagem = dosagem;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataTermino() {
        return dataTermino;
    }

    public void setDataTermino(LocalDate dataTermino) {
        this.dataTermino = dataTermino;
    }

    public int getVezesPorDia() {
        return vezesPorDia;
    }

    public void setVezesPorDia(int vezesPorDia) {
        this.vezesPorDia = vezesPorDia;
    }

    public List<String> getInstrucoes() {
        return instrucoes;
    }

    public void setInstrucoes(List<String> instrucoes) {
        this.instrucoes = instrucoes;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBulaId() {
        return bulaId;
    }

    public void setBulaId(Long bulaId) {
        this.bulaId = bulaId;
    }
}