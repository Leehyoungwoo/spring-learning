package com.springlearning.global.s3.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.springlearning.domain.file.entity.File;
import com.springlearning.domain.file.entity.type.FileType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Util {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 amazonS3;

    public File upload(FileType fileType, MultipartFile multipartFile) throws IOException {
        String originFileName = multipartFile.getOriginalFilename();
        String folderKey = fileType.toString().toLowerCase() + "/";
        UUID randomId = UUID.randomUUID();
        String fileName = randomId + "_" + originFileName;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        if (!amazonS3.doesObjectExist(bucketName, folderKey)) {
            amazonS3.putObject(bucketName, folderKey, "");
        }

        String objectKey = folderKey + fileName;
        amazonS3.putObject(new PutObjectRequest(bucketName, objectKey, multipartFile.getInputStream(), objectMetadata));
        String fileUrl = amazonS3.getUrl(bucketName, objectKey).toString();

        return File.builder()
                .name(fileName)
                .fileType(fileType)
                .originalName(originFileName)
                .url(fileUrl)
                .dir(folderKey)
                .build();
    }

    public void remove(File file) {
        final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, file.getUrl());
        amazonS3.deleteObject(deleteObjectRequest);
    }

    public byte[] getFile(File file) throws Exception {
        String fileUrl = file.getUrl();
        String objectKey = fileUrl.substring(fileUrl.indexOf(".com/") + 5);

        String decodedKey = URLDecoder.decode(objectKey, StandardCharsets.UTF_8.name());

        S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucketName, decodedKey));

        return s3Object.getObjectContent().readAllBytes();

    }
}
