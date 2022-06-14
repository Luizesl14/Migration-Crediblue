package com.migration.application.core;

import com.migration.application.core.config.IcreateProponent;
import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.domain.*;
import com.migration.domain.enums.CompanyType;
import com.migration.domain.enums.MaritalStatus;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.enums.ProponentType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Address;
import com.migration.domain.persona.aggregation.Company;
import com.migration.domain.persona.aggregation.Phone;
import com.migration.infrastructure.IPartnerRepository;
import com.migration.infrastructure.IPersonaRepository;
import com.migration.infrastructure.IProposalProponentRepository;
import com.migration.infrastructure.IProposalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class Start implements IcreateProponent {

    @Autowired
    private IProposalRepository proposalRepository;

    @Autowired
    private ConvertLocalDataTime convert;

    @Autowired
    private CreateObject create;

    @Autowired
    private IProposalProponentRepository proposalProponentRepository;

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private IPartnerRepository partnerRepository;

    private String name;
    private String phone;
    private String email;
    private String cpfCnpj;
    private MaritalStatus maritalStatus;
    private LocalDate localDate;
    private String rg;
    private String orgaoEmissor;
    private String nacionality;
    private String motherName;
    private String citiZenship;
    private Boolean isPep = false;
    private String occupation;
    private PersonaType personaType;
    private CompanyType companyType;
    private String cnae;
    private Date createdAd;
    private ProponentType proponentType;
    private LeadProposal leadProposal;
    private Address address;
    private String financialInstitutionCode;
    private String accountBranch;
    private String accountNumber;
    private String accountDigit;


    @Override
    public PersonaType isTaxId(String taxId) {
        if(taxId.length() <= 11)
            return PersonaType.NATURAL_PERSON;
        return PersonaType.LEGAL_PERSON;
    }

    @Override
    public void goThroughProposal() {
        List<Proposal> proposals = this.proposalRepository.findAll();
        List<ProposalProponent> proponents = new ArrayList<>();
        List<Persona> personas = new ArrayList<>();
        List<ProposalProponent> mainProponents = new ArrayList<>();


        proposals.parallelStream().forEach(proposal -> {

            proposal.getPersonas().forEach(persona -> proponents.add(this.createdProponent(persona)));
            personas.add( this.createdPersona(
                    null, proposal.getLeadProposal(),
                    null, null, null, null, null));
        });
        personas.parallelStream().forEach(persona -> {
            mainProponents.add(this.createdProponent(persona));
        });
//        this.proposalProponentRepository.saveAll(proponents);
//        this.personaRepository.saveAll(personas);
//        this.proposalProponentRepository.saveAll(mainProponents);
    }

    @Override
    public void checkedLead(Lead lead) {
        this.name = lead.getName();
        this.email = lead.getEmail();
        this.phone = lead.getTelephone();
        this.cpfCnpj = lead.getCpfCnpj();
        this.maritalStatus = lead.getMaritalStatus();
        this.localDate = lead.getBirthDate();
        this.personaType = this.isTaxId(lead.getCpfCnpj());
    }

    @Override
    public void checkedLeadProposal(LeadProposal leadProposal) {
        this.name = leadProposal.getName();
        this.email = leadProposal.getEmail();
        this.phone = leadProposal.getTelephone();
        this.cpfCnpj = leadProposal.getCpfCnpj();
        this.maritalStatus = leadProposal.getMaritalStatus();
        this.localDate = leadProposal.getBirthDate();
        this.rg = leadProposal.getRg();
        this.orgaoEmissor = leadProposal.getOrgaoEmissor();
        this.nacionality = leadProposal.getNationality();
        this.motherName = leadProposal.getMother();
        this.citiZenship = leadProposal.getCitizenship();
        this.isPep = !leadProposal.getPep() ? leadProposal.getPep() : false;
        this.occupation = null;
        this.personaType = this.isTaxId(leadProposal.getCpfCnpj());
        this.companyType = leadProposal.getCompanyType();
        this.createdAd = this.convert.covertLocalDataTimeToDate(leadProposal.getCreatedAt());
        this.proponentType = ProponentType.PRINCIPAL;
        this.leadProposal = leadProposal;
        this.financialInstitutionCode = leadProposal.getFinancialInstitutionCode()
                != null ? leadProposal.getFinancialInstitutionCode() : null;
        this.accountBranch = leadProposal.getAccountBranch() != null ? leadProposal.getAccountBranch() : null;
        this.accountNumber = leadProposal.getAccountNumber() != null ? leadProposal.getAccountNumber() : null;
        this.accountDigit = leadProposal.getAccountDigit() != null ? leadProposal.getAccountDigit() : null;
    }

    @Override
    public void checkedPartner(Partner partner) {
        this.name = partner.getName();
        this.email = partner.getEmail();
        this.phone = partner.getTelephone();
        this.cpfCnpj = partner.getCpfCnpj();
        this.personaType = this.isTaxId(partner.getCpfCnpj());
        this.createdAd = this.convert.covertLocalDataTimeToDate(partner.getCreatedAt());
        this.financialInstitutionCode = partner.getFinancialInstitutionCode()
                != null ? partner.getFinancialInstitutionCode() : null;
        this.accountBranch = partner.getAccountBranch() != null ? partner.getAccountBranch() : null;
        this.accountNumber = partner.getAccountNumber() != null ? partner.getAccountNumber() : null;
        this.accountDigit = partner.getAccountDigit() != null ? partner.getAccountDigit() : null;
    }

    @Override
    public void checkedFinder(Finder finder) {
        this.name = finder.getName();
        this.email = finder.getEmail();
        this.phone = finder.getTelephone();
        this.cpfCnpj = finder.getCpf();
        this.personaType = this.isTaxId(finder.getCpf());
        this.createdAd = this.convert.covertLocalDataTimeToDate(finder.getCreatedAt());
        this.financialInstitutionCode = finder.getFinancialInstitutionCode()
                != null ? finder.getFinancialInstitutionCode() : null;
        this.accountBranch = finder.getAccountBranch() != null ? finder.getAccountBranch() : null;
        this.accountNumber = finder.getAccountNumber() != null ? finder.getAccountNumber() : null;
        this.accountDigit = finder.getAccountDigit() != null ? finder.getAccountDigit() : null;
    }

    @Override
    public void checkedInvestor(Investor investor) {
        this.name = investor.getName();
        this.email = investor.getEmail();
        this.phone = investor.getTelephone();
        this.cpfCnpj = investor.getCnpj();
        this.personaType = this.isTaxId(investor.getCnpj());
        this.createdAd = this.convert.covertLocalDataTimeToDate(investor.getCreatedAt());

    }

    @Override
    public void checkedUser(User user) {
        this.name = user.getName();
        this.cpfCnpj = user.getCpf();
        this.personaType = this.isTaxId(user.getCpf());
        this.createdAd = this.convert.covertLocalDataTimeToDate(user.getCreatedAt());
    }

    @Override
    public void checkedPersona(Persona persona) {
        this.cpfCnpj = persona.getCpfCnpj();
        this.email = persona.getEmail();
        this.phone = persona.getTelephone();
        this.maritalStatus = persona.getMaritalStatus();
        this.localDate = persona.getBirthDate();
        this.rg = persona.getRg();
        this.orgaoEmissor = persona.getOrgaoEmissor();
        this.nacionality = persona.getNationality();
        this.motherName = persona.getMotherName();
        this.citiZenship = persona.getCitizenship();
        this.isPep = persona.getPep();
        this.occupation = persona.getOccupation();
        this.personaType = this.isTaxId(persona.getCpfCnpj());
        this.createdAd = persona.getCreatedAt();
        this.financialInstitutionCode = persona.getFinancialInstitutionCode()
                != null ? persona.getFinancialInstitutionCode() : null;
        this.accountBranch = persona.getAccountBranch() != null ? persona.getAccountBranch() : null;
        this.accountNumber = persona.getAccountNumber() != null ? persona.getAccountNumber() : null;
        this.accountDigit = persona.getAccountDigit() != null ? persona.getAccountDigit() : null;

    }

    @Override
    public void updateLeadProposal(LeadProposal leadProposal) {

    }

    @Override
    public void updatePartner(Partner partner) {

    }

    @Override
    public void updateFinder(Finder finder) {

    }

    @Override
    public void updateInvestor(Investor investor) {

    }

    @Override
    public void updatekedUser(User user) {

    }

    @Override
    public void updatePersona(Persona persona) {

    }


    @Override
    public Persona createdPersona(Lead lead, LeadProposal leadProposal,
                                  Partner partner, Finder finder, Investor investor,
                                  User user, Persona persona) {

        if(lead != null)
            this.checkedLead(lead);
        if(leadProposal != null)
            this.checkedLeadProposal(leadProposal);
        if(partner != null)
            this.checkedPartner(partner);
        if(finder != null)
            this.checkedFinder(finder);
        if(investor != null)
            this.checkedInvestor(investor);
        if(user != null)
            this.checkedUser(user);
        if(persona != null)
            this.checkedPersona(persona);

        Persona newPersona = new Persona();

        newPersona.setCpfCnpj(this.cpfCnpj);
        newPersona.setMaritalStatus(this.maritalStatus);
        newPersona.setBirthDate(this.localDate);
        newPersona.setRg(this.rg);
        newPersona.setOrgaoEmissor(this.orgaoEmissor);
        newPersona.setNationality(this.nacionality);
        newPersona.setMotherName(this.motherName);
        newPersona.setCitizenship(this.citiZenship);
        newPersona.setPep(this.isPep);
        newPersona.setOccupation(this.occupation);
        newPersona.setPersonaType(this.personaType);

        if (newPersona.getPersonaType().equals(PersonaType.NATURAL_PERSON))
            newPersona.setName(this.name.toUpperCase());

        if(newPersona.getPersonaType().equals(PersonaType.LEGAL_PERSON)){
            newPersona.setName(this.name.toUpperCase());
            Company company = new Company();
            company.setFantasyName(this.name.toUpperCase());
            company.setCorporateName(this.name.toUpperCase());
            company.setType(this.companyType);
            company.setCnae(this.cnae);
            newPersona.setCompanyData(company);
        }
        if (financialInstitutionCode != null) {
            newPersona.getBankAccounts().add(
                    this.create.createAccount(
                            financialInstitutionCode, accountBranch,
                           accountNumber, accountDigit, this.createdAd));
        }
        if (address != null) {
            newPersona.getAddresses().add(
                    this.create.createAddress(address, this.convert.covertDate(this.createdAd), newPersona));
        }
        if (email != null)
            newPersona.getContacts().add(this.create.createEmail(email,this.createdAd));

        if (phone != null) {
            Phone phoneData = new Phone();
            phoneData.setNumber(phone);
            phoneData.setIsWhatsApp(Boolean.FALSE);
            newPersona.getPhones().add(this.create.createPhone(phoneData, this.createdAd));
        }
        return newPersona;
    }

    @Override
    public ProposalProponent createdProponent(Persona persona) {
             ProposalProponent proponent = null;
            ProposalProponent verifyProponent = null;
            if(persona.getProposal() != null){
                verifyProponent = this.proposalProponentRepository
                        .virifyProponent(persona.getCpfCnpj(),
                                persona.getProposal().getId(), persona.getProponentType());
            }
            if(verifyProponent == null){
                proponent = this.create.createProponent(
                        persona, persona.getCreatedAt(), persona.getProponentType());

                if(persona.getComposeIncome() != null){
                    if (persona.getComposeIncome().equals(Boolean.TRUE)) {
                        proponent.setComposeIncome(Boolean.TRUE);
                        proponent.setMonthlyIncome(persona.getMonthlyIncome()
                                != null ? persona.getMonthlyIncome() : BigDecimal.ZERO);
                    }
                }
                if (persona.getParticipationPercentage() != null) {
                    if (persona.getParticipationPercentage() != 0) {
                        proponent.setPercentageOfCommitment(
                                persona.getParticipationPercentage() == null
                                        ? 0 : persona.getParticipationPercentage());
                    }
                }
                proponent.setPersona(persona);
                proponent.setProposal(persona.getProposal());
            }
        return proponent;
    }

    @Override
    public Boolean notNullPhone(List list) {
        return null;
    }

    @Override
    public Boolean notNullEmail(List list) {
        return null;
    }

    @Override
    public Boolean notNullAccount(List list) {
        return null;
    }

    @Override
    public Boolean notNullAddress(List list) {
        return null;
    }
}
