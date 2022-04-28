package com.migration.domain.enums;

public enum AddressType {

    COMMERCIAL("Comercial"),
    RESIDENTIAL("Residencial"),
    OTHERS("Outros");

    public final String description;

    AddressType(String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return description;
    }
}
