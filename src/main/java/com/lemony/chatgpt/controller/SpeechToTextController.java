package com.lemony.chatgpt.controller;

import com.lemony.chatgpt.util.AudioUtil;
import com.lemony.chatgpt.util.R;
import com.lemony.chatgpt.service.impl.SpeachToTextServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
public class SpeechToTextController {
    @Autowired
    SpeachToTextServiceImpl speachToTextService;
    @Value("${ffmpeg.path}")
    String ffmpegPath;
    //语音输入
    @PostMapping("/voice")
    public R voiceToText(@RequestParam("audio") MultipartFile file,@RequestParam("lang") String lang) throws Exception {
        //音频格式化为wav
        String audioPath=AudioUtil.convertAudioToWav(file,ffmpegPath);
        //调用微软Azure语音转文字服务
        String text=speachToTextService.speechToText(audioPath,lang);
        // 删掉格式化后的音频文件 避免音频堆积占用空间
        File outputFile=new File(audioPath);
        outputFile.delete();
        return R.ok().put("text", text);
    }

}
