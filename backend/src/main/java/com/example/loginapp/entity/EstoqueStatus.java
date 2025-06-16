package com.example.loginapp.entity;

public enum EstoqueStatus {
    DISPONIVEL, // A caixa tem comprimidos e não está vencida
    VAZIO,      // A caixa não tem mais comprimidos
    VENCIDO     // A caixa passou da data de validade
}