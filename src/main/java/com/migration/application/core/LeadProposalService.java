package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.domain.Proposal;
import com.migration.domain.ProposalProponent;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.enums.ProponentType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Company;
import com.migration.domain.persona.aggregation.PersonaCompanion;
import com.migration.domain.persona.aggregation.Phone;
import com.migration.infrastructure.IPersonaRepository;
import com.migration.infrastructure.IProposalProponentRepository;
import com.migration.infrastructure.IProposalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
@Service
public class LeadProposalService {

    @Autowired
    private IProposalRepository proposalRepository;

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private ConvertLocalDataTime convert;

    @Autowired
    private CreateObject create;

    @Autowired
    private IProposalProponentRepository proposalProponentRepository;


    public void findAll() {
        List<Proposal> proposals = this.proposalRepository.findAll();
        System.out.println("Quantidade de leads proposal do banco: " + proposals.size());
        this.createPersona(proposals);
    }

    public Boolean createPersona (List<Proposal> proposals) {

        int index = 0;
        for (Proposal proposal : proposals) {
            Persona persona = new Persona();
            persona.setCpfCnpj(proposal.getLeadProposal().getCpfCnpj());

            persona.setPersonaType(
                    proposal.getLeadProposal().getCpfCnpj()
                            .length() == 11 ? PersonaType.NATURAL_PERSON : PersonaType.LEGAL_PERSON);

            if (persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)) {
                persona.setName(proposal.getLeadProposal().getName());
            } else {
                Company company = new Company();
                company.setCorporateName(proposal.getLeadProposal().getName());
                company.setType(proposal.getLeadProposal().getCompanyType());
                company.setCnae(proposal.getLeadProposal().getCnaeCode());

                if (proposal.getLeadProposal().getCompanyFoundingDate() != null) {
                    company.setFoundationDate(
                            this.convert.convertToLocalDate(proposal.getLeadProposal().getCompanyFoundingDate()));
                }
                persona.setCompanyData(company);
            }
            persona.setMaritalStatus(proposal.getLeadProposal().getMaritalStatus());
            persona.setBirthDate(proposal.getLeadProposal().getBirthDate());
            persona.setMaritalStatus(proposal.getLeadProposal().getMaritalStatus());
            persona.setRg(proposal.getLeadProposal().getRg());
            persona.setOrgaoEmissor(proposal.getLeadProposal().getOrgaoEmissor());
            persona.setNationality(proposal.getLeadProposal().getNationality());
            persona.setMotherName(proposal.getLeadProposal().getMother());
            persona.setCitizenship(proposal.getLeadProposal().getCitizenship());
            persona.setPep(proposal.getLeadProposal().getPep());
            persona.setOccupation(proposal.getLeadProposal().getOccupation());

            if (proposal.getLeadProposal().getSpouseName() != null) {
                PersonaCompanion personaCompanion = new PersonaCompanion();
                Persona companion = new Persona();
                companion.setName(proposal.getLeadProposal().getSpouseName());
                personaCompanion.setType(proposal.getLeadProposal().getTypeRegimeCompanion());
                personaCompanion.setData(companion);
            }
            if(proposal.getLeadProposal().getCreatedAt() != null){
                persona.setCreatedAt(this.convert.covertLocalDataTimeToDate(proposal.getLeadProposal().getCreatedAt()));
            }

            if (proposal.getLeadProposal().getFinancialInstitutionCode() != null) {
                persona.getBankAccounts().add(
                        this.create.createAccount(
                                proposal.getLeadProposal().getFinancialInstitutionCode(), proposal.getLeadProposal().getAccountBranch(),
                                proposal.getLeadProposal().getAccountNumber(), proposal.getLeadProposal().getAccountDigit(), null));
            }
            if (proposal.getLeadProposal().getAddress() != null) {
                persona.getAddresses().add(
                        this.create.createAddress(proposal.getLeadProposal().getAddress(), proposal.getLeadProposal().getAddress().getCreatedAt(), persona));
            }
            if (proposal.getLeadProposal().getEmail() != null) {
                persona.getContacts().add(
                        this.create.createEmail(proposal.getLeadProposal().getEmail(),
                                this.convert.covertLocalDataTimeToDate(proposal.getLeadProposal().getCreatedAt())));
            }
            if (proposal.getLeadProposal().getTelephone() != null) {
                Phone phone = new Phone();
                phone.setNumber(proposal.getLeadProposal().getTelephone());
                phone.setIsWhatsApp(Boolean.FALSE);
                persona.getPhones().add(this.create.createPhone(phone, null));
            }
                persona.setLeadProposal(proposal.getLeadProposal());
                Persona personaSaved = this.personaRepository.save(persona);
                this.saveProponent(personaSaved,proposal, personaSaved.getCreatedAt(), ProponentType.PRINCIPAL);
                if (persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)) {
                    System.out.println("New Person ** PF ** : " + persona.getName());
                } else {
                    System.out.println("New Person ** PJ ** : " + persona.getCompanyData().getCorporateName());
                System.out.println();
            }
            System.out.println("### Index persona criado: " + index++);
        }
        return  Boolean.TRUE;
    }

    public  Boolean saveProponent(Persona persona, Proposal proposal, Date createdAt, ProponentType proponentType){
        ProposalProponent proponent = this.create.createProponentPrincipal(createdAt, proponentType);

        if(persona.getSourceIncome() != null){
            proponent.setComposeIncome(Boolean.TRUE);
            proponent.setMonthlyIncome(persona.getMonthlyIncome());
        }
        if(persona.getProponentType() != null){
            if(persona.getProponentType().equals(ProponentType.PRINCIPAL)){
                proponent.setScrConsulted(persona.getProposal().getLeadProposal().getScrConsulted());
            }
        }
        if( persona.getParticipationPercentage() != null){
            if(persona.getParticipationPercentage() != 0){
                proponent.setPercentageOfCommitment(
                        persona.getParticipationPercentage() == null ? 0 : persona.getParticipationPercentage());
            }
        }

        ProposalProponent proposalProponentSaved = this.proposalProponentRepository.save(proponent);
        proposalProponentSaved.setPersona(persona);
        proposalProponentSaved.setProposal(proposal);
        this.proposalProponentRepository.save(proposalProponentSaved);

        System.out.println(" ## ID ##: " + persona.getId() + " Proponent Salvo ** PF ** : "+ persona.getName());
        return Boolean.TRUE;
    }

}
