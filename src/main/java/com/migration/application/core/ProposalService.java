package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.domain.LeadProposal;
import com.migration.domain.Proposal;
import com.migration.domain.ProposalProponent;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.enums.ProponentType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.PersonaCompanion;
import com.migration.domain.persona.aggregation.PersonaComposeIncome;
import com.migration.domain.persona.aggregation.Phone;
import com.migration.infrastructure.IPersonaRepository;
import com.migration.infrastructure.ProposalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProposalService {

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private ConvertLocalDataTime convert;

    @Autowired
    private CreateObject create;

    @Transactional
    public void findAll() {
        List<Proposal> proposals = this.proposalRepository.findAll();
        System.out.println("Quantidade de proposals no banco: " + proposals.size());

        proposals.forEach(proposal -> {
            Persona persona = this.personaRepository.findByTaxId(proposal.getLeadProposal().getCpfCnpj());
            if(persona != null){
                ProposalProponent proponentPrincipal =  this.create.createProponent(persona,
                        proposal.getLeadProposal().getCreatedAt(),
                        ProponentType.PRINCIPAL);

                proposal.getProponents().add(proponentPrincipal);

                proposal.getPersonas().forEach(personaProp->{
                    Persona proponent = this.createPersona(personaProp);
                    ProposalProponent createProponents =  this.create.createProponent(proponent,
                            proposal.getLeadProposal().getCreatedAt(),
                            personaProp.getProponentType());
                    proposal.getProponents().add(createProponents);
                    this.save(proposal);
                });

                this.save(proposal);
            }
        });
    }


    @Transactional
    public Persona createPersona (Persona leadProposal) {

        Persona persona = new Persona();
        persona.setName(leadProposal.getName());
        persona.setTaxId(leadProposal.getCpfCnpj());
        persona.setPersonaType(
                leadProposal.getCpfCnpj()
                        .length() == 11 ? PersonaType.NATURAL_PERSON: PersonaType.LEGAL_PERSON);
        persona.setMaritalStatus(leadProposal.getMaritalStatus());
        persona.setBirthDate(this.convert.convertToLocalDate(leadProposal.getBirthDate()));
        persona.setMaritalStatus(leadProposal.getMaritalStatus());
        persona.setRg(leadProposal.getRg());
        persona.setOrgaoEmissor(leadProposal.getOrgaoEmissor());
        persona.setNationality(leadProposal.getNationality());
        persona.setMotherName(leadProposal.getMotherName());
        persona.setCitizenship(leadProposal.getCitizenship());
        persona.setPep(leadProposal.getPep());
        persona.setOccupation(leadProposal.getOccupation());
        persona.setGenderType(leadProposal.getGenderType());

        if(leadProposal.getCompanyType() != null){
            persona.getCompanyData().setType(leadProposal.getCompanyType());
            persona.getCompanyData().setCnae(leadProposal.getCnaeCode());
            persona.getCompanyData().setFoundationDate(
                    this.convert.convertToLocalDate(leadProposal.getCompanyFoundingDate()));
        }

        if(leadProposal.getSpouseName() != null){
            PersonaCompanion personaCompanion = new PersonaCompanion();
            personaCompanion.getPersona().setName(leadProposal.getSpouseName());
            personaCompanion.setRegime(leadProposal.getTypeRegimeCompanion());
        }

        if(leadProposal.getMonthlyRevenue() != null
                && leadProposal.getMonthlyExpenses() != null
                && leadProposal.getTotalRevenue() != null){
            PersonaComposeIncome composeIncome = new PersonaComposeIncome();
            composeIncome.getComposeIncome().setMonthlyRevenue(leadProposal.getMonthlyRevenue());
            composeIncome.getComposeIncome().setMonthlyRevenue(leadProposal.getMonthlyExpenses());
            composeIncome.getComposeIncome().setTotalRevenue(leadProposal.getTotalRevenue());
            persona.getComposeIncomes().add(composeIncome);
        }

        if(leadProposal.getAccountInfo() != null){
            persona.getBankAccounts().add(
                    this.create.createAccount(leadProposal.getAccountInfo(), null));
        }
        if(leadProposal.getAddress() != null){
            persona.getAddresses().add(
                    this.create.createAddress(leadProposal.getAddress(), leadProposal.getAddress().getCreatedAt()));
        }
        if(leadProposal.getEmail() != null){
            persona.getContacts().add(
                    this.create.createEmail(leadProposal.getEmail(), null));
        }
        if(leadProposal.getTelephone() != null){
            Phone phone = new Phone();
            phone.setNumber(leadProposal.getTelephone());
            phone.setIsWhatsApp(Boolean.FALSE);
            persona.getPhones().add(this.create.createPhone(phone, null));
        }
        return  persona;
    }


    @Transactional
    public void save (Proposal proposal) {
        this.proposalRepository.save(proposal);
        System.out.println("Proposta salva com proponent principal " + proposal);
    }
}
