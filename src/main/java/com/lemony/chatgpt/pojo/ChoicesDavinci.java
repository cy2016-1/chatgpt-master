package com.lemony.chatgpt.pojo;

import lombok.Data;

@Data
public class ChoicesDavinci {
        private String text;
    private Integer index;
    private String logprobs;
    private String finish_reason;
}
