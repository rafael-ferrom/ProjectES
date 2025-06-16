package com.example.loginapp.entity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "doses")
public class Dose {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "medicamento_id", nullable = false)
    @JsonBackReference
    private Medicamento medicamento;
    
    @ManyToOne
    @JoinColumn(name = "estoque_id", nullable = true)
    private EstoqueMedicamento estoqueMedicamento;


    public Dose() {
    }

    public Dose(Medicamento medicamento, EstoqueMedicamento estoqueMedicamento) {
        this.medicamento = medicamento;
        this.estoqueMedicamento = estoqueMedicamento;
        this.timestamp = LocalDateTime.now(ZoneOffset.UTC);
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }

    public EstoqueMedicamento getEstoqueMedicamento() {
        return estoqueMedicamento;
    }

    public void setEstoqueMedicamento(EstoqueMedicamento estoqueMedicamento) {
        this.estoqueMedicamento = estoqueMedicamento;
    }
}