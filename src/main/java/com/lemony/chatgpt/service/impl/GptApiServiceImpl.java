package com.lemony.chatgpt.service.impl;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.lemony.chatgpt.config.OpenAIConfig;
import com.lemony.chatgpt.pojo.*;
import com.lemony.chatgpt.service.GptApiService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.rmi.ServerException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

@Service
public class GptApiServiceImpl implements GptApiService {
    @Resource
    private RestTemplate restTemplate;
    @Autowired
    private  OpenAIConfig openAIConfig;
    @Autowired
    ThreadPoolExecutor executor;

    //查询余额
    @Override
    public BigDecimal getBalance(String apiKey) {
        // 构造请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        // 发送请求
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(openAIConfig.getSubscription_url(), HttpMethod.GET, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        JSONObject response=JSONObject.parseObject(responseEntity.getBody());
        //总金额
        BigDecimal total= (BigDecimal) response.get("hard_limit_usd");
        return total;
    }

    //查询用量
    @Override
    public BigDecimal getUsage(String apiKey) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, 1);
        Date end = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String endDate = dateFormat.format(end);
        StringBuilder builder=new StringBuilder();
        //构建请求 https://api.openai.com/v1/dashboard/billing/usage?start_date=2023-01-01&end_date=2023-04-02 start_time时间尽量提前
        builder.append(openAIConfig.getBilling_url()).append("?").append("start_date=2023-01-01").append("&end_date=").append(endDate);
        String url=builder.toString();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        JSONObject response=JSONObject.parseObject(responseEntity.getBody());
        //总用量
        BigDecimal total_usage= (BigDecimal) response.get("total_usage");

        return total_usage;
    }
    //异步请求
    @Override
    public CompletableFuture<String> generateMessageAsync(ChatRequest request) throws ServerException, TimeoutException{
        CompletableFuture<String> future=CompletableFuture.supplyAsync(()->{
            String apiKey=request.getApiKey();
            String model=openAIConfig.getModel();
            Integer max_tokens=request.getMaxTokens();
            //如果前端不设置apiKey则默认使用配置文件的
            if(StringUtils.isBlank(apiKey)){
                apiKey=openAIConfig.getApiKey();
            }
            //如果前端不设置max_tokens 则默认使用配置文件的
            if(max_tokens==null){
                max_tokens=openAIConfig.getMaxTokens();
            }

            //聊天记录处理
            List<Map<String, String>> messages = dealRequest(request,max_tokens);
            // 构造请求体
            Map<String, Object> params = MapUtil.ofEntries(
//                MapUtil.entry("stream", true),
                    MapUtil.entry("max_tokens", max_tokens),
                    MapUtil.entry("model", model),
                    MapUtil.entry("temperature", openAIConfig.getTemperature()),
                    MapUtil.entry("messages", messages)
            );
            String requestBodyJson = JSONUtil.toJsonStr(params);
            System.out.println("请求体:"+requestBodyJson);
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyJson, headers);
            ResponseEntity<String> responseEntity = null;
            try {
                responseEntity = restTemplate.exchange(openAIConfig.getApi_endPoint(), HttpMethod.POST, requestEntity, String.class);
            } catch (RestClientException e) {
                e.printStackTrace();
            }
            String message= null;

            try {
                message = getGPT3Answer(responseEntity);
            } catch (ServerException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }

            return message;

        },executor);
        return future;
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
        System.out.println(s.toString());
        if(s.toString().contains("\\n\\n")){
            return s.substring(4,s.length());
        }
        return s.toString();
    }

    private  List<Map<String, String>> dealRequest(ChatRequest request,Integer max_tokens){
        List<Map<String, String>> messages = new ArrayList<>();
        List<MessageHistory> histories=request.getMessageHistory();
        StringBuilder builder = new StringBuilder();
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
                // 拼接所有的message
                builder.append(content).append(role);
                // 如果字符数超过max_tokens，删除最早的message
                while (calculateLength(builder.toString()) > max_tokens) {
                    messages.remove(0);
                    String firstMessageContent = messages.get(0).toString();
                    builder.delete(0, firstMessageContent.length());
                    System.out.println("字符数超过max_tokens，删除最早的message");
                }
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

    /**
     * 计算字符串的长度，一个中文字符计算为两个token，4个英文字符为一个token
     */
    private int calculateLength(String str) {
        int length = 0;
        int cnLength=0;
        int enLength=0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c >= 0x4E00 && c <= 0x9FA5) {
                cnLength += 2;
            } else {
                enLength += 1;
            }
        }
        length=cnLength+enLength/4;
        return length;
    }
}