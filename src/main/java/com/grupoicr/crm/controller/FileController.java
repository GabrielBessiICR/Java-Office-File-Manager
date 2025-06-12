package com.grupoicr.crm.controller;

import com.grupoicr.crm.domain.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@Tag(name = "File Management", description = "API for managing file uploads and downloads.")
public class FileController {
    @Autowired
    private FileService fileService;

    @Operation(summary = "Download a file by its filename",
            description = "Retrieves a file from the server based on its given filename and initiates a download.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully downloaded the file",
                    content = @Content(mediaType = "application/octet-stream",
                            schema = @Schema(type = "string", format = "binary"))), // Describes binary content
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "The exact filename of the file to download, including its extension. E.g., 'document.docx'",
                    example = "document.docx")
            @PathVariable String filename, HttpServletRequest request
    ) {
        Resource resource = fileService.loadFileAsResource(filename);

        if (resource == null || !resource.exists()) {
            // might want to implement a custom exception or a more robust error handling for this.
            return ResponseEntity.notFound().build();
        }

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            System.err.println("Could not determine file type for " + filename + ": " + ex.getMessage());
        }

        // Fallback to a generic
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
