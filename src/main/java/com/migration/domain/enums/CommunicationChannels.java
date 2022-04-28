package com.migration.domain.enums;

public enum CommunicationChannels {

    VIDEO_CALLS("Vídeo chamadas"),
    PHONE_CALLS("Ligações telefônicas"),
    WHATSAPP("Whatsapp"),
    EMAIL("E-mail"),
    FACE_TO_FACE_MEETINGS("Reuniões presenciais");

    private final String description;

    CommunicationChannels(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}