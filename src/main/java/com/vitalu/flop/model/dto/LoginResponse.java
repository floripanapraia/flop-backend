package com.vitalu.flop.model.dto;

public record LoginResponse(
	    boolean requiresTwoFactor,
	    String message,
	    String token // null se requiresTwoFactor = true
	) {}