package com.lemony.chatgpt.service;

import com.lemony.chatgpt.pojo.TextToSpeech;

import java.io.File;
import java.io.FileNotFoundException;


public interface TextToSpeechService {
    File tts(TextToSpeech tts) throws FileNotFoundException;
}
