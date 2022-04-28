package com.migration.domain.enums;

public enum ParameterCategoryType {

    GENERAL("Geral"),
    HOME_EQUITY("Empréstimo com imóvel em garantia"),
    PROPERTY_FINANCING("Financiamento de Imóvel"),
    GUARANTEE_CREDIT("Crédito para construção");

    private final String description;

    ParameterCategoryType(String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return description;
    }
}
