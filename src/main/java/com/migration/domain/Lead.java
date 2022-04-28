package com.migration.domain;

import com.migration.domain.enums.MaritalStatus;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Address;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Lead {

    private Integer id;
    private String name;
    private LocalDate birthDate;
    private String telephone;
    private String email;
    private String cpfCnpj;
    private MaritalStatus maritalStatus;
    private BigDecimal familyIncome;
    private String spouseName;
    private Address address;
    private boolean active = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isChecked = false;
    private Persona persona;
    private Partner partner;
}
