package com.grupoicr.crm.domain.service.dto.document;

import lombok.Getter;

@Getter
public class DocumentEditorConfig {
    private String callbackUrl;
    private DocumentUserInfo user;
    private DocumentCustomization customization;
    private String mode;
    private String lang;
    private String location;
    private String region;

    public DocumentEditorConfig(String callbackUrl, DocumentUserInfo user, DocumentCustomization customization, String mode) {
        this(callbackUrl,user,customization,mode,"pt-BR","br","pt-BR");
    }

    private DocumentEditorConfig(String callbackUrl, DocumentUserInfo user, DocumentCustomization customization, String mode, String lang, String location, String region) {
        this.callbackUrl = callbackUrl;
        this.user = user;
        this.customization = customization;
        this.mode = mode;
        this.lang = lang;
        this.location = location;
        this.region = region;
    }
}
