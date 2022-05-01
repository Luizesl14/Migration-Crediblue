package com.migration.domain;

import com.migration.domain.persona.Persona;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Simulation {

    private Integer id;
    private Lead lead;
    private Persona persona;
    private Partner partner;
    private Integer installmentsAmount;
    private Finder finder;
    private String auditOriginClientIpAddress;

}
