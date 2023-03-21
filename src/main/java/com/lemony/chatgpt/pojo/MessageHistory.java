package com.lemony.chatgpt.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class MessageHistory {
    private String message;
    private String sender;
    private Date time;
}
