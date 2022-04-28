package com.migration.domain.enums;

public enum WorkPlace {

    PHYSICAL_LOCATION("Físico (escritório, stand, loja)"),
    ONLINE("Apenas virtual"),
    EXTERNAL("Visitas externas"),
    RESTAURANTS_AND_COFFEES("Cafés e restaurantes");

    private final String description;

    WorkPlace(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}