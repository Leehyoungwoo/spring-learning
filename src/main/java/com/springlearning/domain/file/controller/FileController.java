package com.springlearning.domain.file.controller;

import com.springlearning.domain.file.entity.type.FileType;
import com.springlearning.domain.file.service.FileService;
import com.springlearning.global.security.dto.UserFormLoginDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping
    public void uploadFile(@RequestParam FileType fileType,
                           @RequestParam MultipartFile file) throws IOException {
        fileService.uploadFile(fileType, file);
    }
}
