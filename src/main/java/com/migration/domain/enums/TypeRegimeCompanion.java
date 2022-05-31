package com.migration.domain.enums;

public enum TypeRegimeCompanion {

    PARTIAL_COMMUNION("Comunhão Parcial"),
    UNIVERSIAL_COMMUNION("Comunhão Universal"),
    TOTAL_SEPARATION("Separação Total"),
    FINAL_PARTICIPATION_IN_AQUESTOS("Participação Final nos Aquestos");


    private final String description;

    TypeRegimeCompanion(String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return description;
    }
}

