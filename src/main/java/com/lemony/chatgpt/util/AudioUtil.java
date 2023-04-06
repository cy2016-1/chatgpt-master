package com.lemony.chatgpt.util;

import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;


public class AudioUtil {
    public static String convertAudioToWav( MultipartFile audioFile,String ffmpegPath) throws IOException, InterruptedException {
        String audioFileName="audio.wav";
//        String classpath = ResourceUtils.getURL("classpath:").getPath();
        // 构造原始音频文件保存目录 保存至电脑本地
        String inputFilePath = "/Users/lee/Desktop/audio/input";
        //构建格式化后音频文件保存目录
        String outputFilePath =  "/Users/lee/Desktop/audio/output";
        File inputExist=new File(inputFilePath);
        if(!inputExist.exists()){
            inputExist.mkdirs();
        }
        File outputExist=new File(outputFilePath);
        if(!outputExist.exists()){
            outputExist.mkdirs();
        }
        //创建原始音频文件对象
        File inputFile=new File(inputFilePath,audioFileName);
        //将前端传来的音频保存到本地
        audioFile.transferTo(inputFile);
        //创建格式化音频文件对象
        File outputFile=new File(outputFilePath,audioFileName);
        try {
            AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(inputFile);
            AudioFormat audioFormat = fileFormat.getFormat();
            // 判断wav音频文件是否满足16000hz、16bit的要求,如果不满足再格式化
            if (audioFormat.getSampleRate() == 16000 && audioFormat.getSampleSizeInBits() == 16) {
                outputFile=inputFile;
            }else{
                convertToValidWavFormat(inputFile.getAbsolutePath(),outputFile.getAbsolutePath(),ffmpegPath);
                //删除格式化之前的语音 避免音频堆积占用空间
                inputFile.delete();
            }
        } catch (IOException | InterruptedException | UnsupportedAudioFileException e) {
            //如果输入音频为mp3、m4a等 会抛出UnsupportedAudioFileException异常 ，则直接转换为wav格式
            if(e instanceof UnsupportedAudioFileException){
                convertToValidWavFormat(inputFile.getAbsolutePath(),outputFile.getAbsolutePath(),ffmpegPath);
                inputFile.delete();
            }else{
                e.printStackTrace();
            }
        }

        return outputFile.getAbsolutePath();
    }

    private static void convertToValidWavFormat(String inputPath, String outputPath,String ffmpegPath) throws IOException, InterruptedException {
        StringBuilder builder=new StringBuilder();
        //将音频格式化为采样率16000hz深度为16的wav格式
        builder.append(ffmpegPath).append(" -i").append(" ").append(inputPath).append(" ").append("-vn -ar 16000 -ac 1 -b:a 16k").append(" ").append(outputPath);
        String command = builder.toString();
        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println("文件转换成功！");

        } else {
            System.out.println("文件转换失败！");
        }
    }
}

