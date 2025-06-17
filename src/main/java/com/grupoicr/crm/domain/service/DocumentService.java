package com.grupoicr.crm.domain.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.grupoicr.crm.domain.service.dto.document.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DocumentService {
    @Value("${api.security.token.secret}")
    private String SECRET;
    private static final String BASE_URL = "http://host.docker.internal:8080/files/";
    private static final String CALLBACK_URL = "http://host.docker.internal:8080/api/callback?fileName=";

    public DocumentConfig generateDocumentConfig(String fileName) throws JsonProcessingException {
        DocumentConfig config = new DocumentConfig(
                "word",
                "desktop",
                createDocumentInfo(fileName),
                createEditorConfig(fileName),
                createPermissions()
        );

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(config);
        config.setToken(generateToken(jsonString));

        return config;
    }

    private DocumentInfo createDocumentInfo(String fileName) {
        return new DocumentInfo("docx",UUID.randomUUID().toString(),fileName,BASE_URL + fileName);
    }

    private DocumentEditorConfig createEditorConfig(String fileName) {
        return new DocumentEditorConfig(
                CALLBACK_URL + fileName,
                createUserInfo("1","Gabriel"),
                createCustomization(),
                "edit");
    }

    private DocumentEditorConfig createEditorConfig(String fileName,String mode) {
        return new DocumentEditorConfig(
                CALLBACK_URL + fileName,
                createUserInfo("1","Gabriel"),
                createCustomization(),
                mode);
    }

    private DocumentCustomization createCustomization() {
        return new DocumentCustomization();
    }

    private DocumentUserInfo createUserInfo(String id,String name) {
        return new DocumentUserInfo(id,name);
    }

    private DocumentPermissions createPermissions() {
        return new DocumentPermissions();
    }

    private DocumentPermissions createPermissions(boolean edit, boolean download, boolean print, boolean copy) {
        return new DocumentPermissions(edit, download, print, copy);
    }

    private String generateToken(Map<String, Object> config) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        return JWT.create().withPayload(config).sign(algorithm);
    }

    private String generateToken(String config) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        return JWT.create().withPayload(config).sign(algorithm);
    }

    public String getSECRET() {
        return SECRET;
    }
}
