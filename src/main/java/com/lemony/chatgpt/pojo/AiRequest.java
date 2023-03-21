package com.lemony.chatgpt.pojo;

import lombok.Data;

@Data
public class AiRequest {
    /**
     * 问题
     */
    private String role;
    private String content;

}
