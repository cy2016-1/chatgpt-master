package com.lemony.chatgpt.service;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.lemony.chatgpt.pojo.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import javax.annotation.Resource;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Service
public class GptApiServiceImpl implements GptApiService {
    @Value("${openai.apiKey}")
    private String apiKey;
    @Value("${openai.model}")
    private String model;
    @Value("${openai.api_endPoint}")
    private String api_endPoint;
    @Value("${openai.temperature}")
    private Double temperature;
    @Resource
    private RestTemplate restTemplate;

    //使用的模型是gpt-3.5-turbo
    @Override
    public String generateMessage(ChatRequest request) throws ServerException, TimeoutException {
        //聊天记录处理
        List<Map<String, String>> messages = dealRequest(request);
        // 构造请求体
        Map<String, Object> params = MapUtil.ofEntries(
//                MapUtil.entry("stream", true),
//                MapUtil.entry("max_tokens", 2048),
                MapUtil.entry("model", model),
                MapUtil.entry("temperature", temperature),
                MapUtil.entry("messages", messages)
        );
        String requestBodyJson = JSONUtil.toJsonStr(params);
        System.out.println("请求体:"+requestBodyJson);
    // 构造请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyJson, headers);
        // 发送请求
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(api_endPoint, HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        String message=getGPT3Answer(responseEntity);
        return message;
    }

    //gpt-3.5-turbo
    private String getGPT3Answer(ResponseEntity<String> responseEntity) throws ServerException, TimeoutException {
        String responseBody = responseEntity.getBody();
        Answer answer= JSONObject.parseObject(responseBody,Answer.class);
        StringBuilder stringBuilder= new StringBuilder();
        List<Choices> choices = answer.getChoices();
        choices.forEach(choice -> {
            stringBuilder.append(UnicodeUtil.toString(choice.getMessage().getContent()));
        });
        StringBuilder s = new StringBuilder(stringBuilder.toString());
        if(s.toString().contains("\\n\\n")){
            return s.substring(4,s.length());
        }
        return s.toString();
    }

    private  List<Map<String, String>> dealRequest(ChatRequest request){
        List<Map<String, String>> messages = new ArrayList<>();
        List<MessageHistory> histories=request.getMessageHistory();
        //是否开启上下文对话
        if(request.getIsContextChat()){
            for (MessageHistory messageHistory : histories) {
                String role = messageHistory.getSender();
                String content = messageHistory.getMessage();
                Map<String, String> userMessage = MapUtil.ofEntries(
                        MapUtil.entry("role", role),
                        MapUtil.entry("content", content)
                );
                messages.add(userMessage);
            }
        }else{
            MessageHistory messageHistory=histories.get(histories.size()-1);
            String role = messageHistory.getSender();
            String content = messageHistory.getMessage();
            Map<String, String> userMessage = MapUtil.ofEntries(
                    MapUtil.entry("role", role),
                    MapUtil.entry("content", content)
            );
            messages.add(userMessage);
        }
        return messages;
    }
}