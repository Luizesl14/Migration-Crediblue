package com.migration.domain.persona;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.migration.domain.LeadProposal;
import com.migration.domain.Proposal;
import com.migration.domain.enums.*;
import com.migration.domain.persona.aggregation.*;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.FetchProfile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


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

    @OneToOne(cascade = CascadeType.ALL)
    private Companion companion;

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


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_companion_id")
    private PersonaCompanion personaCompanionId;


    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_id")
    private List<PersonaComposeIncome> composeIncomes = new ArrayList<>();


    @Column(name = "compose_income")
    private Boolean composeIncome;


    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_id")
    private List<ContactEmail> contacts = new ArrayList<>();


    @Column(name = "occupation")
    private String occupation;

    @Column(name = "pep")
    private Boolean pep;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "deleted_at")
    private Date deletedAt;


    @Column(name = "monthly_income")
    private BigDecimal monthlyIncome;
    private String telephone;
    private String email;

    @Column(name = "opening_date")
    private LocalDate openingDate;

    @Column(name = "persona_partners")
    private String personaPartners;

    @JsonIgnore
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "address_id")
    private Address address;


    @Enumerated(EnumType.STRING)
    @Column(name = "property_system")
    private PropertySystem propertySystem;


    @Column(name = "financial_institution_code")
    private String financialInstitutionCode;

    @Column(name = "account_branch")
    private String accountBranch;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_digit")
    private String accountDigit;

    private Double participationPercentage;

    private Boolean legalRepresentative;

    @JsonIgnore
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

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "lead_proposal_id")
    private LeadProposal leadProposal;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Persona persona = (Persona) o;
        return Objects.equals(cpfCnpj, persona.cpfCnpj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpfCnpj);
    }
}
