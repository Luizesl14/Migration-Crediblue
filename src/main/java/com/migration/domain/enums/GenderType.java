package com.migration.domain.enums;

public enum GenderType {
    MALE ("Masculino"),
    FEMALE ("Feminino"),
    OTHERS ("Outros");


    private final String description;

    GenderType(String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return description;
    }
}
