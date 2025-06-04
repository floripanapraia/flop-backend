package com.vitalu.flop.ia;

import java.util.List;

import lombok.Data;

@Data
public class OpenAIResponse {
    private List<Choice> choices;

    public String getFirstMessage() {
        return choices.get(0).message.content;
    }

    public static class Choice {
        public Message message;
    }

    public static class Message {
        public String role;
        public String content;
    }

}
