package com.migration.domain.persona;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.migration.domain.enums.*;
import com.migration.domain.persona.aggregation.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@ToString
@Entity
@Table(name = "credi_persona")
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String name;

    @Column(name = "cpf_cnpj")
    private String cpfCnpj;

    @Column(name = "tax_id")
    private String taxId;

    @Enumerated(EnumType.STRING)
    @Column(name = "persona_type")
    private PersonaType personaType;

    @Enumerated(EnumType.STRING)
    @Column(name = "proponent_type")
    private ProponentType proponentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender_type")
    private GenderType genderType;

    @Column(name = "rg")
    private String rg;

    @Column(name = "orgao_emissor")
    private String orgaoEmissor;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status")
    private MaritalStatus maritalStatus;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "citizenship")
    private String citizenship;

    @Column(name = "birthdate")
    private LocalDate birthDate;

    @Column(name = "mother_name")
    private String motherName;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_income")
    private LeadSourceIncome sourceIncome;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company_data_id")
    private Company companyData;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_id")
    private List<PersonaAddress> addresses = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_id")
    private List<PersonaPhone> phones = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_id")
    private List<PersonaAccounts> bankAccounts = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_companion_id")
    private PersonaCompanion companion;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_id")
    private List<PersonaComposeIncome> composeIncomes = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_id")
    private List<ContactEmail> contacts = new ArrayList<>();


    @Column(name = "occupation")
    private String occupation;

    @Column(name = "pep")
    private Boolean pep;

    @Column(name = "created_at")
    private Date createdAt = new Date();

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "deleted_at")
    private Date deletedAt;

    @PrePersist
    public void persist() {
        this.createdAt =  new Date();
    }

}
