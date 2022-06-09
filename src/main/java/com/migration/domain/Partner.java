package com.migration.domain;

import com.migration.domain.enums.SeguimentType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "credi_partner")
public class Partner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String cpfCnpj;
    private String name;
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

//    @OneToMany
//    @JoinColumn(name = "contact_id")
//    private List<Contact> contacts = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_id")
    private Persona persona;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "partner_id")
    private List<PartnerContact> partnerContacts = new ArrayList<>();

    @Column(name = "summary")
    private String summary;

    @Column(name = "segment")
    @Enumerated(EnumType.STRING)
    private SeguimentType segment;

    @Column(name = "instagram")
    private String instagram;

    @Column(name = "opportunity")
    private Boolean opportunity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
