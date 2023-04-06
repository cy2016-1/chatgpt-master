package com.lemony.chatgpt.controller;

import com.lemony.chatgpt.pojo.BalanceRequest;
import com.lemony.chatgpt.util.R;
import com.lemony.chatgpt.pojo.ChatRequest;
import com.lemony.chatgpt.service.GptApiService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeoutException;

@RestController
public class GptController {
    @Value("${openai.apiKey}")
    private String api_key;
    @Autowired
    GptApiService gptApiService;
    //gpt3.5-turbo 联系上下文
    @PostMapping("/chat")
    public R getChat4(@RequestBody ChatRequest request) throws IOException, TimeoutException {
        String text = gptApiService.generateMessage(request);
        System.out.println(text);
        return R.ok().put("data", text);
    }
    //查询余额
    @PostMapping("/getBalance")
    public R getBilling2(@RequestBody BalanceRequest request){
        String apiKey=request.getApiKey();
        //如果前端不传apiKey  默认使用配置文件的
        if(StringUtils.isBlank(apiKey)){
          apiKey=api_key;
        }
        //查询总额
        BigDecimal total=gptApiService.getBalance(apiKey).setScale(2, RoundingMode.HALF_UP);
        //查询用量
        BigDecimal total_usage=gptApiService.getUsage(apiKey).divide(new BigDecimal("100"),2, RoundingMode.HALF_UP);
        //余额
        BigDecimal balance=total.subtract(total_usage).setScale(2, RoundingMode.HALF_UP);
        return R.ok().put("total",total.toString()).put("usage",total_usage.toString()).put("balance",balance.toString());
    }

}
