package com.example.movie_backend.controller;

import com.example.movie_backend.dto.response.ApiResponse;
import com.example.movie_backend.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Media Controller", description = "Xử lý upload tệp tin (Images/Media)")
public class MediaController {
    FileStorageService fileStorageService;

    @Operation(summary = "Upload hình ảnh (Poster, Avatar, Banner...)")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {
        String fileUrl = fileStorageService.storeFile(file, folder);
        return ApiResponse.<String>builder()
                .result(fileUrl)
                .build();
    }
}
