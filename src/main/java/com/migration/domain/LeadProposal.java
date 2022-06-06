package com.migration.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.migration.domain.enums.CompanyType;
import com.migration.domain.enums.MaritalStatus;
import com.migration.domain.enums.TypeRegimeCompanion;
import com.migration.domain.persona.aggregation.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@Entity
@Table(name = "credi_lead_proposal")
public class LeadProposal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String telephone;
    private String email;

    @Column(name = "cpf_cnpj")
    private String cpfCnpj;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status")
    private MaritalStatus maritalStatus;

    @Column(name = "family_income")
    private BigDecimal familyIncome;

    @Column(name = "spouse_name")
    private String spouseName;

    @OneToOne(targetEntity = Address.class, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "home_address_id")
    private Address address;

    private boolean active = true;

    @Column(name = "monthly_revenue")
    private BigDecimal monthlyRevenue;

    @Column(name = "total_revenue")
    private BigDecimal totalRevenue;

    @Column(name = "monthly_expenses")
    private BigDecimal monthlyExpenses;

    @Column(name = "total_expenses")
    private BigDecimal totalExpenses;

    @JsonIgnore
    @UpdateTimestamp
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

    private String rg;

    @Column(name = "orgao_emissor")
    private String orgaoEmissor;

    @OneToOne(mappedBy = "leadProposal")
    private Proposal proposal;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_system")
    private TypeRegimeCompanion typeRegimeCompanion;

    private String nationality;

    private String citizenship;

    private String occupation;

    private String mother;

    @Column(name = "scr_consulted")
    private Boolean scrConsulted;

    @Column(name = "corporate_type")
    @Enumerated(EnumType.STRING)
    private CompanyType companyType;

    @Column(name = "cnae_code")
    private String cnaeCode;

    @Column(name = "pep")
    private Boolean pep;

    @Column(name = "same_address_warranty_home")
    private Boolean sameAddressWarrantyHome;

    @Column(name = "company_founding_date")
    private LocalDate companyFoundingDate;
}
