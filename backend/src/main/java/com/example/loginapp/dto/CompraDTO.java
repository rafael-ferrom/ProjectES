package com.example.loginapp.dto;

public class CompraDTO {

    private Long userId;
    private Long bulaId;
    private int numeroDeCaixas;
    private int comprimidosPorCaixa;
    private int validadeEmAnos;

    public CompraDTO() {
    }

    // Getters and Setters
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

    public int getNumeroDeCaixas() {
        return numeroDeCaixas;
    }

    public void setNumeroDeCaixas(int numeroDeCaixas) {
        this.numeroDeCaixas = numeroDeCaixas;
    }

    public int getComprimidosPorCaixa() {
        return comprimidosPorCaixa;
    }

    public void setComprimidosPorCaixa(int comprimidosPorCaixa) {
        this.comprimidosPorCaixa = comprimidosPorCaixa;
    }

    public int getValidadeEmAnos() {
        return validadeEmAnos;
    }

    public void setValidadeEmAnos(int validadeEmAnos) {
        this.validadeEmAnos = validadeEmAnos;
    }
}