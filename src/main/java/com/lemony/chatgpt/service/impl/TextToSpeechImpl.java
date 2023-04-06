package com.lemony.chatgpt.service.impl;


import com.lemony.chatgpt.pojo.TextToSpeech;
import com.lemony.chatgpt.service.TextToSpeechService;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;


@Service
public class TextToSpeechImpl implements TextToSpeechService {
    @Value("${azure.SPEECH_KEY}")
    private String SPEECH_KEY;
    @Value("${azure.SERVICE_REGION}")
    private String SERVICE_REGION;

    @Override
    public File tts(TextToSpeech tts) throws FileNotFoundException {
        // Create a SpeechConfig object with your subscription key and service region
        SpeechConfig config = SpeechConfig.fromSubscription(SPEECH_KEY, SERVICE_REGION);
        config.setSpeechSynthesisVoiceName(tts.getLanguage());
        // 获取classpath路径
        String classpath = ResourceUtils.getURL("classpath:").getPath();
        // 构建生成语音的保存目录:static
        String outputFilePath = classpath + "static/" ;
        //创建语音文件
        File fileExits=new File(outputFilePath);
        if(!fileExits.exists()){
            fileExits.mkdirs();
        }
        String fileName= UUID.randomUUID().toString().substring(0,6)+".wav";
        //构建生成的语音文件对象
        File outputFile=new File(outputFilePath,fileName);
        AudioConfig audioConfig = AudioConfig.fromWavFileOutput(outputFile.getAbsolutePath());
        SpeechSynthesizer synthesizer = new SpeechSynthesizer(config,audioConfig);
        try {
            synthesizer.SpeakText(tts.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
          return outputFile;
    }

}
