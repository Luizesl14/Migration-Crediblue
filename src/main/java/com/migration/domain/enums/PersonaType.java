package com.migration.domain.enums;

public enum PersonaType {
    NATURAL_PERSON("Pessoa Física"),
    LEGAL_PERSON("Pessoa Jurídica");

    private final String description;

    PersonaType(String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return description;
    }
}
