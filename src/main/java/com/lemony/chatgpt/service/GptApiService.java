package com.lemony.chatgpt.service;

import com.lemony.chatgpt.pojo.ChatRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.ServerException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public interface GptApiService {

    BigDecimal getBalance(String apiKey);

    BigDecimal getUsage(String apiKey);

    CompletableFuture<String> generateMessageAsync(ChatRequest request) throws ServerException, TimeoutException;

    void generateMessage(ChatRequest request,WebSocketServer webSocketServer) throws IOException;
}
