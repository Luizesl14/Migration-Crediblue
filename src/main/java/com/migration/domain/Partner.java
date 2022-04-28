package com.migration.domain;

import com.migration.domain.enums.*;
import com.migration.domain.persona.aggregation.Address;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class Partner {

    private Integer id;
    private String cpfCnpj;
    private String name;
    private String telephone;
    private String email;
    private PartnerSize partnerSize;
    private String summary;
    private SeguimentType segment;
    private Address address;
    private List<Parameter> parameters;
    private Set<OperationType> operationTypes = new HashSet<>();
    private BankAccount accountInfo;
    private String instagram;
    private String site;
    private Boolean active = true;
    private boolean opportunity;
    private Boolean alreadyHasCreditSolution = false;
    private List<Contact> contacts;
    private LandingPage landingPage;
    private String corporateName;
    private String facebook;
    private String linkedin;
    private LocalDate partnerSince;
    private OpportunityStatus opportunityStatus;
    private WorkPlace workPlace;
    private Set<CommunicationChannels> communicationChannels = new HashSet<>();
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
    private Set<String> playersAlreadyWork = new HashSet<>();
    private Set<Prospecting> prospecting = new HashSet<>();
}
