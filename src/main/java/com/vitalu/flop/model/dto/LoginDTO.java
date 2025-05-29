package com.vitalu.flop.model.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String email;
    private String senha;
    private Integer codigo2fa;
}
