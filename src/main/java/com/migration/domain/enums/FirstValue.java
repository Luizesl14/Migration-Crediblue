package com.migration.domain.enums;

public enum FirstValue {

    CELERITY("Celeridade"),
    TRANSPARENCY("Transparência"),
    RATE("Taxa"),
    RELATIONSHIP("Relacionamento"),
    FLEXIBILITY("Flexibilidade"),
    COMMISSION("Comissão");

    private final String description;

    FirstValue(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}