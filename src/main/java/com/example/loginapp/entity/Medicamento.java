package com.example.loginapp.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "medicamentos")
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String dosagem;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "frequencia_id")
    private Frequencia frequencia;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bula_id")
    private Bula bula;

    @OneToMany(mappedBy = "medicamento", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference // Lado "gerenciado" da referência
    private List<Instrucao> instrucoes = new ArrayList<>();

    private String tipo; // Ex: "comprimido", "líquido", "injeção"

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // NEW: One-to-Many relationship with Dose
    @OneToMany(mappedBy = "medicamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Dose> doses = new ArrayList<>();

    // Construtores
    public Medicamento() {
    }

    public Medicamento(String nome, String dosagem, Frequencia frequencia, Bula bula, String tipo, User user) {
        this.nome = nome;
        this.dosagem = dosagem;
        this.frequencia = frequencia;
        this.bula = bula;
        this.tipo = tipo;
        this.user = user;
    }

    // Método para adicionar instruções
    public void adicionarInstrucao(String descricao) {
        Instrucao instrucao = new Instrucao();
        instrucao.setDescricao(descricao);
        instrucao.setMedicamento(this);
        this.instrucoes.add(instrucao);
    }

    // Getters e Setters existentes
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Frequencia getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(Frequencia frequencia) {
        this.frequencia = frequencia;
    }

    public Bula getBula() {
        return bula;
    }

    public void setBula(Bula bula) {
        this.bula = bula;
    }

    public List<Instrucao> getInstrucoes() {
        return instrucoes;
    }

    public void setInstrucoes(List<Instrucao> instrucoes) {
        this.instrucoes = instrucoes;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // NEW: Getter and Setter for doses
    public List<Dose> getDoses() {
        return doses;
    }

    public void setDoses(List<Dose> doses) {
        this.doses = doses;
    }
}
