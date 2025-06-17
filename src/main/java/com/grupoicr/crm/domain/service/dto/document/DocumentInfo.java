package com.grupoicr.crm.domain.service.dto.document;

import lombok.Getter;

@Getter
public class DocumentInfo {
        private String fileType;
        private String key;
        private String title;
        private String url;

    public DocumentInfo(String fileType, String key, String title, String url) {
        this.fileType = fileType;
        this.key = key;
        this.title = title;
        this.url = url;
    }
}
