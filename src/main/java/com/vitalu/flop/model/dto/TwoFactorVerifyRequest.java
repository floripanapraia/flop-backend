package com.vitalu.flop.model.dto;

public record TwoFactorVerifyRequest(
	    String email,
	    Integer otp
	) {}
