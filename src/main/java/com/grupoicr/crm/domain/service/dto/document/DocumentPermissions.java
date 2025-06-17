package com.grupoicr.crm.domain.service.dto.document;

import lombok.Getter;

@Getter
public class DocumentPermissions {
    private boolean edit;
    private boolean download;
    private boolean print;
    private boolean copy;

    public DocumentPermissions() {
        this.edit = true;
        this.download = true;
        this.print = true;
        this.copy = true;
    }

    public DocumentPermissions(boolean edit, boolean download, boolean print, boolean copy) {
        this.edit = edit;
        this.download = download;
        this.print = print;
        this.copy = copy;
    }
}
