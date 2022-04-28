package com.migration.domain.enums;

public enum EmailType {

    CORPORATE("Corporativo"),
    PERSONAL("Pessoal"),
    COMMERCIAL("Comercial");

    public final String description;

    EmailType(String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return description;
    }
}