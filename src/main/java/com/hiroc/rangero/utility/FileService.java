package com.hiroc.rangero.utility;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileService {

    private final S3Client s3Client;
    @Value("${aws.bucketName}")
    private String bucketName;
    private final long MAX_MB = 8;
    private final long MAX_FILE_SIZE = MAX_MB * 1000 * 1000;

    //This is the start
    public String uploadFile(MultipartFile file,long taskId) throws IOException{
        //TODO - add validation for file size
        if (file.getSize()>MAX_FILE_SIZE){
            throw new BadRequestException("File size must be less than "+MAX_MB+"mb.");
        }
        String key = "task-"+taskId+"/"+ System.currentTimeMillis() + "_" + file.getOriginalFilename();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(),file.getSize());
        s3Client.putObject(putObjectRequest,requestBody);
        return key;
    }

//    public byte[] downloadFile(String fileName) throws IOException{
//
//    }
//
//    public void deleteFile(String fileName){
//
//    }
}
