package com.lemony.chatgpt.pojo;

import lombok.Data;

import java.util.List;

@Data
public class Response {
    private String id;
    private String object;
    private String model;
    private Integer created;
    private List<ChoicesDavinci> choices;

}
