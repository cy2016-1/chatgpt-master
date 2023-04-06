package com.lemony.chatgpt.service.impl;

import com.lemony.chatgpt.service.SpeachToTextService;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.Future;

@Service
public class SpeachToTextServiceImpl implements SpeachToTextService {
    @Value("${azure.SPEECH_KEY}")
    private String SPEECH_KEY;
    @Value("${azure.SERVICE_REGION}")
    private String SERVICE_REGION;

    @Override
    public String speechToText(String audioPath,String lang) throws Exception {
        File audio=new File(audioPath);
        try {
            SpeechConfig speechConfig = SpeechConfig.fromSubscription(SPEECH_KEY, SERVICE_REGION);
            //获取格式化后的音频文件
            AudioConfig audioConfig = AudioConfig.fromWavFileInput(audioPath);
            // 设置语言
            speechConfig.setSpeechRecognitionLanguage(lang.substring(0,5));
            SpeechRecognizer speechRecognizer = new SpeechRecognizer(speechConfig, audioConfig);
            Future<SpeechRecognitionResult> task = speechRecognizer.recognizeOnceAsync();
            SpeechRecognitionResult result = task.get();
            // 获取转换结果
            if (result.getReason() == ResultReason.RecognizedSpeech) {
                return result.getText();
            } else {
                throw new Exception("Speech recognition failed: " + result.getReason().toString());
            }
        } catch (Exception ex) {
            //不管是否成功都要删掉格式化后的音频文件
            audio.delete();
            System.out.println(ex.getMessage());
            throw ex;
        }
    }
}
