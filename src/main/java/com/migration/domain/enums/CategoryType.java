package com.migration.domain.enums;

public enum CategoryType {

    COMMERCIAL("Comercial"),
    PERSONAL("Pessoal"),
    RESIDENTIAL("Residencial");

    public final String description;

    CategoryType(String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return description;
    }
}