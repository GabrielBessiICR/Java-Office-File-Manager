package com.grupoicr.crm.domain.service.dto.document;

import lombok.Getter;

@Getter
public class DocumentUserInfo {
    private String id;
    private String name;

    public DocumentUserInfo(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
