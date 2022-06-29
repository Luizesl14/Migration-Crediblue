package com.migration.application.core;

import com.migration.application.core.config.GenericObjectMapper;
import com.migration.application.core.config.IcreateProponent;
import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.application.shared.ExistsEntity;
import com.migration.domain.*;
import com.migration.domain.enums.*;
import com.migration.domain.persona.Companion;
import com.migration.domain.persona.CreditAnalysis;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.PersonaMigration;
import com.migration.domain.persona.aggregation.*;
import com.migration.infrastructure.*;
import org.apache.commons.text.WordUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Transactional
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

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private  IInvestorRepository investorRepository;

    @Autowired
    private IFinderRespository finderRespository;

    @Autowired
    private ILeadProposalRespository leadProposalRepository;

    @Autowired
    private ILeadRepository leadRepository;

    @Autowired
    private ISimulatonRepository simulatonRepository;

    @Autowired
    private IPersonaDocumentRepository personaDocumentRepository;

    @Autowired
    private ILeadProposalDocumentRepository leadProposalDocumentRepository;

    @Autowired
    private  ICreditAnalysisDocumentRepository creditAnalysisDocumentRepository;

    @Autowired
    private ICreditAnalysisRepository creditAnalysisRepository;

    @Autowired
    private ExistsEntity existsEntity;

    @Autowired
    private  IBalanceSheetRepository balanceSheetRepository;

    @Autowired
    private  IincomeStatementRepository iincomeStatementRepository;

    @Autowired
    private ICreditAnalysisProponentRepository creditAnalysisProponentRepository;

    @Autowired
    private ICreditAnalysisComparativeRepository creditAnalysisComparativeRepository;

    @Autowired
    private PersonaNormalizationService personaNormalizationService;

    @Autowired
    private IPersonaMigrationRepository personaMigrationRepository;

    @Autowired
    private IPersonaAddressRepository personaAddressRepository;

    @Autowired
    private IContactEmailRepository contactEmailRepository;

    @Autowired
    private GenericObjectMapper mapper;

    @Autowired
    private IPersonaAccountRepository personaAccountRepository;

    @Autowired
    private CreateObject created;

    private final static Logger LOGGER = Logger.getLogger(Start.class.getName());

    private Persona personaTransient;


    @Override
    public void satartApplication() {
        this.adjust();
//        this.goThroughProponent();
//        this.migrationPersonasNewEntity();
//        this.normalizedPersona();
//        this.saveLeadProposalByPerosnaMigration();
//        this.goThroughProponentPrincipal();
//        this.normaliedProponentPrincipal();
//        this.goThroughAnalysisBalanceAndIncome();
//        this.goThroughLeadDocument();
//        this.goThroughPersonaDocument();
//        this.goThroughCreditAnalysisDocument();

//        this.createdCompanionByPersona();
//        this.normalizedEntityCpfAndCnpjIsNull();
//        this.goToUser();
//        this.goToLead();
//        this.goThroughSimulation();
//        this.goToFinder();
//        this.goToInvestor();
//        this.goToPartner();

    }

    @Override
    public void goThroughProponent() {     // CREATED PROPONENT SECUNDARIO
        List<Persona> personasDatabase = this.personaRepository.findAll();
        List<ProposalProponent> proponents = new ArrayList<>();

        personasDatabase.forEach(persona -> {
            ProposalProponent proponent = this.createdProponent(persona);
            proponent.setOldPersona(persona);
            proponent.setCompanion(persona.getCompanion() != null ? persona.getCompanion() : null);
            proponents.add(proponent);
        });
        this.proposalProponentRepository.saveAll(proponents);
        LOGGER.log(Level.INFO, "TOTAL DE PROPONENTS {0}", proponents.size());
        System.out.println();
    }

    @Override
    public void migrationPersonasNewEntity() {  // MIGRATION PERSONA PARA PERSONAMIGRATION
        List<Persona> personaDatabase = this.personaRepository.findAll();
        List<PersonaMigration> migration = new ArrayList<>();

        personaDatabase.forEach(persona -> {
            PersonaMigration personaMigration;
            personaMigration =  this.mapper.mapTo(persona, PersonaMigration.class);
            personaMigration.setId(null);
            personaMigration.setPersona(persona);
            migration.add(personaMigration);
        });

        this.personaMigrationRepository.saveAll(migration);
        LOGGER.log(Level.INFO, "TOTAL DE PERSONAS {0}", personaDatabase.size());
        LOGGER.log(Level.INFO, "MIGRATION PERSONA TO PERSONA ENTITY {0}", migration.size());
    }


    @Override
    public void normalizedPersona() {   // SAVE PERSONA MIGRATION NORMALIZED  PERSONAS AND CREATED PROPONENT
        List<PersonaMigration> personas = this.personaMigrationRepository.findPropeonenSegundariosNormalized();

        List<PersonaMigration> personaMigrationDistinct = personas.stream().distinct().collect(Collectors.toList());

        personaMigrationDistinct.forEach(personaMigration-> {
            Persona persona = this.mapper.mapTo(personaMigration, Persona.class);
            Persona updatePersona = this.createdPersona(
                    null,null, null, null, null, null, persona);
            this.personaRepository.save(updatePersona);
        });

        LOGGER.log(Level.INFO, "TOTAL DE PERSONAS NO BANCO {0}", personas.size());

        //NORMALIZED PROPONENT
        List<ProposalProponent> proposalProponents = this.proposalProponentRepository.findAll();
        List<ProposalProponent> proponents = new ArrayList<>();

        proposalProponents.forEach(proponent -> {
            Persona persona = this.personaRepository.findByTaxId(proponent.getPersona().getCpfCnpj());
            if(persona != null){
                proponent.setPersona(persona);
                proponents.add(proponent);
            }
        });
        this.proposalProponentRepository.saveAll(proponents);
        LOGGER.log(Level.INFO, "TOTAL DE PROPONENTS {0}", proponents.size());
        System.out.println();
    }

    @Override
    public void saveLeadProposalByPerosnaMigration() {  // SAVE LEAD PROPOSAL TO PROPOSAL MIGRATION
        List<LeadProposal> leadProposals = this.leadProposalRepository.findAll();
        List<PersonaMigration> migration = new ArrayList<>();
        leadProposals.forEach(leadProposal-> {
            Persona updatePersona = this.createdPersona(
                    null,leadProposal, null, null, null, null, null);
            PersonaMigration personaMigration = this.mapper.mapTo(updatePersona, PersonaMigration.class);
            personaMigration.setLeadProposal(leadProposal);
            if(leadProposal.getScrConsulted() != null)
            personaMigration.setIsConsultedScr(leadProposal.getScrConsulted());

            personaMigration.setCnaeCode(leadProposal.getCnaeCode()!= null ? leadProposal.getCnaeCode(): null);
            personaMigration.setTypeRegimeCompanion(leadProposal.getTypeRegimeCompanion());
            personaMigration.setSpouseName(leadProposal.getSpouseName() != null ? leadProposal.getSpouseName() : null);
            personaMigration.setMonthlyIncome(leadProposal.getFamilyIncome() != null ? leadProposal.getFamilyIncome() : BigDecimal.ZERO);
            personaMigration.setOpeningDate(this.convert.convertToLocalDateViaMilisecond(leadProposal.getCompanyFoundingDate()));
            personaMigration.setCorporateType(leadProposal.getCorporateType() != null ? leadProposal.getCorporateType() : null);
            migration.add(personaMigration);
        });
        this.personaMigrationRepository.saveAll(migration);
        LOGGER.log(Level.INFO, "TOTAL DE LEAD PORPOSAL NO BANCO {0}", leadProposals.size());
        LOGGER.log(Level.INFO, "TOTAL DE LEAD PORPOSAL SALVO PERSONA MIGRATION {0}", migration.size());
    }

    public void adjust(){

//        List<Finder> finderList = this. finderRespository.findNameNameIsNull();
//        List<Finder> personFinders = new ArrayList<>();
//
//        finderList.forEach(finder -> {
//                if (finder.getFinancialInstitutionCode() != null) {
//                    PersonaAccounts personaAccounts = new PersonaAccounts();
//                    BankAccount account = new BankAccount();
//                    account.setFinancialInstitutionCode(finder.getFinancialInstitutionCode());
//                    account.setAccountBranch(finder.getAccountBranch());
//                    account.setAccountNumber(finder.getAccountNumber());
//                    account.setAccountDigit(finder.getAccountDigit());
//                    personaAccounts.setAccount(account);
//                    personaAccounts.setCreatedAt(this.convert.covertLocalDataTimeToDate(finder.getCreatedAt()));
//                    personaAccounts.setPrincipal(Boolean.TRUE);
//                    personaAccounts.setPersona(finder.getPersona());
//                    personaAccounts.setType(BankAccountType.CC_PF);
//                    finder.getPersona().getBankAccounts().add(personaAccounts);
//                }
//            finder.setName(finder.getPersona().getName());
//
//            personFinders.add(finder);
//            System.out.println("ID > : " + finder.getId() + " finder-name: " + finder.getPersona().getName());
//        });
//
//        this.finderRespository.saveAll(personFinders);



        List<ProposalProponent> proposalProponents = this.proposalProponentRepository.findAllLeadPrincipal();
        List<Persona> personas = new ArrayList<>();

        proposalProponents.forEach(proponent -> {
            if(proponent.getPersona().getMonthlyIncome() == null && proponent.getLeadProposal().getFamilyIncome() != null)
            proponent.getPersona().setMonthlyIncome(proponent.getLeadProposal().getFamilyIncome());

            if(proponent.getPersona().getMaritalStatus() == null && proponent.getLeadProposal().getMaritalStatus() != null)
            proponent.getPersona().setMaritalStatus(proponent.getLeadProposal().getMaritalStatus());

            if(proponent.getPersona().getBirthDate() == null && proponent.getLeadProposal().getBirthDate() != null)
            proponent.getPersona().setBirthDate(proponent.getLeadProposal().getBirthDate());

            if(proponent.getPersona().getRg() == null && proponent.getLeadProposal().getRg() != null)
            proponent.getPersona().setRg(proponent.getLeadProposal().getRg());

            if(proponent.getPersona().getOrgaoEmissor() == null && proponent.getLeadProposal().getOrgaoEmissor() != null)
            proponent.getPersona().setOrgaoEmissor(proponent.getLeadProposal().getOrgaoEmissor());

            if(proponent.getPersona().getNationality() ==  null && proponent.getLeadProposal().getNationality() != null )
            proponent.getPersona().setNationality( proponent.getLeadProposal().getNationality());

            if(proponent.getPersona().getMotherName() ==  null && proponent.getLeadProposal().getMother() != null )
            proponent.getPersona().setMotherName( proponent.getLeadProposal().getMother());

            if(proponent.getPersona().getCitizenship() ==  null && proponent.getLeadProposal().getCitizenship() != null )
            proponent.getPersona().setCitizenship(proponent.getLeadProposal().getCitizenship());
            proponent.getPersona().setPep(
                    proponent.getLeadProposal().getPep().equals(Boolean.TRUE) ? Boolean.TRUE : Boolean.FALSE);

            if(proponent.getPersona().getOccupation() ==  null
                    && proponent.getLeadProposal().getOccupation() != null )
            proponent.getPersona().setOccupation(proponent.getLeadProposal().getOccupation());

            if(proponent.getPersona().getCompanyData() != null){
                if(proponent.getPersona().getCompanyData().getFoundationDate() ==  null
                        && proponent.getLeadProposal().getCompanyFoundingDate() != null ){
                    proponent.getPersona().getCompanyData().setFoundationDate( proponent.getLeadProposal().getCompanyFoundingDate());
                }
                if(proponent.getPersona().getCompanyData().getCnae() ==  null
                        && proponent.getLeadProposal().getCnaeCode() != null ){
                    proponent.getPersona().getCompanyData().setCnae( proponent.getLeadProposal().getCnaeCode());
                }
                if(proponent.getPersona().getCompanyData().getType() ==  null
                        && proponent.getLeadProposal().getCompanyType()!= null ){
                    proponent.getPersona().getCompanyData().setType( proponent.getLeadProposal().getCompanyType());
                }

            }

            if(proponent.getLeadProposal().getEmail()!= null){
                if(proponent.getPersona().getContacts() != null && !proponent.getPersona().getContacts().isEmpty()){
                    proponent.getPersona().getContacts().forEach(contactEmail -> {
                        if(contactEmail.getEmail() == null)
                            contactEmail.setEmail(proponent.getLeadProposal().getEmail());

                        if(!contactEmail.getEmail().equals(proponent.getLeadProposal().getEmail())){
                            contactEmail.setEmail(proponent.getLeadProposal().getEmail());
                            contactEmail.setPersona(proponent.getPersona());
                            contactEmail.setType(EmailType.PERSONAL);
                            contactEmail.setCreatedAt(this.convert.covertLocalDataTimeToDate(proponent.getLeadProposal().getCreatedAt()));
                            contactEmail.setPrincipal(Boolean.TRUE);
                        }
                    });
                }else{
                  ContactEmail contactEmail = new ContactEmail();
                  contactEmail.setEmail(proponent.getLeadProposal().getEmail());
                  contactEmail.setPersona(proponent.getPersona());
                  contactEmail.setType(EmailType.PERSONAL);
                  contactEmail.setCreatedAt(this.convert.covertLocalDataTimeToDate(proponent.getLeadProposal().getCreatedAt()));
                  contactEmail.setPrincipal(Boolean.TRUE);
                }

            }

            if(proponent.getLeadProposal().getAddress() != null){
                if(proponent.getPersona().getAddresses() != null && !proponent.getPersona().getAddresses().isEmpty()){
                    proponent.getPersona().getAddresses().forEach(personaAddress -> {
                        personaAddress.setData(proponent.getLeadProposal().getAddress());
                        personaAddress.setPersona(proponent.getPersona());
                        personaAddress.setPrincipal(Boolean.TRUE);
                        personaAddress.setType(AddressType.RESIDENTIAL);
                    });
                }else{
                    PersonaAddress personaAddress =  new PersonaAddress();
                    personaAddress.setData(proponent.getLeadProposal().getAddress());
                    personaAddress.setPersona(proponent.getPersona());
                    personaAddress.setCreatedAt(this.convert.covertLocalDataTimeToDate(proponent.getLeadProposal().getCreatedAt()));
                    personaAddress.setType(AddressType.RESIDENTIAL);
                    proponent.getPersona().getAddresses().add(personaAddress);
                }

            }

            if (proponent.getLeadProposal().getFinancialInstitutionCode() != null) {
                if(proponent.getPersona().getBankAccounts() != null && !proponent.getPersona().getBankAccounts().isEmpty()){
                    proponent.getPersona().getBankAccounts().forEach(accounts -> {
                        accounts.getAccount().setFinancialInstitutionCode(proponent.getLeadProposal().getFinancialInstitutionCode());
                        accounts.getAccount().setAccountNumber(proponent.getLeadProposal().getAccountBranch() != null ? proponent.getLeadProposal().getAccountBranch() : null);
                        accounts.getAccount().setAccountBranch(proponent.getLeadProposal().getAccountBranch() != null ? proponent.getLeadProposal().getAccountBranch() : null);
                        accounts.getAccount().setAccountNumber(proponent.getLeadProposal().getAccountDigit() != null ? proponent.getLeadProposal().getAccountDigit(): null);
                        accounts.setPrincipal(Boolean.TRUE);
                    });
                }else{
                    PersonaAccounts personaAccounts = new PersonaAccounts();
                    BankAccount account = new BankAccount();
                    account.setFinancialInstitutionCode(proponent.getLeadProposal().getFinancialInstitutionCode());
                    account.setAccountBranch(proponent.getLeadProposal().getAccountBranch() != null ? proponent.getLeadProposal().getAccountBranch() : null);
                    account.setAccountNumber(proponent.getLeadProposal().getAccountNumber() != null ? proponent.getLeadProposal().getAccountNumber(): null);
                    account.setAccountDigit(proponent.getLeadProposal().getAccountDigit() != null ? proponent.getLeadProposal().getAccountDigit(): null);
                    personaAccounts.setAccount(account);

                    if(proponent.getLeadProposal().getCreatedAt() != null)
                        personaAccounts.setCreatedAt(this.convert.covertLocalDataTimeToDate(proponent.getLeadProposal().getCreatedAt()));

                    personaAccounts.setPrincipal(Boolean.TRUE);
                    personaAccounts.setPersona(proponent.getPersona());
                    if(proponent.getPersona().getPersonaType() == null){
                        proponent.getPersona().setPersonaType(
                                proponent.getPersona().getTaxId()
                                        .length() == 11 ? PersonaType.NATURAL_PERSON: PersonaType.LEGAL_PERSON);
                    }
                    if(proponent.getPersona().getPersonaType().equals(PersonaType.NATURAL_PERSON))
                        personaAccounts.setType(BankAccountType.CC_PF);
                    if(proponent.getPersona().getPersonaType().equals(PersonaType.LEGAL_PERSON))
                        personaAccounts.setType(BankAccountType.CC_PJ);
                    proponent.getPersona().getBankAccounts().add(personaAccounts);
                }
            }
            proponent.getPersona().setLeadProposal(proponent.getLeadProposal());
            personas.add(proponent.getPersona());
        });

            this.personaRepository.saveAll(personas);

    }

    @Override
    public void goThroughProponentPrincipal() {  // CRIANDO PROPONENT PRINCIPAL
        List<ProposalProponent> mainProponents = new ArrayList<>();
        List<PersonaMigration> personasDatabase = this.personaMigrationRepository.findPropeonenmMainNormalized();
        personasDatabase.forEach(personaMigration -> {
            ProposalProponent proponent = new ProposalProponent();
            proponent.setSpouseName(personaMigration.getSpouseName() != null ? personaMigration.getSpouseName() :null);
            if(personaMigration.getIsConsultedScr() != null){
                proponent.setScrConsulted(personaMigration.getIsConsultedScr());
            }
            proponent.setPersonaMigration(personaMigration);
            proponent.setScrConsulted(personaMigration.getIsConsultedScr() != null ? personaMigration.getIsConsultedScr() : false);
            proponent.setLeadProposal(personaMigration.getLeadProposal());
            proponent.setProposal(proponent.getLeadProposal().getProposal());
            proponent.setType(ProponentType.PRINCIPAL);
            mainProponents.add(proponent);
        });

        this.proposalProponentRepository.saveAll(mainProponents);
        LOGGER.log(Level.INFO, "TOTAL DE PROPONENTS PRINCIPAIS {0}", mainProponents.size());
    }

    @Override
    public void normaliedProponentPrincipal() {  // CRIANDO PROPONENT PRINCIPAL
        List<PersonaMigration> personas = this.personaMigrationRepository.findPropeonenmMainNormalized();

        List<PersonaMigration> distinct = personas.stream().distinct().collect(Collectors.toList());

        distinct.forEach(personaMigration-> {
            Persona personaDatabase = this.personaRepository.findByTaxId(personaMigration.getCpfCnpj());

            if(personaDatabase == null){
                Persona persona = this.mapper.mapTo(personaMigration, Persona.class);
                Persona updatePersona = this.createdPersona(
                        null,null, null, null, null, null, persona);

                if(personaMigration.getCnaeCode() != null)
                updatePersona.getCompanyData().setCnae(personaMigration.getCnaeCode());

                if(personaMigration.getOpeningDate()!= null)
                    updatePersona.getCompanyData().setFoundationDate(this.convert.convertToLocalDate(personaMigration.getOpeningDate()));

                if(personaMigration.getIsConsultedScr() != null)
                    updatePersona.setIsConsultedScr(personaMigration.getIsConsultedScr());

                if(personaMigration.getOpeningDate() != null)
                    updatePersona.setOpeningDate(personaMigration.getOpeningDate());

                this.personaRepository.save(updatePersona);
            }
        });

        LOGGER.log(Level.INFO, "TOTAL DE PERSONAS NO BANCO {0}", personas.size());

        //NORMALIZED PROPONENT
        List<ProposalProponent> proposalProponents = this.proposalProponentRepository.findAllPersonaByMain();
        List<ProposalProponent> proponents = new ArrayList<>();

        proposalProponents.forEach(proponent -> {
            Persona persona = this.personaRepository.findByTaxId(proponent.getPersonaMigration().getCpfCnpj());
            if(persona != null){
                proponent.setPersona(persona);
                proponents.add(proponent);
            }else{
                proponent.setPersona(this.mapper.mapTo(proponent.getPersonaMigration(), Persona.class));
                proponents.add(proponent);
            }
        });
        this.proposalProponentRepository.saveAll(proponents);
        LOGGER.log(Level.INFO, "TOTAL DE PROPONENTS {0}", proponents.size());
        System.out.println();
    }


    @Override
    public void createdCompanionByPersona() {
        List<ProposalProponent> proposalProponents = this.proposalProponentRepository.findByCompanionId();

        List<Persona> personasNormalized = new ArrayList<>();

        proposalProponents.forEach(proponent -> {
            if(proponent.getCompanion() != null){
                Companion companion = proponent.getCompanion();
                if (companion.getCpf() != null) {
                    Persona personaDatabase = this.personaRepository.findByTaxId(companion.getCpf());
                    PersonaCompanion personaCompanion = new PersonaCompanion();
                    Persona newPerson = new Persona();

                    if (personaDatabase == null) {
                        newPerson.setName(companion.getName() != null ? companion.getName() : null);
                        newPerson.setNationality(companion.getNationality() != null ? companion.getNationality() : null);
                        newPerson.setOccupation(companion.getOccupation() != null ? companion.getOccupation() : null);
                        newPerson.setRg(companion.getRg() != null ? companion.getRg() : null);
                        newPerson.setOrgaoEmissor(companion.getOrgaoEmissor() != null ? companion.getOrgaoEmissor() : null);
                        newPerson.setTaxId(companion.getCpf() != null ? companion.getCpf() : null);

                        if (proponent.getPersona().getMaritalStatus()!= null) {
                            newPerson.setMaritalStatus(proponent.getMaritalStatus());
                        }
                        newPerson.setPersonaType(PersonaType.NATURAL_PERSON);

                        if (companion.getEmail() != null) {
                            newPerson.getContacts().add(
                                    this.create.createEmail(companion.getEmail(), null));
                        }
                        newPerson.setMotherName(companion.getMotherName() != null ? companion.getMotherName() : null);
                        newPerson.setBirthDate(companion.getBirthDate() != null ? companion.getBirthDate() : null);
                        newPerson.setPep(companion.getPep());
                        personaCompanion.setType(proponent.getOldPersona().getPropertySystem() != null ? this.regimeType(proponent.getOldPersona().getPropertySystem()): null);
                        personaCompanion.setData(newPerson);
                        proponent.getPersona().setPersonaCompanionId(personaCompanion);
                        personasNormalized.add(proponent.getPersona());

                    } else {
                        personaCompanion.setData(personaDatabase);
                        personaCompanion.setType(proponent.getTypeRegimeCompanion() != null ? proponent.getTypeRegimeCompanion(): null);
                        proponent.getPersona().setPersonaCompanionId(personaCompanion);
                        personasNormalized.add(proponent.getPersona());
                    }
                }
            }
        });
        System.out.println();
        this.personaRepository.saveAll(personasNormalized);
        LOGGER.log(Level.INFO, "TOTAL DE COMPANIONS CRIADOS PARA PERSONAS NO BANCO {0}", personasNormalized.size());
    }

    @Override
    public void goToUser() {
        List<User> users = this.userRepository.findAll();
        List<User> usersNormalized = new ArrayList<>();
        users.forEach(u->{
            Persona personaExists = this.personaRepository.
                    findAllByTaxId(u.getCpf());
            if(personaExists != null){
                Persona createdPersona = this.createdPersona(
                        null,null, null, null, null, u, null);
                u.setPersona(createdPersona);
                this.updatedUser(u, personaExists);
            }else{
                Persona createdPersona = this.createdPersona(
                        null,null, null, null, null, u, null);
                u.setPersona(createdPersona);
                usersNormalized.add(u);
            }
        });

        this.userRepository.saveAll(usersNormalized);

        System.out.println();
        LOGGER.log(Level.INFO, "TOTAL DE USERS NO BANCO {0}", users.size());
        LOGGER.log(Level.INFO, "TOTAL DE USERS NORMALIZED {0}", usersNormalized.size());
    }

    @Override
    public void goToLead() {
        List<Lead> leads = this.leadRepository.findAll();
        List<Lead> leadsNormalized = new ArrayList<>();
        leads.forEach(l->{
            Persona personaExists = this.personaRepository.
                    findAllByTaxId(l.getCpfCnpj());
            if(personaExists != null){
                Persona createdPersona = this.createdPersona(
                        l,null, null, null, null, null, null);
                l.setPersona(createdPersona);
                this.updateLead(l, personaExists);
            }else{
                Persona createdPersona = this.createdPersona(
                        l,null, null, null, null, null, null);
                l.setPersona(createdPersona);
                leadsNormalized.add(l);
            }
        });

        this.leadRepository.saveAll(leadsNormalized);

        LOGGER.log(Level.INFO, "TOTAL DE LEADS NO BANCO {0}", leads.size());
        LOGGER.log(Level.INFO, "TOTAL DE LEADS NORMALIZED {0}", leadsNormalized.size());
        System.out.println();

    }

    @Override
    public void goToFinder() {
        List<Finder> finders = this.finderRespository.findAll();
        List<Finder> findersNormalized = new ArrayList<>();
        finders.forEach(finder -> {
            Persona personaExists = this.personaRepository.
                    findAllByTaxId(finder.getCpf());

            if(personaExists != null){
                Persona createdPersona = this.createdPersona(
                        null,null, null, finder, null, null, null);
                finder.setPersona(createdPersona);
                this.updateFinder(finder, personaExists);
            }else{
                Persona createdPersona = this.createdPersona(
                        null,null, null, finder, null, null, null);
                finder.setPersona(createdPersona);
                findersNormalized.add(finder);
            }
        });

        this.finderRespository.saveAll(findersNormalized);

        LOGGER.log(Level.INFO, "TOTAL DE FINDERS NO BANCO {0}", finders.size());
        LOGGER.log(Level.INFO, "TOTAL DE FINDERS NORMALIZED {0}", findersNormalized.size());
        System.out.println();
    }

    @Override
    public void goToInvestor() {
        List<Investor> investors = this.investorRepository.findAll();
        List<Investor> investorNormalized = new ArrayList<>();
        investors.forEach(i->{
            Persona personaExists = this.personaRepository.
                    findAllByTaxId(i.getCnpj());
            if(personaExists != null){
                Persona createdPersona = this.createdPersona(
                        null,null, null, null, i, null, null);
                i.setPersona(createdPersona);
                this.updateInvestor(i, personaExists);
            }else{
                Persona createdPersona = this.createdPersona(
                        null,null, null, null, i, null, null);
                i.setPersona(createdPersona);
                investorNormalized.add(i);
            }
        });

        this.investorRepository.saveAll(investorNormalized);

        LOGGER.log(Level.INFO, "TOTAL DE INVESTOR NO BANCO {0}", investors.size());
        LOGGER.log(Level.INFO, "TOTAL DE INVESTOR NORMALIZED {0}", investorNormalized.size());

    }

    @Override
    public void goToPartner() {
        List<Partner> partners = this.partnerRepository.findAll();
        List<Partner> partnerNormalized = new ArrayList<>();
        partners.forEach(p-> {
            Persona personaExists = this.personaRepository.
                    findAllByTaxId(p.getCpfCnpj());

            if(personaExists != null){
                Persona createdPersona = this.createdPersona(
                        null,null, p, null, null, null, null);
                p.setPersona(createdPersona);
                this.updatePartner(p, personaExists);
            }else{
                Persona createdPersona = this.createdPersona(
                        null,null, p, null, null, null, null);
                p.setPersona(createdPersona);
                partnerNormalized.add(p);
            }
        });

        this.partnerRepository.saveAll(partnerNormalized);

        LOGGER.log(Level.INFO, "TOTAL DE PARCEIROS NO BANCO {0}", partners.size());
        LOGGER.log(Level.INFO, "TOTAL DE PARCEIROS NORMALIZED {0}", partnerNormalized.size());
        System.out.println();
    }

    @Override
    public void normalizedEntityCpfAndCnpjIsNull() {

        List<User> usersPartner = this.userRepository.findByRoleRole("ADMIN_PARTNER");
        List<User> usersPartnerNormalized = new ArrayList<>();
        usersPartner.forEach(user -> {
            user.setCpf(user.getPartner().getCpfCnpj());
            usersPartnerNormalized.add(user);
        });

        List<User> usersInvestor = this.userRepository.findByRoleRole("INVESTOR");
        List<User> usersInvestorNormalized = new ArrayList<>();
        usersInvestor.forEach(user -> {
            user.setCpf(user.getInvestor().getCnpj());
            usersInvestorNormalized.add(user);
        });

        List<User> usersNull = this.userRepository.findByUserCpfNull();
        List<User> usersNullNormalized = new ArrayList<>();
        usersNull.forEach(user -> {
            String token = UUID.randomUUID().toString().toUpperCase(Locale.ROOT);
            user.setCpf(token.substring(0,8));
            usersNullNormalized.add(user);
        });

        List<User> usersIsEmpty = this.userRepository.findByUserIsEmpity();
        List<User> usersIsEmptyNormalized = new ArrayList<>();
        usersIsEmpty.forEach(user -> {
            String token = UUID.randomUUID().toString().toUpperCase(Locale.ROOT);
            user.setCpf(token.substring(0,8));
            usersIsEmptyNormalized.add(user);
        });


        List<Partner> partnerNull = this.partnerRepository.findByPartnerTaxIdIsNull();
        List<Partner> partnerNullNormalized = new ArrayList<>();

        partnerNull.forEach(partner -> {
            String token = UUID.randomUUID().toString().toUpperCase(Locale.ROOT);
            partner.setCpfCnpj(token.substring(0,8));
            partnerNullNormalized.add(partner);
        });

        this.userRepository.saveAll(usersPartnerNormalized);
        this.userRepository.saveAll(usersInvestorNormalized);
        this.userRepository.saveAll(usersNullNormalized);
        this.userRepository.saveAll(usersIsEmptyNormalized);
        this.partnerRepository.saveAll(partnerNullNormalized);

        LOGGER.log(Level.INFO, "TOTAL DE USUARIOS NORMALIZADOS NO BANCO {0}", usersPartner.size());
        LOGGER.log(Level.INFO, "TOTAL DE USUARIOS DO PARCEIRO NORMALIZADOS {0}", usersPartnerNormalized.size());

        LOGGER.log(Level.INFO, "TOTAL DE USUARIOS DO INVESTIDOR NO BANCO {0}", usersInvestor.size());
        LOGGER.log(Level.INFO, "TOTAL DE USUARIOS  INVESTIDOR  NORMALIZADOS {0}", usersInvestorNormalized.size());

    }

    @Override
    public void goThroughSimulation() {
        List<Simulation> simulations = this.simulatonRepository.findAll();
        List<Simulation> normalizedSimulation = new ArrayList<>();
        simulations.forEach(simulation -> {
               simulation.setPersona(simulation.getLead().getPersona());
                this.simulatonRepository.save(simulation);
        });
        this.simulatonRepository.saveAll(normalizedSimulation);

        LOGGER.log(Level.INFO, "TOTAL DE SIMULATION NO BANCO {0}", simulations.size());
        LOGGER.log(Level.INFO, "TOTAL DE SIMULATION NORMALIZED {0}", normalizedSimulation.size());
    }


    @Override
    public Partner updatePartner(Partner partner, Persona personaDatabse) {

            if(personaDatabse != null && partner.getPersona() != null){
                personaDatabse.setName(
                        partner.getName() != null ? partner.getName().toUpperCase() : null);

                if(personaDatabse.getPersonaType().equals(PersonaType.LEGAL_PERSON)){
                    personaDatabse.getCompanyData().setFantasyName(partner.getName().toUpperCase());
                    personaDatabse.getCompanyData().setCorporateName(partner.getName().toUpperCase());
                }
                BeanUtils.copyProperties(partner.getPersona(), personaDatabse, "id", "createdAt");
                Persona personaSave = this.personaRepository.save(personaDatabse);
                partner.setPersona(personaSave);
                 return partner;
            }else{
                return partner;
            }
    }

    @Override
    public Lead updateLead(Lead lead, Persona personaDatabse) {

        if(personaDatabse != null){

            personaDatabse.setName(
                    personaDatabse.getName() != null ? lead.getName().toUpperCase() : null);

            if(personaDatabse.getPersonaType().equals(PersonaType.LEGAL_PERSON)){
                personaDatabse.getCompanyData().setFantasyName(lead.getName().toUpperCase());
                personaDatabse.getCompanyData().setCorporateName(lead.getName().toUpperCase());
            }
            BeanUtils.copyProperties(lead.getPersona(), personaDatabse, "id", "createdAt");
            Persona personaSave = this.personaRepository.save(personaDatabse);
            lead.setPersona(personaSave);
            return lead;
        }else{
            return lead;
        }
    }

    @Override
    public Finder updateFinder(Finder finder, Persona personaDatabse) {
        if(personaDatabse != null){
            personaDatabse.setName(
                    personaDatabse.getName() != null ? finder.getName().toUpperCase() : null);

            if(personaDatabse.getPersonaType().equals(PersonaType.LEGAL_PERSON)){
                personaDatabse.getCompanyData().setFantasyName(finder.getName().toUpperCase());
                personaDatabse.getCompanyData().setCorporateName(finder.getName().toUpperCase());
            }
            BeanUtils.copyProperties(finder.getPersona(), personaDatabse, "id", "createdAt");
            Persona personaSave = this.personaRepository.save(personaDatabse);
            finder.setPersona(personaSave);
              return finder;
            }else{
            return finder;
        }
    }

    @Override
    public Investor updateInvestor(Investor investor, Persona personaDatabse) {

        if(personaDatabse != null){
            personaDatabse.setName(
                    personaDatabse.getName() != null ? investor.getName().toUpperCase() : null);

            if(personaDatabse.getPersonaType().equals(PersonaType.LEGAL_PERSON)){
                personaDatabse.getCompanyData().setFantasyName(investor.getName().toUpperCase());
                personaDatabse.getCompanyData().setCorporateName(investor.getName().toUpperCase());
            }
            investor.setPersona(personaDatabse);
            return investor;
        }else{
            return investor;
        }
    }

    @Override
    public User updatedUser(User user,Persona personaDatabse) {
            if(personaDatabse != null){
                personaDatabse.setName(
                        user.getName() != null ? user.getName().toUpperCase() : null);

                if(personaDatabse.getPersonaType().equals(PersonaType.LEGAL_PERSON)){
                    personaDatabse.getCompanyData().setFantasyName(user.getName().toUpperCase());
                    personaDatabse.getCompanyData().setCorporateName(user.getName().toUpperCase());
                }
                BeanUtils.copyProperties(user.getPersona(), personaDatabse, "id", "createdAt");
                Persona personaSave = this.personaRepository.save(personaDatabse);
                user.setPersona(personaSave);
            }else{
                return user;
            }
            return null;
    }

    @Override
    public void goThroughPersonaDocument() {
        List<PersonaDocument> personaDocuments = this.personaDocumentRepository.findAll();
        List<PersonaDocument> personaDocumentsNormalized = new ArrayList<>();

        personaDocuments.forEach(personaDocument -> {
            if(personaDocument.getPersona() != null && personaDocument.getPersona().getProposal() != null) {

                ProposalProponent proposalProponent =
                        this.proposalProponentRepository.findAllDByProposalByPersona(
                                personaDocument.getPersona().getId(),
                                personaDocument.getPersona().getProposal().getId());

                if (proposalProponent != null) {
                    personaDocument.setProponent(proposalProponent);
                    personaDocumentsNormalized.add(personaDocument);
                }
            }
        });
        this.personaDocumentRepository.saveAll(personaDocumentsNormalized);
        LOGGER.log(Level.INFO, "TOTAL DE DOCUMENTOS PERSONA PERSONAS NO BANCO {0}", personaDocuments.size());
        LOGGER.log(Level.INFO, "TOTAL DE DOCUMENTOS PERSONA PERSONAS NORMALIZADAS {0}", personaDocuments.size());
    }

    @Override
    public void goThroughLeadDocument() {
        List<LeadProposalDocument> documents = this.leadProposalDocumentRepository.findAll();
        List<LeadProposalDocument> documentsNormalized = new ArrayList<>();
        documents.forEach(document -> {
            if(document.getLeadProposal()  != null && document.getLeadProposal().getProposal() != null){

                ProposalProponent proposalProponent  =
                        this.proposalProponentRepository.findAllByProposalByLeadProposal(
                                document.getLeadProposal().getId(),
                                document.getLeadProposal().getProposal().getId());

                if(proposalProponent != null) {
                    document.setProponent(proposalProponent);
                    documentsNormalized.add(document);
                }
            }
        });

        List<LeadProposalDocument> leadProposalDocuments = this.leadProposalDocumentRepository.saveAll(documentsNormalized);
        List<PersonaDocument> migrationDocuments = new ArrayList<>();

        leadProposalDocuments.forEach(document -> {
            PersonaDocument  personaDocument = new PersonaDocument();
            personaDocument.setProponent(document.getProponent());
            personaDocument.setDocument(document.getDocument());
            migrationDocuments.add(personaDocument);
        });

        this.personaDocumentRepository.saveAll(migrationDocuments);
        LOGGER.log(Level.INFO, "TOTAL DE DOCUMENTOS LEAD PROPOSAL NO BANCO {0}", documents.size());
        LOGGER.log(Level.INFO, "TOTAL DE DOCUMENTOS LEAD PROPOSA NORMALIZADAS {0}", documentsNormalized.size());
    }

    @Override
    public void goThroughCreditAnalysisDocument() {
        List<CreditAnalysisDocuement> creditAnalysisDocuements = this.creditAnalysisDocumentRepository.findAll();
        List<CreditAnalysisDocuement> creditAnalysisDocuementsNormalized = new ArrayList<>();

        creditAnalysisDocuements.forEach(docuement -> {
            if(docuement.getCreditAnalysisId().getProposal() != null) {

                ProposalProponent proposalProponent =
                        this.proposalProponentRepository.findAllByProposalByLeadProposal(
                                docuement.getCreditAnalysisId().getProposal().getLeadProposal().getId(),
                                docuement.getCreditAnalysisId().getProposal().getId());

                if (proposalProponent != null) {
                    docuement.setProponent(proposalProponent);
                    creditAnalysisDocuementsNormalized.add(docuement);
                }
            }
        });
        this.creditAnalysisDocumentRepository.saveAll(creditAnalysisDocuementsNormalized);
        LOGGER.log(Level.INFO, "TOTAL DE DOCUMENTOS PERSONA PERSONAS NO BANCO {0}", creditAnalysisDocuements.size());
        LOGGER.log(Level.INFO, "TOTAL DE DOCUMENTOS PERSONA PERSONAS NORMALIZADAS {0}", creditAnalysisDocuementsNormalized.size());

    }

    @Override
    public void goThroughAnalysisBalanceAndIncome() {
        List<CreditAnalysis>  creditsAnalyses = this.creditAnalysisRepository.findAll();
        List<BalanceSheet> balanceSheetList = new ArrayList<>();
        List<IncomeStatement> incomeStatements = new ArrayList<>();
        creditsAnalyses.forEach(analysis -> {

            ProposalProponent proponent =
                    this.proposalProponentRepository
                            .findAllByProposalByLeadProposalMain(analysis.getProposal().getLeadProposal().getId());

            CreditAnalysisProponent creditAnalysisProponent = new CreditAnalysisProponent();
            creditAnalysisProponent.setCreditAnalysis(analysis);

            CreditAnalysisComparative creditAnalysisComparativeSaved =
                    this.creditAnalysisComparativeRepository.save(new CreditAnalysisComparative());

            creditAnalysisProponent.setAnalysisComparative(creditAnalysisComparativeSaved);

            CreditAnalysisProponent creditAnalysisProponentSaved =
                    this.creditAnalysisProponentRepository.save(creditAnalysisProponent);

            creditAnalysisProponentSaved.setIncome(
                    analysis.getIncome() != null ? analysis.getIncome(): BigDecimal.ZERO);

            creditAnalysisProponentSaved.setFinancialCommitment(
                    analysis.getFinancialCommitment() != null ? analysis.getFinancialCommitment() : BigDecimal.ZERO);

            creditAnalysisProponentSaved.setSerasaRiskScore(
                    analysis.getSerasaRiskScore() != null ? analysis.getSerasaRiskScore() : null);

            creditAnalysisProponentSaved.setScrScore(
                    analysis.getScrScore() != null ? analysis.getScrScore() : null);

            creditAnalysisProponentSaved.setDueDiligence(
                    analysis.getDueDiligence() != null ? analysis.getDueDiligence() : null);

            creditAnalysisProponentSaved.setTotalIncome(
                    analysis.getTotalIncome() != null ? analysis.getTotalIncome() : null);

            creditAnalysisProponentSaved.setTotalFinancialCommitment(
                    analysis.getTotalFinancialCommitment() != null ? analysis.getTotalFinancialCommitment() : null);

            creditAnalysisProponentSaved.setTotalCommitment(
                    analysis.getTotalCommitment() != null ? analysis.getTotalCommitment() : null);

            creditAnalysisProponentSaved.setMmaCommitmentFinancialScr(
                    analysis.getFinancialCommitment() != null ? analysis.getFinancialCommitment() : BigDecimal.ZERO);

                creditAnalysisProponentSaved.setProponent(proponent);

            this.creditAnalysisProponentRepository.save(creditAnalysisProponentSaved);

            if(!analysis.getBalanceSheets().isEmpty()) {
                analysis.getBalanceSheets().forEach(balanceSheet -> {
                    BalanceSheet balanceSheet02;
                    balanceSheet02 = balanceSheet;
                    balanceSheet02.setCreditAnalysisComparative(creditAnalysisComparativeSaved);
                    balanceSheetList.add(balanceSheet02);
                });
            }
            if(!analysis.getIncomeStatements().isEmpty()){
                analysis.getIncomeStatements().forEach(incomeStatement -> {
                    IncomeStatement incomeStatement2;
                    incomeStatement2 = incomeStatement;
                    incomeStatement2.setCreditAnalysisComparative(creditAnalysisComparativeSaved);
                    incomeStatements.add(incomeStatement2);
                });
            }

        });

        this.iincomeStatementRepository.saveAll(incomeStatements);
        this.balanceSheetRepository.saveAll(balanceSheetList);

        LOGGER.log(Level.INFO, "TOTAL DE BALANCESHEETLIST NORMALIZADAS {0}", balanceSheetList.size());
        LOGGER.log(Level.INFO, "TOTAL DE INCOMESTATEMENTS NORMALIZADAS {0}", incomeStatements.size());

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


    @Override
    public PersonaType isTaxId(String taxId) {
        if(taxId.length() <= 11)
            return PersonaType.NATURAL_PERSON;
        return PersonaType.LEGAL_PERSON;
    }


    @Override
    public Persona createdPersona(Lead lead, LeadProposal leadProposal,
                                  Partner partner, Finder finder, Investor investor,
                                  User user, Persona persona) {

        Persona newPersona = new Persona();
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
        if(persona != null){
            this.checkedPersona(persona);
        }
        newPersona.setCpfCnpj(personaTransient.getCpfCnpj() != null ? personaTransient.getCpfCnpj() : null);
        newPersona.setMaritalStatus(personaTransient.getMaritalStatus() != null ? personaTransient.getMaritalStatus() : null);
        newPersona.setBirthDate(personaTransient.getBirthDate() != null ? personaTransient.getBirthDate() : null);
        newPersona.setRg(personaTransient.getRg() != null ? personaTransient.getRg() : null );
        newPersona.setOrgaoEmissor(personaTransient.getOrgaoEmissor() != null ? personaTransient.getOrgaoEmissor() : null);
        newPersona.setNationality(personaTransient.getNationality() != null ? personaTransient.getNationality() : null);
        newPersona.setMotherName(personaTransient.getMotherName() != null ? personaTransient.getMotherName() : null);
        newPersona.setCitizenship(personaTransient.getCitizenship() != null ? personaTransient.getCitizenship() : null);
        newPersona.setTaxId(personaTransient.getTaxId() != null ? personaTransient.getTaxId() : null);

        if(personaTransient.getPep() != null)
        newPersona.setPep(personaTransient.getPep().equals(Boolean.FALSE) ? Boolean.FALSE : Boolean.TRUE);

        newPersona.setOccupation(personaTransient.getOccupation() != null ? personaTransient.getOccupation() : null);
        newPersona.setPersonaType(personaTransient.getPersonaType() != null ? personaTransient.getPersonaType() : null);
        newPersona.setProponentType(personaTransient.getProponentType() != null ? personaTransient.getProponentType() : null);
        newPersona.setCreatedAt(personaTransient.getCreatedAt());
        newPersona.setProposal(personaTransient.getProposal() != null ? personaTransient.getProposal() : null);
        newPersona.setSourceIncome(personaTransient.getSourceIncome() != null ? personaTransient.getSourceIncome() : null);
        newPersona.setCompanion(personaTransient.getCompanion() != null ? personaTransient.getCompanion() : null);
        newPersona.setLegalRepresentative(personaTransient.getLegalRepresentative() != null ? personaTransient.getLegalRepresentative() : null);
        newPersona.setOpeningDate(personaTransient.getOpeningDate() != null ? personaTransient.getOpeningDate() : null);
        newPersona.setEmail(personaTransient.getEmail() != null ? personaTransient.getEmail() : null);
        newPersona.setProposal(personaTransient.getProposal() != null ? personaTransient.getProposal() : null);
        newPersona.setMonthlyIncome(personaTransient.getMonthlyIncome() != null ? personaTransient.getMonthlyIncome(): BigDecimal.ZERO);

        if (newPersona.getPersonaType().equals(PersonaType.NATURAL_PERSON))
            newPersona.setName(WordUtils.capitalize(personaTransient.getName()));

        if(newPersona.getPersonaType().equals(PersonaType.LEGAL_PERSON)){
            newPersona.setName(personaTransient.getName().toUpperCase());
            Company company = new Company();
            company.setFantasyName(WordUtils.capitalize(personaTransient.getName()));
            company.setCorporateName(WordUtils.capitalize(personaTransient.getName()));
            company.setType(personaTransient.getCorporateType() != null ? personaTransient.getCorporateType() : null);
            if(personaTransient.getCompanyData() != null){
                company.setFoundationDate(personaTransient.getCompanyData().getFoundationDate()
                        != null ? personaTransient.getCompanyData().getFoundationDate(): null);
                company.setCnae(personaTransient.getCompanyData().getCnae()
                        != null ? personaTransient.getCompanyData().getCnae() : null);
            }
           newPersona.setCompanyData(company);
        }

        if(personaTransient.getCompanion() != null && personaTransient.getPersonaCompanionId().getType() != null){
            PersonaCompanion companion = new PersonaCompanion();
            companion.setType(personaTransient.getPersonaCompanionId().getType());
            newPersona.setPersonaCompanionId(companion);
        }

        if (personaTransient.getFinancialInstitutionCode() != null) {
            newPersona.getBankAccounts().add(
                    this.create.createAccount(
                            personaTransient.getFinancialInstitutionCode(),
                            personaTransient.getAccountBranch() != null ? personaTransient.getAccountBranch() : null,
                            personaTransient.getAccountNumber() != null ? personaTransient.getAccountNumber() : null,
                            personaTransient.getAccountDigit() != null ? personaTransient.getAccountDigit() : null,
                            personaTransient.getCreatedAt()));
        }
        if (personaTransient.getAddress() != null) {
            newPersona.getAddresses().add(
                    this.create.createAddress(personaTransient.getAddress(),
                            this.convert.covertDate(personaTransient.getCreatedAt()), newPersona));
        }
        if (personaTransient.getEmail() != null)
            newPersona.getContacts().add(this.create.createEmail(personaTransient.getEmail(), personaTransient.getCreatedAt()));

        if (personaTransient.getTelephone() != null) {
            Phone phoneData = new Phone();
            phoneData.setNumber(personaTransient.getTelephone());
            phoneData.setIsWhatsApp(Boolean.FALSE);
            newPersona.getPhones().add(this.create.createPhone(phoneData, personaTransient.getCreatedAt()));
        }
        return newPersona;
    }

    @Override
    public TypeRegimeCompanion regimeType(PropertySystem system) {
        TypeRegimeCompanion[] regimeTypeList = TypeRegimeCompanion.values();
        for(TypeRegimeCompanion regimeType : regimeTypeList){
            if(system.name().equals(regimeType.name()))
                return regimeType;
        }
       return  null;
    }

    @Override
    public ProposalProponent createdProponent(Persona persona) {

            ProposalProponent proponent = this.create.createProponent(
                    persona, persona.getCreatedAt(), persona.getProponentType());
            if(persona.getComposeIncome() != null
                    && persona.getComposeIncome().equals(Boolean.TRUE)){
                    proponent.setComposeIncome(Boolean.TRUE);
                    proponent.setMonthlyIncome(persona.getMonthlyIncome()
                            != null ? persona.getMonthlyIncome() : BigDecimal.ZERO);
            }
            proponent.setScrConsulted(persona.getIsConsultedScr());
            if (persona.getParticipationPercentage() != null && persona.getParticipationPercentage() != 0) {
                    proponent.setPercentageOfCommitment(
                            persona.getParticipationPercentage() == null ? 0 : persona.getParticipationPercentage());
            }
            proponent.setPersona(persona);
            proponent.setProposal(persona.getProposal());

        return proponent;
    }

    @Override
    public void checkedLead(Lead lead) {
        personaTransient = new Persona();
        personaTransient.setName(lead.getName());
        personaTransient.setEmail(lead.getEmail()); ;
        personaTransient.setBirthDate(lead.getBirthDate() != null ? lead.getBirthDate() : null);
        personaTransient.setTelephone(lead.getTelephone());
        personaTransient.setTaxId(lead.getCpfCnpj());
        personaTransient.setMaritalStatus(lead.getMaritalStatus() != null ? lead.getMaritalStatus(): null);
        personaTransient.setBirthDate(lead.getBirthDate());
        personaTransient.setCreatedAt(this.convert.covertLocalDataTimeToDate(lead.getCreatedAt()));
        personaTransient.setPersonaType(this.isTaxId(lead.getCpfCnpj()));
    }

    @Override
    public void checkedLeadProposal(LeadProposal leadProposal) {
        personaTransient = new Persona();
        Company company = new Company();
        company.setFoundationDate(leadProposal.getCompanyFoundingDate() != null ? leadProposal.getCompanyFoundingDate() : null);
        company.setCnae(leadProposal.getCnaeCode() != null ? leadProposal.getCnaeCode(): null);
        company.setType(leadProposal.getCorporateType());
        personaTransient.setCompanyData(company);

        PersonaCompanion companion = new PersonaCompanion();
        companion.setType(leadProposal.getTypeRegimeCompanion() != null ? leadProposal.getTypeRegimeCompanion() : null);
        personaTransient.setPersonaCompanionId(companion);

        personaTransient.setProposal(leadProposal.getProposal());
        personaTransient.setLeadProposal(leadProposal);
        personaTransient.setCpfCnpj(leadProposal.getCpfCnpj());

        personaTransient.setName(leadProposal.getName());
        personaTransient.setEmail(leadProposal.getEmail());
        personaTransient.setTelephone(leadProposal.getTelephone());
        personaTransient.setCpfCnpj(leadProposal.getCpfCnpj());
        personaTransient.setMaritalStatus(leadProposal.getMaritalStatus());
        personaTransient.setBirthDate(leadProposal.getBirthDate());
        personaTransient.setRg(leadProposal.getRg());
        personaTransient.setOrgaoEmissor(leadProposal.getOrgaoEmissor());
        personaTransient.setNationality(leadProposal.getNationality());
        personaTransient.setMotherName(leadProposal.getMother());
        personaTransient.setCitizenship(leadProposal.getCitizenship());
        personaTransient.setPep(leadProposal.getPep().equals(Boolean.FALSE) ? leadProposal.getPep() : false);
        personaTransient.setOccupation(leadProposal.getOccupation());
        personaTransient.setPersonaType(this.isTaxId(leadProposal.getCpfCnpj()));
        personaTransient.setCorporateType(leadProposal.getCorporateType());
        personaTransient.setCreatedAt(this.convert.covertLocalDataTimeToDate(leadProposal.getCreatedAt()));
        personaTransient.setProponentType(ProponentType.PRINCIPAL);
        personaTransient.setLeadProposal(leadProposal);
        personaTransient.setFinancialInstitutionCode(leadProposal.getFinancialInstitutionCode()
                != null ? leadProposal.getFinancialInstitutionCode() : null);
        personaTransient.setAccountBranch( leadProposal.getAccountBranch() != null ? leadProposal.getAccountBranch() : null);
        personaTransient.setAccountNumber(leadProposal.getAccountNumber() != null ? leadProposal.getAccountNumber() : null);
        personaTransient.setAccountDigit(leadProposal.getAccountDigit() != null ? leadProposal.getAccountDigit() : null);
    }

    @Override
    public void checkedPartner(Partner partner) {
        personaTransient = new Persona();
        personaTransient.setName(partner.getName());
        personaTransient.setEmail(partner.getEmail());
        personaTransient.setTelephone(partner.getTelephone());
        personaTransient.setTaxId(partner.getCpfCnpj());
        personaTransient.setPersonaType(this.isTaxId(partner.getCpfCnpj()));
        personaTransient.setAddress(partner.getAddress());
        personaTransient.setCreatedAt(this.convert.covertLocalDataTimeToDate(partner.getCreatedAt()));
        personaTransient.setFinancialInstitutionCode(partner.getFinancialInstitutionCode()
                != null ? partner.getFinancialInstitutionCode() : null);
        personaTransient.setAccountBranch( partner.getAccountBranch() != null ? partner.getAccountBranch() : null);
        personaTransient.setAccountNumber(partner.getAccountNumber() != null ? partner.getAccountNumber() : null);
        personaTransient.setAccountDigit(partner.getAccountDigit() != null ? partner.getAccountDigit() : null);
    }

    @Override
    public void checkedFinder(Finder finder) {
        personaTransient = new Persona();
        personaTransient.setName(finder.getName());
        personaTransient.setEmail(finder.getEmail());
        personaTransient.setTelephone(finder.getTelephone());
        personaTransient.setTaxId(finder.getCpf());
        personaTransient.setPersonaType(this.isTaxId(finder.getCpf()));
        personaTransient.setCreatedAt(this.convert.covertLocalDataTimeToDate(finder.getCreatedAt()));
        personaTransient.setFinancialInstitutionCode(finder.getFinancialInstitutionCode()
                != null ? finder.getFinancialInstitutionCode() : null);
        personaTransient.setAccountBranch( finder.getAccountBranch() != null ? finder.getAccountBranch() : null);
        personaTransient.setAccountNumber(finder.getAccountNumber() != null ? finder.getAccountNumber() : null);
        personaTransient.setAccountDigit(finder.getAccountDigit() != null ? finder.getAccountDigit() : null);
    }

    @Override
    public void checkedInvestor(Investor investor) {
        personaTransient = new Persona();
        personaTransient.setName(investor.getName());
        personaTransient.setEmail(investor.getEmail());
        personaTransient.setTelephone(investor.getTelephone());
        personaTransient.setTaxId(investor.getCnpj());
        personaTransient.setPersonaType(this.isTaxId(investor.getCnpj()));
        personaTransient.setCreatedAt(this.convert.covertLocalDataTimeToDate(investor.getCreatedAt()));

    }

    @Override
    public void checkedUser(User user) {
        personaTransient = new Persona();
        personaTransient.setName(user.getName());
        personaTransient.setEmail(user.getEmail());
        personaTransient.setTelephone(user.getTelephone());
        personaTransient.setTaxId(user.getCpf());
        personaTransient.setPersonaType(this.isTaxId(user.getCpf()));
        personaTransient.setCreatedAt(this.convert.covertLocalDataTimeToDate(user.getCreatedAt()));
    }

    @Override
    public void checkedPersona(Persona persona) {
        personaTransient = new Persona();
        personaTransient = new Persona();
        Company company = new Company();
        company.setFoundationDate(persona.getOpeningDate() != null ? this.convert.convertToLocalDate(persona.getOpeningDate())  : null);
        company.setType(persona.getCorporateType());
        personaTransient.setCompanyData(company);

        PersonaCompanion companion = new PersonaCompanion();
        companion.setType(persona.getPropertySystem() != null ? this.regimeType(persona.getPropertySystem()) : null);
        personaTransient.setPersonaCompanionId(companion);

        personaTransient.setProposal(persona.getProposal());
        personaTransient.setTaxId(persona.getCpfCnpj());
        personaTransient.setName(persona.getName());
        personaTransient.setEmail(persona.getEmail());
        personaTransient.setTelephone(persona.getTelephone());
        personaTransient.setMaritalStatus(persona.getMaritalStatus());
        personaTransient.setBirthDate(persona.getBirthDate());
        personaTransient.setRg(persona.getRg());
        personaTransient.setOrgaoEmissor(persona.getOrgaoEmissor());
        personaTransient.setNationality(persona.getNationality());
        personaTransient.setMotherName(persona.getMotherName());
        personaTransient.setCitizenship(persona.getCitizenship());
        personaTransient.setLeadProposal(persona.getLeadProposal() != null ? persona.getLeadProposal(): null);

        if(persona.getPep() != null)
        personaTransient.setPep(persona.getPep().equals(Boolean.FALSE) ? persona.getPep() : false);

        personaTransient.setOccupation(persona.getOccupation());
        personaTransient.setPersonaType(this.isTaxId(persona.getCpfCnpj()));
        personaTransient.setCorporateType(persona.getCorporateType());
        personaTransient.setCreatedAt(persona.getCreatedAt());
        personaTransient.setProponentType(ProponentType.PRINCIPAL);
        personaTransient.setAddress(persona.getAddress());
        personaTransient.setFinancialInstitutionCode(persona.getFinancialInstitutionCode()
                != null ? persona.getFinancialInstitutionCode() : null);
        personaTransient.setAccountBranch( persona.getAccountBranch() != null ? persona.getAccountBranch() : null);
        personaTransient.setAccountNumber(persona.getAccountNumber() != null ? persona.getAccountNumber() : null);
        personaTransient.setAccountDigit(persona.getAccountDigit() != null ? persona.getAccountDigit() : null);
    }
}
