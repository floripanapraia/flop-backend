package com.vitalu.flop.model.dto;

import lombok.Builder;

@Builder
public record MailBody(String to, String subject, String text) {

}
