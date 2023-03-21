package com.lemony.chatgpt.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemony.chatgpt.openAiUtil.R;
import com.lemony.chatgpt.pojo.AiRequest;
import com.lemony.chatgpt.pojo.ChatRequest;
import com.lemony.chatgpt.pojo.MessageHistory;
import com.lemony.chatgpt.service.GptApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@RestController
public class GptController {
    @Autowired
    GptApiService gptApiService;
    //gpt3.5-turbo 联系上下文
    @PostMapping("/chat")
    public R getChat4(@RequestBody ChatRequest request) throws IOException, TimeoutException {
        String text = gptApiService.generateMessage(request);
        System.out.println(text);
        return R.ok().put("data", text);
    }

}
