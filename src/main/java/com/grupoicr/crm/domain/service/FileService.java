package com.grupoicr.crm.domain.service;


import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileService {

    public final Path fileStorageLocation = Paths.get("files").toAbsolutePath().normalize();

    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found " + filename, ex);
        }
    }

    public Optional<Path> getFileIfExists(String fileName) {
        Path filePath = fileStorageLocation.resolve(fileName);
        return Files.exists(filePath) ? Optional.of(filePath) : Optional.empty();
    }

    public Optional<Path> copyFileIfFirstExists(String fileName, String newFileName) throws IOException {
        return getFileIfExists(fileName)
                .map(filePath -> {
                    try {
                        Path newFilePath = fileStorageLocation.resolve(newFileName);
                        Files.copy(filePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);
                        return Optional.of(newFilePath);
                    } catch (IOException e) {
                        throw new RuntimeException("Error copying file: " + fileName + " to " + newFileName, e);
                    }
                }).orElseThrow(() -> new RuntimeException("File not found: " + fileName));
    }

    public Optional<Path> createFileWithRandomUUIDWithTemplate(String template) throws IOException {
        String fileName = UUID.randomUUID() + ".docx";
        return copyFileIfFirstExists(template, fileName);
    }

}
