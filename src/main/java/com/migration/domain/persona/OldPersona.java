package com.migration.domain.persona;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.migration.domain.Proposal;
import com.migration.domain.enums.*;
import com.migration.domain.persona.aggregation.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@ToString
@Entity
@Table(name = "credi_persona")
public class OldPersona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String rg;
    @Column(name = "orgao_emissor")
    private String orgaoEmissor;

    @Column(name = "cpf_cnpj")
    private String cpfCnpj;
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status")
    private MaritalStatus maritalStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_system")
    private PropertySystem propertySystem;

    @Enumerated(EnumType.STRING)
    @Column(name = "proponent_type")
    private ProponentType proponentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_income")
    private LeadSourceIncome sourceIncome;

    private String nationality;
    private String citizenship;
    private String occupation;

    @Column(name = "monthly_income")
    private BigDecimal monthlyIncome;
    private String telephone;
    private String email;

    @Column(name = "birthdate")
    private LocalDate birthDate;

    @Column(name = "opening_date")
    private LocalDate openingDate;

    @Column(name = "persona_partners")
    private String personaPartners;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "companion_id")
    private Companion companion;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "financial_institution_code")
    private String financialInstitutionCode;

    @Column(name = "account_branch")
    private String accountBranch;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_digit")
    private String accountDigit;

    private double participationPercentage;

    private boolean legalRepresentative;

    @Column(name = "mother_name")
    private String motherName;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;

    @Column(name = "external_analysis")
    private String externalAnalysis;

    @Column(name = "income_tax_analysis")
    private String incomeTaxAnalysis;

    @Column(name = "digital_media_analysis")
    private String digitalMediaAnalysis;

    @Column(name = "protest_analysis")
    private String protestAnalysis;

    @Column(name = "process_analysis")
    private String processAnalysis;

    @Column(name = "scr_analysis")
    private String scrAnalysis;

    @Column(name = "tax_id")
    private String taxId;


    @Enumerated(EnumType.STRING)
    @Column(name = "persona_type")
    private PersonaType personaType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company_data_id")
    private Company companyData;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_id")
    private List<PersonaAddress> addresses = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_id")
    private List<PersonaPhone> phones = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_id")
    private List<PersonaAccounts> bankAccounts = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_id")
    private List<PersonaComposeIncome> composeIncomes = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_id")
    private List<ContactEmail> contacts = new ArrayList<>();

}
