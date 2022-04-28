package com.migration.domain.enums;

public enum Gender {

    M("Masculino"),
    F("Feminino");

    private final String description;

    private Gender(String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return this.description;
    }
}