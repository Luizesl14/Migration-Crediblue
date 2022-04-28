package com.migration.domain.enums;

public enum LeadOrigin {

    DIGITAL("Digital"),
    ACTIVE("Ativa"),
    APPOINTMENT("Indicação");

    private final String description;

    LeadOrigin(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
