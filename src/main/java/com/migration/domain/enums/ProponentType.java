package com.migration.domain.enums;


public enum ProponentType {

    SPOUSE("Cônjuge"),
    GUARANTOR("Garantidor"),
    PROCURATOR("Procurador"),
    COMPANY_PARTNER("Sócio"),
    MAIN_CLIENT("Solicitante"),
    PRINCIPAL("Principal"),
    SELLER("Vendedor");

    private final String description;

    ProponentType(String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return description;
    }
}
