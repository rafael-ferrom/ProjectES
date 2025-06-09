package com.example.loginapp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime; // Import LocalDateTime

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "frequencias")
public class Frequencia {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDate dataInicio;
  private LocalDate dataTermino;
  private int vezesPorDia;

  // NEW: Field to store the exact timestamp of the first dose taken
  private LocalDateTime primeiraDoseTimestamp;

  public Frequencia() {
  }

  public Frequencia(LocalDate dataInicio, LocalDate dataTermino, int vezesPorDia) {
    this.dataInicio = dataInicio;
    this.dataTermino = dataTermino;
    this.vezesPorDia = vezesPorDia;
    this.primeiraDoseTimestamp = null; // Starts as null
  }

  // Getters and Setters for all fields...
  
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  // NEW: Getter and Setter for the new field
  public LocalDateTime getPrimeiraDoseTimestamp() {
    return primeiraDoseTimestamp;
  }

  public void setPrimeiraDoseTimestamp(LocalDateTime primeiraDoseTimestamp) {
    this.primeiraDoseTimestamp = primeiraDoseTimestamp;
  }
}