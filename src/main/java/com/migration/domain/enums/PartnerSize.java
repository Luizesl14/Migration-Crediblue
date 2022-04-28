package com.migration.domain.enums;

public enum PartnerSize {

    LIGHT("Light"),
    PROFESSIONAL("Pro"),
    EXPERT("Expert"),
    ENTERPRISE("Enterprise");

    private final String description;

    PartnerSize(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}