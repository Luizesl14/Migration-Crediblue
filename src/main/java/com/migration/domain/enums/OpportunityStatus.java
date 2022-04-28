package com.migration.domain.enums;

public enum OpportunityStatus {

    FIRST_CONTACT("1º contato"),
    QUALIFICATION("Qualificação"),
    SCHEDULING("Agendademento"),
    RESCHEDULING("Reagendamento"),
    CLOSURE("Fechamento"),
    NEGOTIATION("Negociação"),
    RELATIONSHIP("Relacionamento"),
    LOST_BY_B2C("Perdida por B2C"),
    LOST_FOR_NOT_COMPLYING("Perdida por não aderência");

    private final String description;

    OpportunityStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}