package com.lemony.chatgpt.pojo;

import lombok.Data;

import java.util.List;

@Data
public class ChatRequest {
    private List<MessageHistory> messageHistory;
    Boolean isContextChat;
    private String apiKey;
    private Integer maxTokens;
}
