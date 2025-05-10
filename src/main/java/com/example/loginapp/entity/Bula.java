package com.example.loginapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
}
