package com.lemony.chatgpt.pojo;



import lombok.Data;

@Data
public class Choices {
//    private String text;
//    private Integer index;
//    private String logprobs;
//    private String finish_reason;
    private Integer index;
    private String finish_reason;
    private Message message;

}
