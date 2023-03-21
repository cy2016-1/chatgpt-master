package com.lemony.chatgpt.service;

import com.lemony.chatgpt.pojo.ChatRequest;

import java.rmi.ServerException;
import java.util.concurrent.TimeoutException;

public interface GptApiService {

    String generateMessage(ChatRequest request) throws ServerException, TimeoutException;
}
