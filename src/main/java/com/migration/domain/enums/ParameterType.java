package com.migration.domain.enums;

public enum ParameterType {

    DECIMAL("Decimal"),
    PERCENTAGE("Porcentagem");

    private final String descriptionEnum;

    ParameterType(String desc) {
        this.descriptionEnum = desc;
    }

    public String getDescriptionEnum() {
        return descriptionEnum;
    }
}
