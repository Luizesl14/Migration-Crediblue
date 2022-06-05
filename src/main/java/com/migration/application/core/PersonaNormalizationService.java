package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.domain.Proposal;
import com.migration.domain.ProposalProponent;
import com.migration.domain.enums.MaritalStatus;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.enums.ProponentType;
import com.migration.domain.enums.TypeRegimeCompanion;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.*;
import com.migration.infrastructure.IPersonaRepository;
import com.migration.infrastructure.IProposalProponentRepository;
import com.migration.infrastructure.IProposalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class PersonaNormalizationService {

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private ConvertLocalDataTime convert;

    @Autowired
    private CreateObject create;

    @Autowired
    private IProposalProponentRepository proposalProponentRepository;

    @Autowired
    private LeadProposalService leadProposalService;

    @Autowired
    private CompanionService companionService;

    @Autowired
    private SimulationService simulationService;

    public void findAll() {
        List<Persona> oldPersonas = this.personaRepository.findAll();
        System.out.println("Quantidade de Old - Personas do banco: " + oldPersonas.size());

        this.normalization(oldPersonas);
        this.normalizedProponent();
        this.leadProposalService.findAll();
        this.companionService.createCompanion();
        this.simulationService.findAll();
    }

    public  Boolean normalization(List<Persona> oldPersonas){
        List<Persona> notRepeatedPersonas = oldPersonas.stream().distinct().toList();
        this.updatePersonas(notRepeatedPersonas);
        return  Boolean.TRUE;
    }

    public Boolean updatePersonas (List<Persona> oldPersonas) {
        for (Persona oldPersona: oldPersonas) {
                    if(oldPersona.getProponentType() ==  null)
                        oldPersona.setProponentType(ProponentType.COMPANY_PARTNER);

                    if(oldPersona.getPersonaType() ==  null)
                        oldPersona.setPersonaType(PersonaType.LEGAL_PERSON);

                    if(oldPersona.getCpfCnpj() != null){
                        oldPersona.setPersonaType(
                                oldPersona.getCpfCnpj()
                                        .length() == 11 ? PersonaType.NATURAL_PERSON: PersonaType.LEGAL_PERSON);
                    }
                    if(oldPersona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
                        oldPersona.setName(oldPersona.getName());
                    }

                    if(oldPersona.getCpfCnpj() != null)
                        oldPersona.setTaxId(oldPersona.getCpfCnpj());

                    if(oldPersona.getFinancialInstitutionCode() != null){
                        List<PersonaAccounts> personaAccountsList = new ArrayList<>();
                        PersonaAccounts personaAccounts =  this.create.createAccount(
                                oldPersona.getFinancialInstitutionCode(),oldPersona.getAccountBranch(),
                                oldPersona.getAccountNumber(), oldPersona.getAccountDigit(), oldPersona.getCreatedAt());
                        personaAccountsList.add(personaAccounts);
                        oldPersona.setBankAccounts(personaAccountsList);
                    }
                    if(oldPersona.getAddress() != null){
                        List<PersonaAddress> personaAddressList = new ArrayList<>();
                        PersonaAddress personaAddress = this.create.createAddress(oldPersona.getAddress(), oldPersona.getAddress().getCreatedAt());
                        personaAddressList.add(personaAddress);
                        oldPersona.setAddresses(personaAddressList);

                    }
                    if(oldPersona.getEmail() != null){
                        List<ContactEmail> contactEmailList = new ArrayList<>();
                        ContactEmail contactEmail = this.create.createEmail(oldPersona.getEmail(), oldPersona.getCreatedAt());
                        contactEmailList.add(contactEmail);
                        oldPersona.setContacts(contactEmailList);
                    }
                    if(oldPersona.getTelephone() != null){
                        Phone phone = new Phone();
                        phone.setNumber(oldPersona.getTelephone());
                        phone.setIsWhatsApp(Boolean.FALSE);
                        List<PersonaPhone> personaPhoneList = new ArrayList<>();
                        PersonaPhone personaPhone = this.create.createPhone(phone, oldPersona.getCreatedAt());
                        personaPhoneList.add(personaPhone);
                        oldPersona.setPhones(personaPhoneList);
                    }

                    if(oldPersona.getPersonaType().equals(PersonaType.LEGAL_PERSON)){
                        Company company = new Company();
                        company.setCorporateName(oldPersona.getName());
                        if(oldPersona.getOpeningDate() != null){
                            company.setFoundationDate(this.convert.convertToLocalDate(oldPersona.getOpeningDate()));
                        }
                        if(oldPersona.getOpeningDate()!= null){
                            company.setFoundationDate(
                                    this.convert.convertToLocalDate(oldPersona.getOpeningDate()));
                        }
                        oldPersona.setCompanyData(company);
                        this.print(oldPersona);
                    }
                    this.personaRepository.save(oldPersona);
                }
        return  Boolean.TRUE;
    }


    public void normalizedProponent(){
        List<Persona> proponents = this.personaRepository.findAll();
        System.out.println("Numero total de proponentes: " + proponents.size());
        int index = 0;
        for (Persona persona: proponents) {
            System.out.println();
            if(persona.getCpfCnpj() != null){
                System.out.println("Proponent a ser normalizado:  " + persona.getName());
                Persona personaSave = this.personaRepository.findByTaxId(persona.getCpfCnpj());

                if(personaSave.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
                    System.out.println(" ## ID ##: " + personaSave.getId()
                            + " ####### PESSOA JÁ EXISTE ######## ** PF ** : "+ personaSave.getName());
                }else{
                    System.out.println(" ## ID ##: " + persona.getId()
                            + " ####### PESSOA JÁ EXISTE ######## ** PJ ** : " + personaSave.getCompanyData().getCorporateName());
                }
                this.createProponent(personaSave, persona.getProposal());
                index ++;
                System.out.println("Total de proponents Normalizados: " + index);
            }
        }
    }

    public void createProponent(Persona persona, Proposal proposal){
            ProposalProponent proponent = this.create.createProponent(persona, persona.getCreatedAt(), persona.getProponentType());
            if(persona.getSourceIncome() != null){
                proponent.setComposeIncome(Boolean.TRUE);
                proponent.setMonthlyIncome(persona.getMonthlyIncome());
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
    }

    public void print(Persona persona){
        System.out.println();
        System.out.println(" ##### #### ###  ## # Persona Atualizado para novo padrão "
                + persona.getId() + " " + persona.getName());
        if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
            System.out.println(" ## ID ##: " + persona.getId()
                    + " Persona Atualizado ** PF ** : "+ persona.getName());
        }else{
            System.out.println(" ## ID ##: " + persona.getId()
                    + " Persona Atualizado ** PJ ** : " + persona.getCompanyData().getCorporateName());
        }
    }
}
