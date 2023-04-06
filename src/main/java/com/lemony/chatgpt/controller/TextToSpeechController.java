package com.lemony.chatgpt.controller;

import com.lemony.chatgpt.pojo.TextToSpeech;
import com.lemony.chatgpt.service.TextToSpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
public class TextToSpeechController {
    @Autowired
    TextToSpeechService textToSpeechService;
    @PostMapping("/tts")
    public ResponseEntity<byte[]> textToSpeech(@RequestBody TextToSpeech tts) throws IOException {
        File file=textToSpeechService.tts(tts);
        byte[] audioBytes = Files.readAllBytes(file.toPath());
        //删除本地录音文件,避免堆积
        file.delete();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/wav"));
        headers.setContentDispositionFormData("attachment", "file.wav");
        return new ResponseEntity<>(audioBytes, headers, HttpStatus.OK);
    }
}
