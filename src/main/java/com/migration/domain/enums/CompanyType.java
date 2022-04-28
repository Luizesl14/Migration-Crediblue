package com.migration.domain.enums;

public enum CompanyType {
    SA_OPENED("Sociedade Anônima Aberta"),
    SA_CLOSED("Sociedade Anônima Fechada"),
    SE_LTDA("Sociedade Empresária Limitada"),
    SE_CN("Sociedade Empresária em Nome Coletivo"),
    SE_CS("Sociedade Empresária em Comandita Simples"),
    SE_CA("Sociedade Empresária em Comandita por Ações"),
    SCP("Sociedade em Conta de Participação"),
    EI("Empresário (Individual)"),
    ESE("Estabelecimento, no Brasil, de Sociedade Estrangeira"),
    EEAB("Estabelecimento, no Brasil, de Empresa Binacional Argentino-Brasileira"),
    SSP("Sociedade Simples Pura"),
    SS_LTDA("Sociedade Simples Limitada"),
    SS_CN("Sociedade Simples em Nome Coletivo"),
    SS_CS("Sociedade Simples em Comandita Simples"),
    EIRELI_NE("Empresa Individual de Responsabilidade Limitada (de Natureza Empresária)"),
    EIRELI_NS("Empresa Individual de Responsabilidade Limitada (de Natureza Simples)");

    private final String description;

    CompanyType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}