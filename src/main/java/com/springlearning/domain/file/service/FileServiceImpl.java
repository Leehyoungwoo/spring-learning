package com.springlearning.domain.file.service;

import com.springlearning.domain.file.dao.FileRepository;
import com.springlearning.domain.file.entity.File;
import com.springlearning.domain.file.entity.type.FileType;
import com.springlearning.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final S3Service s3Service;

    @Override
    @Transactional
    public void uploadFile(FileType fileType, MultipartFile file) throws IOException {
        File entity = s3Service.upload(fileType, file);
        fileRepository.save(entity);
    }
}
