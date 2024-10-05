package com.example.quizmaster.controller;

import com.example.quizmaster.entity.File;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import com.example.quizmaster.payload.ApiResponse;
import com.example.quizmaster.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@Tag(name = "File Controller", description = "Manage file operations like uploading, retrieving, updating, and deleting files")
public class FileController {

    private final FileService fileService;

    @Operation(summary = "Upload a file", description = "Uploads a file (such as a video) to the server")
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse> uploadVideo(@Valid @RequestParam("file") MultipartFile file) {
        try {
            ApiResponse videoFile = fileService.saveFile(file);
            return ResponseEntity.status(videoFile.getCode()).body(videoFile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get file by ID", description = "Retrieves a file by its ID from the server")
    @GetMapping("/files/{id}")
    public ResponseEntity<Resource> getFile(@Valid @PathVariable Long id) {
        try {
            Resource resource = fileService.loadFileAsResource(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(Files.probeContentType(Paths.get(resource.getURI()))))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Update a file", description = "Updates an existing file by its ID")
    @PutMapping(value = "/update/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<File> updateFile(@Valid @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            File updatedFile = fileService.updateFile(id, file);
            return ResponseEntity.ok(updatedFile);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @Operation(summary = "Delete a file", description = "Deletes a file from the server by its ID")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteFile(@Valid @PathVariable Long id) {
        try {
            ApiResponse apiResponse = fileService.deleteFile(id);
            return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }
}
