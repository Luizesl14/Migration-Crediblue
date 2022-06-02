package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.domain.ProposalProponent;
import com.migration.domain.enums.BankAccount;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.enums.ProponentType;
import com.migration.domain.enums.TypeRegimeCompanion;
import com.migration.domain.persona.Companion;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.*;
import com.migration.infrastructure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class PersonaService {

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private IPersonaMigrationRepository personaMigrationRepository;

    @Autowired
    private GenericObjectMapper mapper;

    @Autowired
    private IPersonaAccountRepository iPersonaAccountRepository;

    @Autowired
    private IPersonaAddressRepository iPersonaAddressRepository;

    @Autowired
    private IPersonaPhoneRepository iPersonaPhoneRepository;

    @Autowired
    private IContactEmailRepository iContactEmailRepository;


    @Autowired
    private ConvertLocalDataTime convert;

    @Autowired
    private CreateObject create;

    @Autowired
    private IProposalProponentRepository proposalProponentRepository;


    public void findAll() {
        List<Persona> oldPersonas = this.personaRepository.findAll();
        System.out.println("Quantidade de Old - Personas do banco: " + oldPersonas.size());

//        this.saveProponent(oldPersonas);
//        this.updatePersonas(oldPersonas);
//        this.createCompanion(oldPersonas);
    }


    public  Boolean saveProponent(List<Persona> oldPersonas){
        for ( Persona persona: oldPersonas) {
            ProposalProponent proponent = this.create.createProponent(persona, persona.getProponentType());
            if(persona.getSourceIncome() != null){
                proponent.setComposeIncome(Boolean.TRUE);
                proponent.setMonthlyIncome(persona.getMonthlyIncome());
            }
            if(persona.getProponentType() != null){
                if(persona.getProponentType().equals(ProponentType.PRINCIPAL)){
                    proponent.setScrConsulted(persona.getProposal().getLeadProposal().getScrConsulted());
                }
            }
            if(persona.getParticipationPercentage() != 0){
                proponent.setPercentageOfCommitment(persona.getParticipationPercentage());
            }
            ProposalProponent proposalProponentSaved = this.proposalProponentRepository.save(proponent);
            proposalProponentSaved.setPersona(persona);
            proposalProponentSaved.setProposal(persona.getProposal());
            this.proposalProponentRepository.save(proposalProponentSaved);

            System.out.println(" ## ID ##: " + persona.getId() + " Proponent Salvo ** PF ** : "+ persona.getName());
        }
        return Boolean.TRUE;
    }



    public Boolean updatePersonas (List<Persona> oldPersonas) {
        for (Persona oldPersona: oldPersonas) {
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
            }
            this.personaRepository.save(oldPersona);
            System.out.println(" ##### #### ###  ## # Persona Atualizado para novo padrão " + oldPersona.getId() + " " + oldPersona.getName());

            if(oldPersona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
                System.out.println(" ## ID ##: " + oldPersona.getId() + " Persona Atualizado ** PF ** : "+ oldPersona.getName());
            }else{
                System.out.println(" ## ID ##: " + oldPersona.getId() + " Persona Atualizado ** PJ ** : " + oldPersona.getCompanyData().getCorporateName());
            }
            }
        return  Boolean.TRUE;
    }




    public Boolean createCompanion (List<Persona> oldPersonas) {

        for (Persona oldPersona: oldPersonas) {
            if (oldPersona.getCompanion() != null) {
                List<Persona> personaSave = this.personaRepository.findAllByTaxId(oldPersona.getCompanion().getCpf());
                PersonaCompanion personaCompanion = new PersonaCompanion();
                Persona newPerson = new Persona();

                if (personaSave == null && oldPersona.getCompanion().getName() != null) {
                    newPerson.setName(oldPersona.getCompanion().getName() != null ? oldPersona.getCompanion().getName() : null);
                    newPerson.setNationality(oldPersona.getCompanion().getNationality() != null ? oldPersona.getCompanion().getNationality() : null);
                    newPerson.setOccupation(oldPersona.getCompanion().getOccupation() != null ? oldPersona.getCompanion().getOccupation() : null);
                    newPerson.setRg(oldPersona.getCompanion().getRg() != null ? oldPersona.getCompanion().getRg() : null);
                    newPerson.setOrgaoEmissor(oldPersona.getCompanion().getOrgaoEmissor() != null ? oldPersona.getCompanion().getOrgaoEmissor() : null);
                    newPerson.setTaxId(oldPersona.getCompanion().getCpf() != null ? oldPersona.getCompanion().getCpf() : null);
                    if (newPerson.getPersonaType() != null) {
                        newPerson.setPersonaType(PersonaType.NATURAL_PERSON);
                    }
                    if (oldPersona.getCompanion().getEmail() != null) {
                        newPerson.getContacts().add(
                                this.create.createEmail(oldPersona.getCompanion().getEmail(), null));
                    }
                    newPerson.setMotherName(oldPersona.getCompanion().getMotherName() != null ? oldPersona.getCompanion().getMotherName() : null);
                    newPerson.setBirthDate(oldPersona.getCompanion().getBirthDate() != null ? oldPersona.getCompanion().getBirthDate() : null);
                    newPerson.setPep(oldPersona.getCompanion().getPep());

                    personaCompanion.setType(this.createType(oldPersona));
                    personaCompanion.setData(newPerson);
                    this.personaRepository.save(oldPersona);
                    oldPersona.setPersonaCompanionId(personaCompanion);
                    System.out.println("################## Companio salvo " + oldPersona.getCompanion().getName());
                    System.out.println(" <<<<<< Companion criado >>>>> : " + oldPersona.getCompanion().getName());
                } else if (personaSave != null && !personaSave.isEmpty()) {
                    List<Persona> personaNormalized = personaSave
                            .stream().filter(p -> !p.getProponentType().equals(ProponentType.SPOUSE)).toList();

                    if(personaNormalized != null  && !personaNormalized.isEmpty()){
                        personaCompanion.setData(personaNormalized.get(0));
                        personaCompanion.setType(this.createType(personaSave.get(0)));
                        oldPersona.setPersonaCompanionId(personaCompanion);
                        this.personaRepository.save(oldPersona);
                        System.out.println("################## Companio salvo " + oldPersona.getCompanion().getName());
                    }
                }
            }
        }
        return Boolean.TRUE;
    }


    public TypeRegimeCompanion createType(Persona persona){
        if(persona.getPropertySystem() != null){
            if (persona.getPropertySystem().name().equals(TypeRegimeCompanion.PARTIAL_COMMUNION.name())) {
                return TypeRegimeCompanion.PARTIAL_COMMUNION;

            } else if (persona.getPropertySystem().name().equals(TypeRegimeCompanion.TOTAL_SEPARATION.name())) {
                return  TypeRegimeCompanion.TOTAL_SEPARATION;

            } else if (persona.getPropertySystem().name().equals(TypeRegimeCompanion.UNIVERSIAL_COMMUNION.name())) {
                return TypeRegimeCompanion.UNIVERSIAL_COMMUNION;

            } else if (persona.getPropertySystem().name().equals(TypeRegimeCompanion.FINAL_PARTICIPATION_IN_AQUESTOS.name())) {
                return TypeRegimeCompanion.FINAL_PARTICIPATION_IN_AQUESTOS;
            }
        }
        return null;
    }


}
