package com.migration.domain.enums;

public enum PropertySystem {

    PARTIAL_COMMUNION("Comunhão Parcial"),
    UNIVERSIAL_COMMUNION("Comunhão Universal"),
    TOTAL_SEPARATION("Separação Total");


    public final String description;

    PropertySystem(String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return description;
    }

}