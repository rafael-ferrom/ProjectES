package com.example.loginapp.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "bulas")
public class Bula {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nomeComercial;
	private String principioAtivo;
	private String concentracao;
	private String formaFarmaceutica;
	private String apresentacao;
	private String fabricante;

	// NEW: Added a list for default instructions
	@ElementCollection
	@CollectionTable(name = "bula_instrucoes", joinColumns = @JoinColumn(name = "bula_id"))
	@Column(name = "instrucao")
	private List<String> instrucoes = new ArrayList<>();

	public Bula() {
	}

	public Bula(String nomeComercial, String principioAtivo, String concentracao, String formaFarmaceutica,
			String apresentacao, String fabricante) {
		this.nomeComercial = nomeComercial;
		this.principioAtivo = principioAtivo;
		this.concentracao = concentracao;
		this.formaFarmaceutica = formaFarmaceutica;
		this.apresentacao = apresentacao;
		this.fabricante = fabricante;
	}

	// NEW: Method to add instructions easily
	public void adicionarInstrucao(String instrucao) {
		this.instrucoes.add(instrucao);
	}

	// Getters and Setters for all fields, including the new 'instrucoes' list

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
}
