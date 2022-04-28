package com.migration.domain.enums;

public enum SeguimentType {

    CONSORTIUM_ADMINISTRATOR("Administradora de consórcio"),
    LAWYER("Advogado"),
    TRAVEL_AGENCY_EVENTS("Agência de viagem/eventos"),
    HEALTH_AREA("Área de saúde"),
    FINANCIAL_ADVISOR_PLANNER("Assessor/Planejador Financeiro"),
    ASSET_FINANCIAL("Asset/Financeira"),
    ASSOCIATION("Associação"),
    AUTONOMOUS("Autônomo"),
    CORRESPONDENT_BANKING("Correspondente bancário"),
    REALTOR("Corretor de imóveis"),
    INSURANCE_BROKER("Corretor de seguros"),
    CONSTRUCTOR_DEVELOPER("Construtora/Incorporadora"),
    CREDIT_CONSULTANT("Consultor de Crédito"),
    BUSINESS_CONSULTING("Consultoria Empresarial"),
    COUNTER("Contador"),
    TIMESHARE_ENTERPRISE("Empreendimento de Multipropriedade"),
    ARCHITECT_ENGINEER("Engenheiro/Arquiteto"),
    ENERGY_SUPPLIER("Fornecedor de energia"),
    REAL_ESTATE("Imobiliária"),
    RETAIL_STORES_TICKET_SALES("Lojas de varejo/Vendas de alto ticket"),
    MARKETPLACE("Marketplace"),
    INSURANCE_COMPANY("Seguradora"),
    OTHERS("Outros");

    private final String description;

    SeguimentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}