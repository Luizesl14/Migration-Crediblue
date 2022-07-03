package com.migration.application.core;

import com.migration.application.core.config.IcreateProponent;
import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.application.shared.ExistsEntity;
import com.migration.domain.*;
import com.migration.domain.enums.*;
import com.migration.domain.persona.Companion;
import com.migration.domain.persona.CreditAnalysis;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.*;
import com.migration.infrastructure.*;
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

    private final static Logger LOGGER = Logger.getLogger(Start.class.getName());

    private Persona personaTransient;


    @Override
    public void satartApplication() {
        this.goThroughProposal();
        this.normalizedEntityCpfAndCnpjIsNull();
        this.normalizedPersona();
        this.normalizedEntityContainsPerson();
        this.goThroughSimulation();
        this.createdCompanionByPersona();
        this.normaizedProponents();
        this.goThroughAnalysisBalanceAndIncome();
    }

    @Override
    public void goThroughProposal() {
        List<Proposal> proposals = this.proposalRepository.findAll();
        List<Persona> personasDatabase = this.personaRepository.findAll();
        List<LeadProposal> leadProposals = this.leadProposalRepository.findAll();

        List<ProposalProponent> proponents = new ArrayList<>();
        List<Persona> personas = new ArrayList<>();
        List<ProposalProponent> mainProponents = new ArrayList<>();

        personasDatabase.forEach(persona -> {
            proponents.add(this.createdProponent(persona));
        });

        leadProposals.forEach(leadProposal -> {
            Persona personaCreated = this.createdPersona(
                    null, leadProposal,
                    null, null, null, null, null);
            personaCreated.setLeadProposal(leadProposal);
            personas.add(personaCreated);
        });

        personas.forEach(persona -> {
            ProposalProponent proponent = this.createdProponent(persona);
            mainProponents.add(proponent);
        });

        this.proposalProponentRepository.saveAll(proponents);
        this.goThroughPersonaDocument();
        this.personaRepository.saveAll(personas);
        this.proposalProponentRepository.saveAll(mainProponents);
        this.goThroughLeadDocument();

        LOGGER.log(Level.INFO, "TOTAL DE PROPOSAL NO BANCO {0}", proposals.size());
        LOGGER.log(Level.INFO, "TOTAL DE PROPONENTS {0}", proponents.size());
        LOGGER.log(Level.INFO, "TOTAL DE LEADS PROPOSAL NO BANCO {0}", leadProposals.size());
        LOGGER.log(Level.INFO, "TOTAL DE PERSONA CRIADA PARA PROPONENT PRINCIPAL {0}", personas.size());
        LOGGER.log(Level.INFO, "TOTAL DE PROPONENTS PRINCIPAIS {0}", mainProponents.size());
        System.out.println();
    }

    @Transactional
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
    public void     normalizedEntityContainsPerson() {

        List<User> users = this.userRepository.findAll();
        List<User> usersNormalized = new ArrayList<>();
        users.forEach(u->{
            Persona personaExists = this.personaRepository.
                    findAllByTaxId(u.getCpf());
            if(personaExists != null){
                Persona createdPersona = this.createdPersona(
                       null,null, null, null, null, u, null);
                u.setPersona(createdPersona);
                usersNormalized.add(this.updatedUser(u, personaExists));
            }else{
                Persona createdPersona = this.createdPersona(
                        null,null, null, null, null, u, null);
                u.setPersona(createdPersona);
                usersNormalized.add(u);
            }
        });

        List<Lead> leads = this.leadRepository.findAll();
        List<Lead> leadsNormalized = new ArrayList<>();
        leads.forEach(l->{
            Persona personaExists = this.personaRepository.
                    findAllByTaxId(l.getCpfCnpj());
            if(personaExists != null){
                Persona createdPersona = this.createdPersona(
                        l,null, null, null, null, null, null);
                l.setPersona(createdPersona);
                leadsNormalized.add(this.updateLead(l, personaExists));
            }else{
                Persona createdPersona = this.createdPersona(
                        l,null, null, null, null, null, null);
                l.setPersona(createdPersona);
                leadsNormalized.add(l);
            }
        });

        List<Partner> partners = this.partnerRepository.findAll();
        List<Partner> partnerNormalized = new ArrayList<>();
        partners.forEach(p-> {
            Persona personaExists = this.personaRepository.
                    findAllByTaxId(p.getCpfCnpj());

            if(personaExists != null){
                Persona createdPersona = this.createdPersona(
                        null,null, p, null, null, null, null);
                p.setPersona(createdPersona);
                partnerNormalized.add(this.updatePartner(p, personaExists));
            }else{
                Persona createdPersona = this.createdPersona(
                        null,null, p, null, null, null, null);
                p.setPersona(createdPersona);
                partnerNormalized.add(p);
            }
        });


        List<Finder> finders = this.finderRespository.findAll();
        List<Finder> findersNormalized = new ArrayList<>();
        finders.forEach(finder -> {
            Persona personaExists = this.personaRepository.
                    findAllByTaxId(finder.getCpf());

            if(personaExists != null){
                Persona createdPersona = this.createdPersona(
                        null,null, null, finder, null, null, null);
                finder.setPersona(createdPersona);
                findersNormalized.add(this.updateFinder(finder, personaExists));
            }else{
                Persona createdPersona = this.createdPersona(
                        null,null, null, finder, null, null, null);
                finder.setPersona(createdPersona);
                findersNormalized.add(finder);
            }
        });


        List<Investor> investors = this.investorRepository.findAll();
        List<Investor> investorNormalized = new ArrayList<>();
        investors.forEach(i->{
            Persona personaExists = this.personaRepository.
                    findAllByTaxId(i.getCnpj());
            if(personaExists != null){
                Persona createdPersona = this.createdPersona(
                       null,null, null, null, i, null, null);
                i.setPersona(createdPersona);
                investorNormalized.add(this.updateInvestor(i, personaExists));
            }else{
                Persona createdPersona = this.createdPersona(
                        null,null, null, null, i, null, null);
                i.setPersona(createdPersona);
                investorNormalized.add(i);
            }
        });

        this.userRepository.saveAll(usersNormalized);
        this.partnerRepository.saveAll(partnerNormalized);
        this.finderRespository.saveAll(findersNormalized);
        this.leadRepository.saveAll(leadsNormalized);
        this.investorRepository.saveAll(investorNormalized);


        LOGGER.log(Level.INFO, "TOTAL DE PARCEIROS NO BANCO {0}", partners.size());
        LOGGER.log(Level.INFO, "TOTAL DE PARCEIROS NORMALIZED {0}", partnerNormalized.size());
        System.out.println();
        LOGGER.log(Level.INFO, "TOTAL DE FINDERS NO BANCO {0}", finders.size());
        LOGGER.log(Level.INFO, "TOTAL DE FINDERS NORMALIZED {0}", findersNormalized.size());
        System.out.println();
        LOGGER.log(Level.INFO, "TOTAL DE LEADS NO BANCO {0}", leads.size());
        LOGGER.log(Level.INFO, "TOTAL DE LEADS NORMALIZED {0}", leadsNormalized.size());
        System.out.println();
        LOGGER.log(Level.INFO, "TOTAL DE INVESTOR NO BANCO {0}", investors.size());
        LOGGER.log(Level.INFO, "TOTAL DE INVESTOR NORMALIZED {0}", investorNormalized.size());
        System.out.println();
        LOGGER.log(Level.INFO, "TOTAL DE USERS NO BANCO {0}", users.size());
        LOGGER.log(Level.INFO, "TOTAL DE USERS NORMALIZED {0}", usersNormalized.size());

    }


    @Override
    public void goThroughSimulation() {
        List<Simulation> simulations = this.simulatonRepository.findAll();
        List<Simulation> normalizedSimulation = new ArrayList<>();
        simulations.forEach(simulation -> {
            Persona personaExists = this.personaRepository.
                    findAllByTaxId(simulation.getLead().getCpfCnpj());

            if(personaExists != null){
                simulation.setPersona(personaExists);
                normalizedSimulation.add(simulation);
            }else{
                Persona createdPersona = this.createdPersona(
                        simulation.getLead(),null, null, null, null, null, null);
                simulation.setPersona(createdPersona);
                normalizedSimulation.add(simulation);
            }
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

                if(partner.getPersona().getBankAccounts() != null){
                    if(this.existsEntity.verifyAccount(partner.getPersona().getBankAccounts(), partner.getPersona().getBankAccounts())
                            .equals(Boolean.FALSE)){
                        personaDatabse.getBankAccounts().addAll(partner.getPersona().getBankAccounts());
                    }
                }

                if(partner.getPersona().getAddresses() != null){
                    if(this.existsEntity.verifyAddress(partner.getPersona().getAddresses(), partner.getPersona().getAddresses())
                            .equals(Boolean.FALSE)){
                        personaDatabse.getAddresses().addAll(partner.getPersona().getAddresses());
                    }
                }

                if(partner.getPersona().getContacts() != null){
                    if(this.existsEntity.verifyEmail(partner.getPersona().getContacts(), partner.getPersona().getContacts())
                            .equals(Boolean.FALSE)){
                        personaDatabse.getContacts().addAll(partner.getPersona().getContacts());
                    }
                }

                if(partner.getPersona().getPhones() != null){
                    if(this.existsEntity.verifyPhone(partner.getPersona().getPhones(),partner.getPersona().getPhones())
                            .equals(Boolean.FALSE)){
                        personaDatabse.getPhones().addAll(partner.getPersona().getPhones());
                    }
                }
                partner.setPersona(personaDatabse);
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

            if(this.existsEntity.verifyAccount(personaDatabse.getBankAccounts(), lead.getPersona().getBankAccounts())
                    .equals(Boolean.FALSE)){
                personaDatabse.getBankAccounts().addAll(lead.getPersona().getBankAccounts());
            }
            if(this.existsEntity.verifyAddress(personaDatabse.getAddresses(), lead.getPersona().getAddresses())
                    .equals(Boolean.FALSE)){
                personaDatabse.getAddresses().addAll(lead.getPersona().getAddresses());
            }
            if(this.existsEntity.verifyEmail(personaDatabse.getContacts(), lead.getPersona().getContacts())
                    .equals(Boolean.FALSE)){
                personaDatabse.getContacts().addAll(lead.getPersona().getContacts());
            }
            if(this.existsEntity.verifyPhone(personaDatabse.getPhones(),lead.getPersona().getPhones())
                    .equals(Boolean.FALSE)){
                personaDatabse.getPhones().addAll(lead.getPersona().getPhones());
            }
            lead.setPersona(personaDatabse);
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

            if(this.existsEntity.verifyAccount(personaDatabse.getBankAccounts(), finder.getPersona().getBankAccounts())
                    .equals(Boolean.FALSE)){
                personaDatabse.getBankAccounts().addAll(finder.getPersona().getBankAccounts());
            }
            if(this.existsEntity.verifyAddress(personaDatabse.getAddresses(), finder.getPersona().getAddresses())
                    .equals(Boolean.FALSE)){
                personaDatabse.getAddresses().addAll(finder.getPersona().getAddresses());
            }
            if(this.existsEntity.verifyEmail(personaDatabse.getContacts(), finder.getPersona().getContacts())
                    .equals(Boolean.FALSE)){
                personaDatabse.getContacts().addAll(finder.getPersona().getContacts());
            }
            if(this.existsEntity.verifyPhone(personaDatabse.getPhones(),finder.getPersona().getPhones())
                    .equals(Boolean.FALSE)){
                personaDatabse.getPhones().addAll(finder.getPersona().getPhones());
            }
            finder.setPersona(personaDatabse);
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
            if(this.existsEntity.verifyEmail(personaDatabse.getContacts(), investor.getPersona().getContacts())
                    .equals(Boolean.FALSE)){
                personaDatabse.getContacts().addAll(investor.getPersona().getContacts());
            }
            if(this.existsEntity.verifyPhone(personaDatabse.getPhones(),investor.getPersona().getPhones())
                    .equals(Boolean.FALSE)){
                personaDatabse.getPhones().addAll(investor.getPersona().getPhones());
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
                if(this.existsEntity.verifyEmail(personaDatabse.getContacts(), user.getPersona().getContacts())
                        .equals(Boolean.FALSE)){
                    personaDatabse.getContacts().addAll(user.getPersona().getContacts());
                }
                if(this.existsEntity.verifyPhone(personaDatabse.getPhones(),user.getPersona().getPhones())
                        .equals(Boolean.FALSE)){
                    personaDatabse.getPhones().addAll(user.getPersona().getPhones());
                }
                user.setPersona(personaDatabse);
                return user;
            }else{
                Persona createdUser = this.createdPersona(
                        null,null, null, null, null, user, null);
                user.setPersona(createdUser);
                return user;
            }
    }


    @Override
    public void normalizedPersona() {

        List<Persona> personas = this.personaRepository.findAll();

        List<Persona> distinctPersonas = personas
                .stream().distinct().collect(Collectors.toList());

        List<Persona> normalizedPersonas = new ArrayList<>();

        distinctPersonas.forEach(p-> {
            Persona updatePersona = this.createdPersona(
                    null,null, null, null, null, null, p);
            normalizedPersonas.add(updatePersona);
        });
        this.personaRepository.saveAll(normalizedPersonas);

        LOGGER.log(Level.INFO, "TOTAL DE PERSONAS NO BANCO {0}", personas.size());
        LOGGER.log(Level.WARNING, "TOTAL DE PERSONAS NORMALIZED {0}", distinctPersonas.size());
        LOGGER.log(Level.SEVERE, "TOTAL DE PERSONAS REPETIDAS {0}", (personas.size() - distinctPersonas.size()));
    }



    @Override
    public void normaizedProponents() {
        List<ProposalProponent> proposalProponents = this.proposalProponentRepository.findAll();
        List<ProposalProponent> proposalProponentList = new ArrayList<>();

        proposalProponents.forEach(proponent->{
            Persona personaSave = this.personaRepository.findByTaxId(proponent.getPersona().getCpfCnpj());
            if(personaSave != null){
                proponent.setPersona(personaSave);
                proposalProponentList.add(proponent);
            }
        });

        this.proposalProponentRepository.saveAll(proposalProponentList);
        LOGGER.log(Level.INFO, "TOTAL DE PROPONENTS NO BANCO {0}", proposalProponents.size());
        LOGGER.log(Level.INFO, "TOTAL DE PROPONENTS NORMALIZADOS {0}", proposalProponentList.size());
    }

    @Override
    public void createdCompanionByPersona() {
        List<Persona> personas = this.personaRepository.findByAllPersonasNormalized();

        List<Persona> personasNormalized = new ArrayList<>();

        personas.forEach(persona -> {
                if((persona.getMaritalStatus().equals(MaritalStatus.CASADO) || persona.getMaritalStatus().equals(MaritalStatus.RELACAO_ESTAVEL))
                        && persona.getCompanion() != null){
                    Companion companion = persona.getCompanion();
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

                            if (persona.getMaritalStatus().equals(MaritalStatus.CASADO)) {
                                newPerson.setMaritalStatus(MaritalStatus.CASADO);
                            }
                            newPerson.setPersonaType(PersonaType.NATURAL_PERSON);

                            if (companion.getEmail() != null) {
                                newPerson.getContacts().add(
                                        this.create.createEmail(companion.getEmail(), null));
                            }
                            newPerson.setMotherName(companion.getMotherName() != null ? companion.getMotherName() : null);
                            newPerson.setBirthDate(companion.getBirthDate() != null ? companion.getBirthDate() : null);
                            newPerson.setPep(companion.getPep());

                            personaCompanion.setType(this.createType(persona));
                            personaCompanion.setData(newPerson);
                            persona.setPersonaCompanionId(personaCompanion);
                            personasNormalized.add(persona);

                        } else {
                            personaCompanion.setData(personaDatabase);
                            personaCompanion.setType(this.createType(personaDatabase));
                            persona.setPersonaCompanionId(personaCompanion);
                            personasNormalized.add(persona);
                        }
                    }
                }
        });
        System.out.println();
        this.personaRepository.saveAll(personasNormalized);
        LOGGER.log(Level.INFO, "TOTAL DE COMPANIONS CRIADOS PARA PERSONAS NO BANCO {0}", personasNormalized.size());
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
        creditsAnalyses.forEach(creditAnalysis -> {

            CreditAnalysisProponent creditAnalysisProponent = new CreditAnalysisProponent();
            creditAnalysisProponent.setCreditAnalysis(creditAnalysis);
            creditAnalysisProponent.setAnalysisComparative(new CreditAnalysisComparative());

            ProposalProponent proponent =
                    this.proposalProponentRepository
                            .findAllByProposalByLeadProposalMain(creditAnalysis.getProposal().getLeadProposal().getId(),
                                    creditAnalysis.getProposal().getId(), ProponentType.PRINCIPAL);

            CreditAnalysisProponent creditAnalysisProponentSaved =
                    this.creditAnalysisProponentRepository.save(creditAnalysisProponent);

            if(proponent != null)
                creditAnalysisProponentSaved.setProponent(proponent);

            if(!creditAnalysis.getBalanceSheets().isEmpty()) {
                creditAnalysis.getBalanceSheets().forEach(balanceSheet -> {
                    BalanceSheet balanceSheet02;
                    balanceSheet02 = balanceSheet;
                    balanceSheet02.setCreditAnalysisComparative(creditAnalysisProponentSaved.getAnalysisComparative());
                    balanceSheetList.add(balanceSheet02);
                });
            }
            if(!creditAnalysis.getIncomeStatements().isEmpty()){
                creditAnalysis.getIncomeStatements().forEach(incomeStatement -> {
                    IncomeStatement incomeStatement2;
                    incomeStatement2 = incomeStatement;
                    incomeStatement2.setCreditAnalysisComparative(creditAnalysisProponentSaved.getAnalysisComparative());
                    incomeStatements.add(incomeStatement2);
                });
            }
        });

        this.iincomeStatementRepository.saveAll(incomeStatements);
        this.balanceSheetRepository.saveAll(balanceSheetList);

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
            newPersona.setId(persona.getId());
            this.checkedPersona(persona);
        }
        newPersona.setCpfCnpj(personaTransient.getCpfCnpj() != null ? personaTransient.getCpfCnpj() : null);
        newPersona.setTaxId(personaTransient.getTaxId() != null ? personaTransient.getTaxId() : null );
        newPersona.setMaritalStatus(personaTransient.getMaritalStatus() != null ? personaTransient.getMaritalStatus() : null);
        newPersona.setBirthDate(personaTransient.getBirthDate() != null ? personaTransient.getBirthDate() : null);
        newPersona.setRg(personaTransient.getRg() != null ? personaTransient.getRg() : null );
        newPersona.setOrgaoEmissor(personaTransient.getOrgaoEmissor() != null ? personaTransient.getOrgaoEmissor() : null);
        newPersona.setNationality(personaTransient.getNationality() != null ? personaTransient.getNationality() : null);
        newPersona.setMotherName(personaTransient.getMotherName() != null ? personaTransient.getMotherName() : null);
        newPersona.setCitizenship(personaTransient.getCitizenship() != null ? personaTransient.getCitizenship() : null);

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
            newPersona.setName(personaTransient.getName().toUpperCase());

        if(newPersona.getPersonaType().equals(PersonaType.LEGAL_PERSON)){
            newPersona.setName(personaTransient.getName().toUpperCase());
            Company company = new Company();
            company.setFantasyName(personaTransient.getName().toUpperCase());
            company.setCorporateName(personaTransient.getName().toUpperCase());
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
            if (persona.getParticipationPercentage() != null && persona.getParticipationPercentage() != 0) {
                    proponent.setPercentageOfCommitment(
                            persona.getParticipationPercentage() == null ? 0 : persona.getParticipationPercentage());
            }
            proponent.setMonthlyIncome(persona.getMonthlyIncome() != null ? persona.getMonthlyIncome(): BigDecimal.ZERO);

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
        personaTransient.setCpfCnpj(lead.getCpfCnpj());
        personaTransient.setMonthlyIncome(lead.getFamilyIncome() != null ? lead.getFamilyIncome() : null);
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
        personaTransient.setCompanyData(company);

        PersonaCompanion companion = new PersonaCompanion();
        companion.setType(leadProposal.getTypeRegimeCompanion() != null ? leadProposal.getTypeRegimeCompanion() : null);
        personaTransient.setPersonaCompanionId(companion);

        personaTransient.setProposal(leadProposal.getProposal());
        personaTransient.setLeadProposal(leadProposal);

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
        personaTransient.setAddress(leadProposal.getAddress());
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
        personaTransient.setCpfCnpj(partner.getCpfCnpj());
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
        personaTransient.setCpfCnpj(finder.getCpf());
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
        personaTransient.setCpfCnpj(investor.getCnpj());
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
        personaTransient.setCpfCnpj(user.getCpf());
        personaTransient.setTaxId(user.getCpf());
        personaTransient.setPersonaType(this.isTaxId(user.getCpf()));
        personaTransient.setCreatedAt(this.convert.covertLocalDataTimeToDate(user.getCreatedAt()));
    }

    @Override
    public void checkedPersona(Persona persona) {
        personaTransient = new Persona();
        personaTransient.setTaxId(persona.getCpfCnpj());
        personaTransient.setCpfCnpj(persona.getCpfCnpj());

        if(persona.getName().equals("Crediblue Admin"))
            personaTransient.setTaxId(persona.getTaxId());

        if(persona.getName().equals("Neoassist Admin"))
            personaTransient.setTaxId(persona.getTaxId());

        PersonaCompanion companion = new PersonaCompanion();
        if(persona.getPropertySystem() != null)
        companion.setType(persona.getPropertySystem() != null ? this.regimeType(persona.getPropertySystem()) : null);
        personaTransient.setPersonaCompanionId(companion);

        personaTransient.setOpeningDate(persona.getOpeningDate() != null ? persona.getOpeningDate() : null);
        personaTransient.setEmail(persona.getEmail());
        personaTransient.setTelephone( persona.getTelephone());
        personaTransient.setAddress(persona.getAddress());
        personaTransient.setName(persona.getName());
        personaTransient.setMotherName(persona.getMotherName());
        personaTransient.setSourceIncome(persona.getSourceIncome());
        personaTransient.setMaritalStatus(persona.getMaritalStatus());
        personaTransient.setBirthDate(persona.getBirthDate());
        personaTransient.setCompanion(persona.getCompanion() != null ? persona.getCompanion() : null);
        personaTransient.setLegalRepresentative(persona.getLegalRepresentative()
                != Boolean.FALSE ? persona.getLegalRepresentative() : Boolean.FALSE);
        personaTransient.setRg(persona.getRg());
        personaTransient.setOrgaoEmissor(persona.getOrgaoEmissor());
        personaTransient.setNationality(persona.getNationality());
        personaTransient.setCitizenship(persona.getCitizenship());
        personaTransient.setPep(persona.getPep());
        personaTransient.setOccupation(persona.getOccupation());
        personaTransient.setPersonaType(this.isTaxId(personaTransient.getTaxId()));
        personaTransient.setCreatedAt(persona.getCreatedAt());
        personaTransient.setFinancialInstitutionCode(persona.getFinancialInstitutionCode()
                != null ? persona.getFinancialInstitutionCode() : null);
        personaTransient.setAccountBranch( persona.getAccountBranch() != null ? persona.getAccountBranch() : null);
        personaTransient.setAccountNumber(persona.getAccountNumber() != null ? persona.getAccountNumber() : null);
        personaTransient.setAccountDigit(persona.getAccountDigit() != null ? persona.getAccountDigit() : null);
    }
}
