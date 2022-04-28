package com.migration.domain.enums;

public enum Prospecting {

    SOCIAL_NETWORK_POSTS("Redes Sociais", Constants.DIGITAL),
    SPONSORED("Patrocinado", Constants.DIGITAL),
    BROADCAST_LIST("Lista de transmissão", Constants.DIGITAL),
    LEAFLETING("Panfletagem", Constants.OFFLINE),
    PAP("PAP", Constants.OFFLINE),
    RECOMMENDATION("Indicação", Constants.OFFLINE),
    PARTNERSHIPS_REPRESENTATIVES("Parcerias/Representantes", Constants.OFFLINE);

    private final String description;
    private final String type;

    Prospecting(String desc, String type) {
        this.description = desc;
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    private static class Constants {
        public static final String DIGITAL = "DIGITAL";
        public static final String OFFLINE = "OFFLINE";
    }
}