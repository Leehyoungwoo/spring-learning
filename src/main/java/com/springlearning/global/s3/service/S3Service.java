package com.springlearning.global.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.springlearning.domain.file.entity.File;
import com.springlearning.domain.file.entity.type.FileType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 amazonS3;

    @Transactional
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
}
