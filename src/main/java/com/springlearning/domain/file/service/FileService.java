package com.springlearning.domain.file.service;

import com.springlearning.domain.file.entity.type.FileType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    void uploadFile(FileType fileType, MultipartFile file) throws IOException;
}
