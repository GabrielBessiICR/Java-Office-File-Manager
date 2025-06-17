package com.grupoicr.crm.domain.service.dto.document;

import lombok.Setter;
import lombok.Getter;


@Getter
public class DocumentConfig {
    private String documentType;
    private String type;
    private DocumentInfo document;
    private DocumentEditorConfig editorConfig;
    private DocumentPermissions permissions;
    @Setter
    private String token;

    public DocumentConfig(
            String documentType,
            String type,
            DocumentInfo document,
            DocumentEditorConfig editorConfig,
            DocumentPermissions permissions
    ) {
        this.documentType = documentType;
        this.type = type;
        this.document = document;
        this.editorConfig = editorConfig;
        this.permissions = permissions;
        this.token="";
    }


}
