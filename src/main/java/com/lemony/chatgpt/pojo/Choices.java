package com.lemony.chatgpt.pojo;



import lombok.Data;

@Data
public class Choices {
    private Integer index;
    private String finish_reason;
    private Message message;

}
