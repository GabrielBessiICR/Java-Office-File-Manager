package com.grupoicr.crm.domain.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class DocumentService {
    // Inject from environment variable
    @Value("${JWT_SECRET}")
    private static String SECRET;
    private static final String BASE_URL = "http://host.docker.internal:8080/files/";
    private static final String CALLBACK_URL = "http://host.docker.internal:8080/api/callback?fileName=";

    public ResponseEntity<Map<String, Object>> generateDocumentConfig(String fileName) {
        Map<String, Object> config = new HashMap<>();
        config.put("documentType", "word");
        config.put("type", "desktop");
        config.put("document", createDocumentInfo(fileName));
        config.put("editorConfig", createEditorConfig(fileName));
        config.put("permissions", createPermissions());

        String token = generateToken(config);
        config.put("token", token);

        return ResponseEntity.ok(config);
    }

    private Map<String, Object> createDocumentInfo(String fileName) {
        Map<String, Object> document = new HashMap<>();
        document.put("fileType", "docx");
        document.put("key", UUID.randomUUID().toString());
        document.put("title", fileName);
        document.put("url", BASE_URL + fileName);
        return document;
    }

    private Map<String, Object> createEditorConfig(String fileName) {
        Map<String, Object> editorConfig = new HashMap<>();
        editorConfig.put("callbackUrl", CALLBACK_URL + fileName);
        editorConfig.put("user", createUserInfo("1","Gabriel"));
        editorConfig.put("customization", createCustomization());
        editorConfig.put("mode", "edit");
        editorConfig.put("lang", "pt-BR");
        editorConfig.put("location", "br");
        editorConfig.put("region", "pt-BR");

        return editorConfig;
    }

    private Map<String, Object> createCustomization() {
        Map<String, Object> customization = new HashMap<>();
        customization.put("autosave", false);
        customization.put("forcesave", true);
        return customization;
    }

    private Map<String, Object> createUserInfo(String id,String name) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", id);
        user.put("name", name);
        return user;
    }

    private Map<String, Object> createPermissions() {
        Map<String, Object> permissions = new HashMap<>();
        permissions.put("edit", true);
        permissions.put("download", true);
        permissions.put("print", true);
        permissions.put("copy", true);
        return permissions;
    }

    private String generateToken(Map<String, Object> config) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        return JWT.create().withPayload(config).sign(algorithm);
    }
}
