package com.lemony.chatgpt.pojo;

import lombok.Data;

@Data
public class AudioFile {
    private String fileName;
    private byte[] fileData;
}
