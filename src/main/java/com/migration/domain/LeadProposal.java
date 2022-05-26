package com.migration.domain;


import com.migration.domain.enums.BankAccount;
import com.migration.domain.enums.CompanyType;
import com.migration.domain.enums.MaritalStatus;
import com.migration.domain.enums.TypeRegimeCompanion;
import com.migration.domain.persona.aggregation.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@Entity
public class LeadProposal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String name;
    private LocalDate birthDate;
    private String telephone;
    private String email;
    private String cpfCnpj;
    private MaritalStatus maritalStatus;
    private BigDecimal familyIncome;
    private String spouseName;

    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;
    private boolean active = true;
    private BigDecimal monthlyRevenue;
    private BigDecimal totalRevenue;
    private BigDecimal monthlyExpenses;
    private BigDecimal totalExpenses;
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
    private String orgaoEmissor;
    private TypeRegimeCompanion typeRegimeCompanion;
    private String nationality;
    private String citizenship;
    private String occupation;
    private String mother;
    private Boolean scrConsulted;
    private CompanyType companyType;
    private String cnaeCode;
    private Boolean pep;
    private Boolean sameAddressWarrantyHome;
    private LocalDate companyFoundingDate;
}
