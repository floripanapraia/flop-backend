package com.vitalu.flop.exception;

import org.springframework.http.HttpStatus;

public class FlopException extends Exception {
	
	private HttpStatus status;
	
	public FlopException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}
}