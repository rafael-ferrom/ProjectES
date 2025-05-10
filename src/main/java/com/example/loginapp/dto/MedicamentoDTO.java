package com.example.loginapp.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class MedicamentoDTO {

	private String nome;
    private String dosagem;
    
    // Frequencia
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataInicio;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataTermino;
    private int vezesPorDia;
    
    // Bula
    private String nomeComercial;
    private String principioAtivo;
    private String concentracao;
    private String formaFarmaceutica;
    private String apresentacao;
    private String fabricante;
    
    // Instrucoes
    private List<String> instrucoes;
    
    private String tipo;
    private Long userId;

    public MedicamentoDTO() {
    }

    // Getters e Setters
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

    public String getNomeComercial() {
        return nomeComercial;
    }

    public void setNomeComercial(String nomeComercial) {
        this.nomeComercial = nomeComercial;
    }

    public String getPrincipioAtivo() {
        return principioAtivo;
    }

    public void setPrincipioAtivo(String principioAtivo) {
        this.principioAtivo = principioAtivo;
    }

    public String getConcentracao() {
        return concentracao;
    }

    public void setConcentracao(String concentracao) {
        this.concentracao = concentracao;
    }

    public String getFormaFarmaceutica() {
        return formaFarmaceutica;
    }

    public void setFormaFarmaceutica(String formaFarmaceutica) {
        this.formaFarmaceutica = formaFarmaceutica;
    }

    public String getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(String apresentacao) {
        this.apresentacao = apresentacao;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
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
}


