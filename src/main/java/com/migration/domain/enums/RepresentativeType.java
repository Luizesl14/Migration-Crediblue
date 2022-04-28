package com.migration.domain.enums;

public enum RepresentativeType {

    LEGAL_REPRESENTATIVE("Representante Legal"),
    JUDICIAL_REPRESENTATIVE("Representante Judicial"),
    CONVENTIONAL("Representante convencional");

    public final String description;

    RepresentativeType(String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return description;
    }

}
