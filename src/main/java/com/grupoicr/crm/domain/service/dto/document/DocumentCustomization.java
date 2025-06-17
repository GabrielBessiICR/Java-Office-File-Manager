package com.grupoicr.crm.domain.service.dto.document;

import lombok.Getter;

@Getter
public class DocumentCustomization {
    private boolean forcesave;
    private boolean autosave;

    public DocumentCustomization() {
        this(true,false);
    }

    public DocumentCustomization(boolean forcesave, boolean autosave) {
        this.forcesave = forcesave;
        this.autosave = autosave;
    }
}
