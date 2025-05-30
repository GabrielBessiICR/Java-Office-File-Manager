package com.grupoicr.crm.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class OnlyOfficeController {
    private static final String SECRET = "your-secret-here";
    private static final String FILES_DIR = "files";

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig(
            @RequestParam(required = false) String fileName
    ) throws Exception {

        if (fileName == null || fileName.isEmpty()) {
            fileName = UUID.randomUUID() + ".docx";
            File template = new File(FILES_DIR, "template.docx");
            File newFile = new File(FILES_DIR, fileName);
            FileUtils.copyFile(template, newFile);
        }

        File file = new File(FILES_DIR, fileName);
        if (!file.exists()) return ResponseEntity.notFound().build();

        Map<String, Object> document = new HashMap<>();
        document.put("fileType", "docx");
        document.put("key", UUID.randomUUID().toString());
        document.put("title", fileName);
        document.put("url", "http://localhost:8080/files/" + fileName);

        Map<String, Object> user = new HashMap<>();
        user.put("id", "1");
        user.put("name", "Gabriel");

        Map<String, Object> editorConfig = new HashMap<>();
        editorConfig.put("callbackUrl", "http://localhost:8080/api/callback?fileName=" + fileName);
        editorConfig.put("user", user);

        Map<String, Object> config = new HashMap<>();
        config.put("documentType", "word");
        config.put("type", "desktop");
        config.put("document", document);
        config.put("editorConfig", editorConfig);

        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        String token = JWT.create()
                .withPayload(config)
                .sign(algorithm);

        config.put("token", token);

        return ResponseEntity.ok(config);
    }


    @PostMapping("/callback")
    public Map<String, Object> handleCallback(
            @RequestParam String fileName,
            @RequestBody JsonNode body
    ) {
        int status = body.get("status").asInt();
        String url = body.get("url").asText();

        try {
            if (status == 2 || status == 6) {
                // Save to final location
                InputStream input = new URL(url).openStream();
                FileOutputStream output = new FileOutputStream(new File(FILES_DIR, fileName));
                IOUtils.copy(input, output);
                input.close();
                output.close();
                System.out.println("Saved: " + fileName);
            }
        } catch (IOException e) {
            return Map.of("error", 1, "message", e.getMessage());
}

        return Map.of("error", 0);
    }

    @PostMapping("/test-callback")
    public Map<String, Object> testCallback(@RequestBody JsonNode body) {
        System.out.println("Simulated callback received:");
        System.out.println(body.toPrettyString());
        return Map.of("error", 0);
    }

}
