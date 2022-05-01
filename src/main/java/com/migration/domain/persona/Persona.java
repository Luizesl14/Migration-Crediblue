package com.migration.domain.persona;

import com.migration.domain.enums.ProponentType;
import com.migration.domain.persona.aggregation.*;
import com.migration.domain.enums.GenderType;
import com.migration.domain.enums.MaritalStatus;
import com.migration.domain.enums.PersonaType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;


@Getter
@Setter
public class Persona {

    private Integer id;
    private String name;
    private String taxId;
    private PersonaType personaType;
    private GenderType genderType;
    private String rg;
    private String orgaoEmissor;
    private MaritalStatus maritalStatus;
    private String nationality;
    private String citizenship;
    private Date birthDate;
    private String motherName;
    private Company companyData;
    private List<PersonaAddress> addresses;
    private List<PersonaPhone> phones;
    private List<PersonaAccounts> bankAccounts;
    private PersonaCompanion companion;
    private List<PersonaComposeIncome> composeIncomes;
    private List<ContactEmail> contacts;
    private ProponentType proponentType;
    private String occupation;
    private Boolean pep;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
}
