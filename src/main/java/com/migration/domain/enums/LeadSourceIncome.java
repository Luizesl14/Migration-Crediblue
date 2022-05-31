package com.migration.domain.enums;

public enum LeadSourceIncome {

    ASSALARIADO("Assalariado (CLT)", PersonaType.NATURAL_PERSON),
    EMPRESARIO("Empresário(a)", PersonaType.NATURAL_PERSON),
    AUTONOMO("Autônomo", PersonaType.NATURAL_PERSON),
    APOSENTADO_PENSIONISTA("Aposentado ou Pensionista", PersonaType.NATURAL_PERSON),
    FUNCIONARIO_PUBLICO("Funcionário Público", PersonaType.NATURAL_PERSON),
    PRESTADOR_SERVICO("Prestador de Serviços", PersonaType.NATURAL_PERSON),
    PROFISSIONAL_LIBERAL("Profissional Liberal", PersonaType.NATURAL_PERSON),
    RENDA_ALUGUEIS("Renda de Alugueis", PersonaType.NATURAL_PERSON),
    DO_LAR("Do Lar", PersonaType.NATURAL_PERSON),
    RURAL_PRODUCER("Produtor Rural/Pecuarista", PersonaType.NATURAL_PERSON),
    OUTROS("Outros", PersonaType.NATURAL_PERSON),
    PRIMARY_SECTOR("Setor Primário", PersonaType.LEGAL_PERSON),
    SECONDARY_SECTOR("Setor Secundário", PersonaType.LEGAL_PERSON),
    TERTIARY_SECTOR("Setor Terciário", PersonaType.LEGAL_PERSON);

    public final String description;
    public final String type;

    LeadSourceIncome(String desc, PersonaType type) {
        this.description = desc;
        this.type = type.name();
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }
}