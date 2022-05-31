package com.migration.domain;

import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "credi_finder")
public class Finder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String name;
    private String cpf;
    private String telephone;
    private String email;

    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "financial_institution_code")
    private String financialInstitutionCode;

    @Column(name = "account_branch")
    private String accountBranch;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_digit")
    private String accountDigit;

    @OneToOne
    @JoinColumn(name = "persona_id")
    private Persona persona;

    @OneToOne
    @JoinColumn(name = "partner_id")
    private Partner partner;

    private boolean active = true;
    private LocalDateTime createdAt;
}
