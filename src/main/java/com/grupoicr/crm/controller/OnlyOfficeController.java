package com.grupoicr.crm.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.grupoicr.crm.domain.service.DocumentService;
import com.grupoicr.crm.domain.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class OnlyOfficeController {
    // Inject from environment variable
    private static final String SECRET = "your-secret-here";
    private static final String FILES_DIR = "files/";
    private static File FILESPATH = new File(FILES_DIR);

    private FileService fileService;
    private DocumentService documentService;

    public static void main(){
        File file = new File(FILESPATH , "text.docx");
        System.out.println(FILESPATH);
        System.out.println(file);

    }

    @Autowired
    public OnlyOfficeController(FileService fileService, DocumentService documentService) {
        this.fileService = fileService;
        this.documentService = documentService;
    }

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig(
            @RequestParam(required = false) String fileName,
            @RequestParam(required = false) String templateFileName
    ) throws Exception {

        String templateFileNameWithDir ;
        if (templateFileName == null || templateFileName.isEmpty()) {
            templateFileNameWithDir = "template/template.docx";
        }else{
            templateFileNameWithDir = "template/"+templateFileName;
        }

        Optional<Path> filePath;
        if (fileName == null || fileName.isEmpty()) {
            filePath = fileService.createFileWithRandomUUIDWithTemplate(templateFileNameWithDir);
        } else {
            filePath = fileService.getFileIfExists(fileName);
            if (filePath.isEmpty()) {
                filePath = fileService.copyFileIfFirstExists(templateFileNameWithDir, "editando/"+fileName);
            }else{
                filePath = fileService.copyFileIfFirstExists(filePath.get().getFileName().toString(), "editando/"+fileName);
            }
        }

        if (filePath.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return documentService.generateDocumentConfig(filePath.get().getFileName().toString());
    }


    @PostMapping("/callback")
    public Map<String, Object> handleCallback(
            @RequestParam(required = false) String fileName,
            @RequestBody(required = false) JsonNode body,
            HttpServletRequest request
    ) {
        System.out.println("\n--- ONLYOFFICE CALLBACK RECEIVED ---");
        System.out.println("Timestamp: " + new java.util.Date());
        System.out.println("Request Method: " + request.getMethod());
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Query String: " + request.getQueryString());

        System.out.println("Request Headers:");
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            System.out.println("  " + headerName + ": " + request.getHeader(headerName));
        });

        System.out.println("Request Body:");
        try {
            System.out.println(body.toPrettyString());
        } catch (Exception e) {
            System.err.println("Error parsing request body for logging: " + e.getMessage());
        }
        System.out.println("--- END ONLYOFFICE CALLBACK INFO ---\n");

        int status = body.get("status").asInt();
        // ONLY GET 'url' WHEN STATUS INDICATES A FILE SAVE
        // Status 2: Document is saved
        // Status 3: Document is being edited, no saving is required (e.g., autosave with no changes)
        // Status 6: Document is being edited, document is closed without saving (e.g., force save on close)
        if (status == 2 || status == 6) {
            String url = body.get("url").asText(); // Now it should be safe to get the URL
            try {
                System.out.println("Callback status is " + status + ". Attempting to save file from URL: " + url);
                InputStream input = new URL(url).openStream();
                FileOutputStream output = new FileOutputStream(new File(FILESPATH, fileName));
                IOUtils.copy(input, output);
                input.close();
                output.close();
                System.out.println("Successfully saved: " + fileName);
            } catch (IOException e) {
                System.err.println("Error processing ONLYOFFICE callback for " + fileName + ": " + e.getMessage());
                e.printStackTrace();
                return Map.of("error", 1, "message", e.getMessage());
            }
        } else {
            System.out.println("Callback status is " + status + ". No file saving action needed.");
            // You can add more specific logging for other statuses if desired
            // For example:
            // if (status == 1) { console.log("Document editing started/in progress."); }
            // if (status == 3) { console.log("Document being edited, no changes to save."); }
            // if (status == 4) { console.error("Error during saving process on Document Server side."); }
            // if (status == 7) { console.error("Document editing error."); }
        }

        return Map.of("error", 0);
    }



}
