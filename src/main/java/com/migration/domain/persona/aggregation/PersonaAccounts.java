package com.migration.domain.persona.aggregation;

import com.migration.domain.persona.Persona;
import com.migration.domain.enums.BankAccount;
import com.migration.domain.enums.BankAccountType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PersonaAccounts {

    private Integer id;
    private Persona persona;
    private BankAccount account;
    private BankAccountType type;
    private Boolean principal;
    private Date createdAt;
}
