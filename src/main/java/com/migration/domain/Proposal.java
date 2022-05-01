package com.migration.domain;


import com.migration.domain.persona.Persona;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class Proposal {

    private Integer id;
    private Simulation simulation;
    private Partner partner;
    private List<Persona> investors;
    private List<User> users = new ArrayList<>();
    private LeadProposal leadProposal;
    private List<ProposalProponent> proponents = new ArrayList<>();
    private List<Persona> personas = new ArrayList<>();
    private Boolean sameAddressWarrantyHome;
    private Integer installmentsAmount;
    private Proposal proposalMain;
}
