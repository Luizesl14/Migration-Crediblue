package com.migration.domain.enums;

public enum ContactType {

    LEGAL_REPRESENTATIVE("Representante legal "),
    INTERNAL_CONTACT("Contato Interno");

    ContactType(String description) {
        this.description = description;
    }

    private final String description;

    public String getDescription() {
        return description;
    }
}
