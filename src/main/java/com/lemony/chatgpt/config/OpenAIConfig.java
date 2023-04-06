package com.lemony.chatgpt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties(prefix = "openai")
public class OpenAIConfig {


    private String apiKey;
    private Integer maxTokens;
    private Double temperature;
    private String api_endPoint;
    private String model;
    //查询订阅状况
    private String subscription_url;
    //查询用量状况
    private String billing_url;

}
