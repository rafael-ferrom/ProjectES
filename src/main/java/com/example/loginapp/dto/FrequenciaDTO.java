package com.example.loginapp.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public class FrequenciaDTO {

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dataInicio;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dataTermino;

	private int vezesPorDia;

	public FrequenciaDTO() {
	}

	public FrequenciaDTO(LocalDate dataInicio, LocalDate dataTermino, int vezesPorDia) {
		this.dataInicio = dataInicio;
		this.dataTermino = dataTermino;
		this.vezesPorDia = vezesPorDia;
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

}
