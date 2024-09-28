package com.example.quizmaster.service;


import com.example.quizmaster.entity.File;
import com.example.quizmaster.payload.ApiResponse;
import com.example.quizmaster.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${file.upload-dir1}")
    private String uploadDir;



    private final FileRepository fileRepository;



    private static final Path root = Paths.get("");

    public ApiResponse saveFile(MultipartFile file) throws ChangeSetPersister.NotFoundException {
        String director = checkingAttachmentType(file);
        if (director == null) {
            return new ApiResponse("File yuklash uchun papka topilmadi", HttpStatus.NOT_FOUND);
        }

        Path directoryPath = root.resolve(director);
        Path filePath = directoryPath.resolve(file.getOriginalFilename());

        long fileId;
        try {
            // Create directories if they don't exist
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            // Copy file to the target location
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Save file details to the database
            File videoFile = new File();
            videoFile.setFileName(file.getOriginalFilename());
            videoFile.setFilepath(filePath.toString());
            File save = fileRepository.save(videoFile);
            fileId = save.getId();


        } catch (IOException e) {
            // Log the exception with a detailed message
            e.printStackTrace();
            throw new ChangeSetPersister.NotFoundException();
        }

        return new ApiResponse("Success", HttpStatus.OK, fileId);
    }

    public String checkingAttachmentType(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            return null;
        }

        return "file";
    }


    //    GetFile uchun
    public Resource loadFileAsResource(Long id) throws MalformedURLException {
        Optional<File> videoFileOptional = fileRepository.findById(id);
        if (videoFileOptional.isPresent()) {
            Path filePath = Paths.get(videoFileOptional.get().getFilepath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            }
        }
        return null;
    }


    //    update
    public File updateFile(Long id, MultipartFile file) throws IOException {
        Optional<File> existingVideoFile = fileRepository.findById(id);
        if (existingVideoFile.isPresent()) {
            File videoFile = existingVideoFile.get();
            Path oldFilePath = Paths.get(videoFile.getFilepath());
            Files.deleteIfExists(oldFilePath);

            String filename = file.getOriginalFilename();
            Path uploadPath = null;
            if (Objects.requireNonNull(filename).endsWith(".jpg") ||
                    filename.endsWith(".png") ||
                    filename.endsWith(".jpeg")) {
                uploadPath = Paths.get(uploadDir);
            }
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            videoFile.setFileName(filename);
            videoFile.setFilepath(filePath.toString());

            return fileRepository.save(videoFile);
        } else {
            throw new IOException("File not found");
        }
    }


    //delete file
    public ApiResponse deleteFile(Long id) throws IOException {
        Optional<File> existingVideoFile = fileRepository.findById(id);
        if (existingVideoFile.isPresent()) {
            File videoFile = existingVideoFile.get();
            Path filePath = Paths.get(videoFile.getFilepath());
            Files.deleteIfExists(filePath);
            fileRepository.delete(videoFile);
            return new ApiResponse("Successfully deleted",HttpStatus.OK);
        } else {
            throw new IOException("File not found");
        }
    }

}
