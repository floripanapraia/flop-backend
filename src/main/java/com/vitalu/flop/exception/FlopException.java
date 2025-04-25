package com.vitalu.flop.exception;

import org.springframework.http.HttpStatus;

public class FlopException extends Exception {
	public FlopException(String message, HttpStatus badRequest) {
		super(message);
	}
}