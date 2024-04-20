package com.springlearning.domain.file.controller;

import com.springlearning.domain.file.entity.type.FileType;
import com.springlearning.domain.file.service.FileService;
import com.springlearning.global.security.dto.UserFormLoginDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.util.UUID;

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

    @GetMapping("/{fileId}")
    public void downloadFile(@PathVariable Long fileId, HttpServletResponse response) throws Exception {
        Object[] object = fileService.getObject(fileId);
        byte[] fileContent = (byte[]) object[0];

        String fileName = UUID.randomUUID().toString() + "_Spring-learning";
        response.setContentLength(fileContent.length);
        response.getOutputStream().write(fileContent);
        response.getOutputStream().flush();
    }
}
