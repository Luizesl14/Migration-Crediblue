package com.migration.domain;

import com.migration.domain.enums.*;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
public class Partner {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "persona_id")
    private Persona persona;
    private String cpfCnpj;
    private String name;
    private String telephone;
    private String email;
    private PartnerSize partnerSize;
    private String summary;
    private SeguimentType segment;

    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToOne
    @JoinColumn(name = "account_id")
    private BankAccount accountInfo;
    private String instagram;
    private String site;
    private Boolean active = true;
    private boolean opportunity;
    private Boolean alreadyHasCreditSolution = false;

    @OneToMany
    @JoinColumn(name = "contact_id")
    private List<Contact> contacts;

    private String corporateName;
    private String facebook;
    private String linkedin;
    private LocalDate partnerSince;
    private OpportunityStatus opportunityStatus;
    private WorkPlace workPlace;
    private String accountManager;
    private LeadOrigin leadOrigin;
    private FirstValue firstValue;
    private String kypRating;
    private BigDecimal averageTicket;
    private Boolean resistanceToProvideInformation;
    private Boolean contradictoryInformation;
    private Boolean negativeToMakeVideoCall;
    private Boolean apparentIgnoranceOfTheMarket;
    private Boolean haveNetworking;
    private Boolean vagueInformation;

}
