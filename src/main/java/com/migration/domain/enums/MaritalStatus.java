package com.migration.domain.enums;

public enum MaritalStatus {

    CASADO("Casado(a)"),
    RELACAO_ESTAVEL("Relação Estável"),
    DIVORCIADO("Divorciado(a)"),
    SOLTEIRO("Solteiro(a)"),
    VIUVO("Viúvo(a)"),
    SEPARATE("Separado(a)");

    private final String description;

    MaritalStatus(String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return description;
    }
}