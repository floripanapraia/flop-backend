package com.vitalu.flop.model.dto;

import com.vitalu.flop.model.enums.StatusDenuncia;

public class AnaliseDenunciaDTO {

	private StatusDenuncia novoStatus;

	public AnaliseDenunciaDTO() {
	}

	public AnaliseDenunciaDTO(StatusDenuncia novoStatus) {
		this.novoStatus = novoStatus;
	}

	public StatusDenuncia getNovoStatus() {
		return novoStatus;
	}

	public void setNovoStatus(StatusDenuncia novoStatus) {
		this.novoStatus = novoStatus;
	}
}