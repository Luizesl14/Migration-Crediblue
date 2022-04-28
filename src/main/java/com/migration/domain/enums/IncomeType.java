package com.migration.domain.enums;

public enum IncomeType {

    PASSIVE_INCOME("Renda passiva"),
    FIXED_INCOME("Renda Fixa"),
    LIFETIME_INCOME("Renda Vitalícia"),
    VARIABLE_INCOME("Renda Variável");

    public final String description;

    IncomeType(String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return description;
    }
}