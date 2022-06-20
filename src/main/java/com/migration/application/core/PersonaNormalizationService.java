package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.domain.ProposalProponent;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.enums.ProponentType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.*;
import com.migration.infrastructure.IPersonaRepository;
import com.migration.infrastructure.IProposalProponentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Autowired
    private PersonaDocumentService documentService;

    @Autowired
    private LeadProposalDocumentService leadProposalDocumentService;

    @Autowired

    private UserService userService;

    @Autowired
    private InvestorService investorService;

    @Autowired
    private FinderService finderService;

    @Autowired
    private LeadService leadService;

    @Autowired
    private PartnerService partnerService;


    public void findAll() {

//        this.createProponent();
//        this.documentService.findAll();
//        this.leadProposalService.findAll();
//        this.leadProposalDocumentService.findAll();
//        this.updatePersonas();
//        this.updatePersonaType();
//        this.normalizedProponent();
//        this.companionService.createCompanion();
//        this.leadService.findAll();
//        this.simulationService.findAll();
//        this.partnerService.findAll();
//        this.finderService.findAll();
//        this.investorService.findAll();
//        this.userService.findAll();

    }

    public void createProponent(){
        List<Persona> proponents = this.personaRepository.findAll();
        System.out.println("Quantidade de Old - Personas do banco: " + proponents.size());
        int index = 0;
        List<ProposalProponent> proposalProponents = new ArrayList<>();
        for (Persona persona: proponents) {
            ProposalProponent verifyProponent = null;
            if(persona.getProposal() != null){
               verifyProponent = this.proposalProponentRepository
                       .virifyProponent(persona.getCpfCnpj(),persona.getProposal().getId(), persona.getProponentType());
            }

           if(verifyProponent == null){
               ProposalProponent proponent = this.create.createProponent(persona, persona.getCreatedAt(), persona.getProponentType());
               if(persona.getComposeIncome() != null){
                   if (persona.getComposeIncome().equals(Boolean.TRUE)) {
                       proponent.setComposeIncome(Boolean.TRUE);
                       proponent.setMonthlyIncome(persona.getMonthlyIncome() != null ? persona.getMonthlyIncome() : BigDecimal.ZERO);
                   }
               }
               if (persona.getParticipationPercentage() != null) {
                   if (persona.getParticipationPercentage() != 0) {
                       proponent.setPercentageOfCommitment(
                               persona.getParticipationPercentage() == null ? 0 : persona.getParticipationPercentage());
                   }
               }
               proponent.setPersona(persona);
               proponent.setProposal(persona.getProposal());
               proposalProponents.add(proponent);
               System.out.println("INDEX PROPONENTE SECUNDARIO CRIADO: " + index++);
           }
        }
        this.proposalProponentRepository.saveAll(proposalProponents);
    }


    public Boolean updatePersonas() {
        List<Persona> oldPersonas = this.personaRepository.findPropeonenSegundariosNormalized();
        List<Persona> notRepeatedPersonas = oldPersonas.stream().distinct().collect(Collectors.toList());
        System.out.println(" ## QUANTIDADE DE PERSONAS NORMALIZADOS ##: " + notRepeatedPersonas.size());

        List<Persona> personas = new ArrayList<>();
        int index = 0;
        for (Persona oldPersona: notRepeatedPersonas) {
              if(oldPersona.getTaxId() == null){
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
                      oldPersona.setName(oldPersona.getName().toUpperCase());
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
                      PersonaAddress personaAddress =
                              this.create.createAddress(oldPersona.getAddress(), oldPersona.getAddress().getCreatedAt(), oldPersona);
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
                      if(oldPersona.getName() != null){
                          company.setFantasyName(oldPersona.getName().toUpperCase());
                          company.setCorporateName(oldPersona.getName().toUpperCase());
                      }else{
                          company.setFantasyName(oldPersona.getCompanyData().getFantasyName().toUpperCase());
                          company.setCorporateName(oldPersona.getCompanyData().getFantasyName().toUpperCase());
                      }

                      if(oldPersona.getOpeningDate() != null)
                          company.setFoundationDate(this.convert.convertToLocalDate(oldPersona.getOpeningDate()));

                      if(oldPersona.getOpeningDate()!= null)
                          company.setFoundationDate(this.convert.convertToLocalDate(oldPersona.getOpeningDate()));

                      oldPersona.setCompanyData(company);
                  }
                  personas.add(oldPersona);
                  System.out.println("QUANTIDADE DE PERSONAS NORMALIZADOS: " + index++);
              }
        }
        this.personaRepository.saveAll(personas);

        return  Boolean.TRUE;
    }

    public void updatePersonaType() {
        List<Persona> personasDatabase = this.personaRepository.personaType();
        int index = 0;
        for (Persona personaDatabase: personasDatabase) {
            if(personaDatabase.getTaxId() != null){
                if(personaDatabase.getPersonaType() != null && personaDatabase.getTaxId().length() <= 11)
                    personaDatabase.setPersonaType(PersonaType.NATURAL_PERSON);
                if(personaDatabase.getPersonaType() != null && personaDatabase.getTaxId().length() > 11)
                    personaDatabase.setPersonaType(PersonaType.LEGAL_PERSON);
                this.personaRepository.save(personaDatabase);
                System.out.println("QUANTIDADE DE PERSONATYPE NORMALIZADOS: " + index++);
            }
        }
    }


    public void normalizedProponent(){
        List<ProposalProponent> proponents = this.proposalProponentRepository.findAll();
        System.out.println("TOTALDE PROPONENTS NO BANCO: " + proponents.size());
        List<ProposalProponent> proposalProponentList = new ArrayList<>();
        int index = 0;
        for (ProposalProponent proponent: proponents) {
            System.out.println();
            if(proponent.getPersona().getCpfCnpj() != null){
                Persona personaSave = this.personaRepository.findByTaxId(proponent.getPersona().getCpfCnpj());
                if(personaSave != null){
                    proponent.setPersona(personaSave);
                    proposalProponentList.add(proponent);
                }
                index ++;
                System.out.println("TOTAL DE PROPONENTS NORMALIZADOS: " + index);
            }
        }
        this.proposalProponentRepository.saveAll(proposalProponentList);
    }
}
