package com.migration.domain.enums;

public enum OperationType {

    HOME_EQUITY(1,"Empréstimo com imóvel em garantia", "HE"),
    PROPERTY_FINANCING(2,"Financiamento de Imóvel", "FI"),
    CONSTRUCTION_CREDIT(3,"Crédito para construção", "CC");

    private final long id;
    private final String description;
    private final String shortcut;

    OperationType(long id, String desc, String shortcut) {
        this.id = id;
        this.description = desc;
        this.shortcut = shortcut;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getShortcut() {
        return shortcut;
    }
}