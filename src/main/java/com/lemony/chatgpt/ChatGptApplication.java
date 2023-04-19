package com.lemony.chatgpt;


import com.lemony.chatgpt.util.SpringContextHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication

public class ChatGptApplication {
    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }
    public static void main(String[] args) {
       SpringApplication.run(ChatGptApplication.class,args);
    }
}
