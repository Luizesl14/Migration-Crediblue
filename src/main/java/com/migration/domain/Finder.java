package com.migration.domain;

import com.migration.domain.enums.BankAccount;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Address;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Finder {

    private Integer id;
    private String name;
    private String cpf;
    private String telephone;
    private String email;
    private Address address;
    private BankAccount accountInfo;
    private Persona persona;
    private Partner partner;
    private boolean active = true;
    private LocalDateTime createdAt;
}
