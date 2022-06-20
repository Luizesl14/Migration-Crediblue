package com.migration.application.core.config;


import com.migration.domain.*;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.enums.PropertySystem;
import com.migration.domain.enums.TypeRegimeCompanion;
import com.migration.domain.persona.Persona;

public interface IcreateProponent {

    void satartApplication();

    void goThroughProponent();

    void saveLeadProposalByPerosnaMigration();

    void goThroughProponentPrincipal();

    void migrationPersonasNewEntity();

     void normaliedProponentPrincipal();

    ProposalProponent createdProponent(Persona persona);

    void goThroughSimulation();

    void normalizedEntityCpfAndCnpjIsNull();

    Partner updatePartner(Partner partner, Persona personaDatabse);

    Lead updateLead(Lead lead, Persona personaDatabse);

    Finder updateFinder(Finder finder, Persona personaDatabse);

    Investor updateInvestor(Investor investor, Persona personaDatabse);

    User updatedUser(User user, Persona personaDatabse);

    void normalizedPersona();

    void createdCompanionByPersona();

    void goToUser();
    void goToLead();
    void goToFinder();
    void goToInvestor();
    void goToPartner();

    void goThroughPersonaDocument();

    void goThroughLeadDocument();

    void goThroughCreditAnalysisDocument();

    void goThroughAnalysisBalanceAndIncome();


    TypeRegimeCompanion regimeType(PropertySystem system);

    PersonaType isTaxId(String taxId);

    Persona createdPersona(Lead lead, LeadProposal leadProposal, Partner partner,
                           Finder finder, Investor investor, User user, Persona persona);

    void checkedLead(Lead lead);

    void checkedLeadProposal(LeadProposal leadProposal);

    void checkedPartner(Partner partner);

    void checkedFinder(Finder finder);

    void checkedInvestor(Investor investor);

    void checkedUser(User user);

    void checkedPersona(Persona persona);


}
