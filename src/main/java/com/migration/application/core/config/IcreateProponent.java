package com.migration.application.core.config;


import com.migration.domain.*;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.enums.ProponentType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.ContactEmail;
import com.migration.domain.persona.aggregation.PersonaAccounts;
import com.migration.domain.persona.aggregation.PersonaAddress;
import com.migration.domain.persona.aggregation.PersonaPhone;

import java.util.List;

public interface IcreateProponent {


    Boolean notNullAddress(List<PersonaAddress> addresses);

    Boolean notNullAccount(List<PersonaAccounts> accounts);

    Boolean notNullEmail(List<ContactEmail> contactEmails);

    Boolean notNullPhone(List<PersonaPhone> personaPhones);

    PersonaType isTaxId(String taxId);

    void goThroughProposal();

    void checkedLead(Lead lead);

    void checkedLeadProposal(LeadProposal leadProposal);

    void checkedPartner(Partner partner);

    void checkedFinder(Finder finder);

    void checkedInvestor(Investor investor);

    void checkedUser(User user);

    void checkedPersona(Persona persona);


    void updateLeadProposal(LeadProposal leadProposal);

    void updatePartner(Partner partner);

    void updateFinder(Finder finder);

    void updateInvestor(Investor investor);

    void updatekedUser(User user);

    void updatePersona(Persona persona);


    Persona createdPersona(Lead lead, LeadProposal leadProposal, Partner partner,
                           Finder finder, Investor investor, User user, Persona persona);


    ProposalProponent createdProponent(Persona persona);


}
