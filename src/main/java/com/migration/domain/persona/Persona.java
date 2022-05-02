package com.migration.domain.persona;

import com.migration.domain.enums.ProponentType;
import com.migration.domain.persona.aggregation.*;
import com.migration.domain.enums.GenderType;
import com.migration.domain.enums.MaritalStatus;
import com.migration.domain.enums.PersonaType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;


@Getter
@Setter
@ToString
@Entity
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
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

    @OneToOne
    @JoinColumn(name = "company_id")
    private Company companyData;

    @OneToMany
    @JoinColumn(name = "persona_id")
    private List<PersonaAddress> addresses;

    @OneToMany
    @JoinColumn(name = "persona_id")
    private List<PersonaPhone> phones;

    @OneToMany
    @JoinColumn(name = "persona_id")
    private List<PersonaAccounts> bankAccounts;

    @OneToOne
    @JoinColumn(name = "persona_id")
    private PersonaCompanion companion;

    @OneToMany
    @JoinColumn(name = "persona_id")
    private List<PersonaComposeIncome> composeIncomes;

    @OneToMany
    @JoinColumn(name = "persona_id")
    private List<ContactEmail> contacts;

    private ProponentType proponentType;
    private String occupation;
    private Boolean pep;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
}
