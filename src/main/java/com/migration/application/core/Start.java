package com.migration.application.core;

import com.migration.application.core.config.IcreateProponent;
import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.application.shared.ExistsEntity;
import com.migration.domain.*;
import com.migration.domain.enums.CompanyType;
import com.migration.domain.enums.MaritalStatus;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.enums.ProponentType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Address;
import com.migration.domain.persona.aggregation.Company;
import com.migration.domain.persona.aggregation.Phone;
import com.migration.infrastructure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private ILeadRepository leadProposalRepository;

    @Autowired
    private ILeadRepository leadRepository;

    @Autowired
    private ISimulatonRepository simulatonRepository;

    @Autowired
    private ExistsEntity existsEntity;

    private final static Logger LOGGER = Logger.getLogger(Start.class.getName());

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

        this.proposalProponentRepository.saveAll(proponents);
        this.personaRepository.saveAll(personas);
        this.proposalProponentRepository.saveAll(mainProponents);

        LOGGER.log(Level.INFO, "TOTAL DE PROPOSAL NO BANCO {0}", proposals.size());
        LOGGER.log(Level.INFO, "TOTAL DE PROPONENTS {0}", proponents.size());
        LOGGER.log(Level.INFO, "TOTAL DE PERSONA CRIADA PARA PROPONENT PRINCIPAL {0}", personas.size());
        LOGGER.log(Level.INFO, "TOTAL DE PROPONENTS PRINCIPAIS {0}", mainProponents.size());

        this.normalizedEntityCpfAndCnpjIsNull();
        this.goThroughSimulation();
        this.normalizedEntityContainsPerson();
    }

    @Override
    public void normalizedEntityCpfAndCnpjIsNull() {
        List<User> users = this.userRepository.findByUserCpfAndCnpjNull();
        List<User> usersNomalized = new ArrayList<>();
        users.parallelStream().forEach(user -> {
            User userExists = this.userRepository.
                    findPersonaUser(user.getName(), user.getEmail(),user.getTelephone(),null);
            if(userExists != null){
                usersNomalized.add(this.updatedUser(user, userExists));
            }
            if(userExists == null){
                String token = UUID.randomUUID().toString().toUpperCase(Locale.ROOT);
                user.setCpf(token.substring(0,8));
                usersNomalized.add(user);
            }
        });

        List<Partner> partners = this.partnerRepository.findByPartnerCpfIsNull();
        List<Partner> partnersNormalized = new ArrayList<>();
        partners.parallelStream().forEach(partner -> {
            Partner partnerExists = this.partnerRepository.
                    findPersonaPartner(partner.getName(), partner.getEmail(),partner.getTelephone(),null);
            if(partnerExists != null){
                partnersNormalized.add(this.updatePartner(partner, partnerExists));
            }
            if(partnerExists == null){
                String token = UUID.randomUUID().toString().toUpperCase(Locale.ROOT);
                partner.setCpfCnpj(token.substring(0,8));
                partnersNormalized.add(partner);
            }
        });

        this.userRepository.saveAll(usersNomalized);
        this.partnerRepository.saveAll(partnersNormalized);

        LOGGER.log(Level.INFO, "TOTAL DE USUARIOS NO BANCO {0}", users.size());
        LOGGER.log(Level.INFO, "TOTAL DE USUARIOS SEM CPF NORMALIZADOS {0}", usersNomalized.size());

        LOGGER.log(Level.INFO, "TOTAL DE PARCEIROS NO BANCO {0}", partners.size());
        LOGGER.log(Level.INFO, "TOTAL DE PARCEIROS SEM CPF NORMALIZADOS {0}", partnersNormalized.size());
    }

    @Override
    public PersonaType isTaxId(String taxId) {
        if(taxId.length() <= 11)
            return PersonaType.NATURAL_PERSON;
        return PersonaType.LEGAL_PERSON;
    }

    @Override
    public void goThroughSimulation() {
        List<Simulation> simulations = this.simulatonRepository.findAll();
        List<Simulation> normalizedSimulation = new ArrayList<>();
        simulations.parallelStream().forEach(simulation -> {
            simulation.setPersona(this.createdPersona(
                    simulation.getLead(), null,
                    null, null, null, null, null));
            normalizedSimulation.add(simulation);
        });
        this.simulatonRepository.saveAll(normalizedSimulation);

        LOGGER.log(Level.INFO, "TOTAL DE SIMULATION NO BANCO {0}", simulations.size());
        LOGGER.log(Level.INFO, "TOTAL DE SIMULATION NORMALIZED {0}", normalizedSimulation.size());

    }

    @Override
    public void normalizedEntityContainsPerson() {

        List<Partner> partners = this.partnerRepository.findAll();
        partners.parallelStream().forEach(p-> this.updatePartner(p, null));

        List<User> users = this.userRepository.findAll();
        users.parallelStream().forEach(u-> this.updatedUser(u, null));

        List<Finder> finders = this.finderRespository.findAll();
        finders.parallelStream().forEach(this::updateFinder);

        List<Lead> leads = this.leadRepository.findLeadTaxIdNull();
        leads.parallelStream().forEach(this::updateLead);
    }


    @Override
    public Partner updatePartner(Partner partner, Partner partnerDatabse) {

            Partner partberSaved = null;
            if(partnerDatabse == null){
                partberSaved = this.partnerRepository.findbyTaxId(partner.getCpfCnpj());
            }
            if(partnerDatabse != null){
               partberSaved = partnerDatabse;
            }

            if(partberSaved != null){

                partberSaved.getPersona().setName(
                        partner.getName() != null ? partner.getName().toUpperCase() : null);

                if(partner.getPersona().getPersonaType().equals(PersonaType.LEGAL_PERSON)){
                    partberSaved.getPersona().getCompanyData().setFantasyName(partner.getName().toUpperCase());
                    partberSaved.getPersona().getCompanyData().setCorporateName(partner.getName().toUpperCase());
                }

                if(this.existsEntity.verifyAccount(partner.getPersona().getBankAccounts(), partner.getPersona().getBankAccounts())
                        .equals(Boolean.FALSE)){
                    partberSaved.getPersona().getBankAccounts().addAll(partner.getPersona().getBankAccounts());
                }
                if(this.existsEntity.verifyAddress(partner.getPersona().getAddresses(), partner.getPersona().getAddresses())
                        .equals(Boolean.FALSE)){
                    partberSaved.getPersona().getAddresses().addAll(partner.getPersona().getAddresses());
                }
                if(this.existsEntity.verifyEmail(partner.getPersona().getContacts(), partner.getPersona().getContacts())
                        .equals(Boolean.FALSE)){
                    partberSaved.getPersona().getContacts().addAll(partner.getPersona().getContacts());
                }
                if(this.existsEntity.verifyPhone(partner.getPersona().getPhones(),partner.getPersona().getPhones())
                        .equals(Boolean.FALSE)){
                    partberSaved.getPersona().getPhones().addAll(partner.getPersona().getPhones());
                }
                return this.partnerRepository.save(partberSaved);
            }
            if(partberSaved == null){
                Persona createdPartner = this.createdPersona(
                        null,null, partner, null, null, null, null);
                partner.setPersona(createdPartner);
                return this.partnerRepository.save(partner);
            }

       return null;
    }

    @Override
    public Lead updateLead(Lead lead) {
        Lead leadDatabase = this.leadRepository.findLeadTaxId(lead.getCpfCnpj());

        if(leadDatabase != null){

            leadDatabase.getPersona().setName(
                    leadDatabase.getName() != null ? lead.getName().toUpperCase() : null);

            if(leadDatabase.getPersona().getPersonaType().equals(PersonaType.LEGAL_PERSON)){
                leadDatabase.getPersona().getCompanyData().setFantasyName(lead.getName().toUpperCase());
                leadDatabase.getPersona().getCompanyData().setCorporateName(lead.getName().toUpperCase());
            }

            if(this.existsEntity.verifyAccount(leadDatabase.getPersona().getBankAccounts(), lead.getPersona().getBankAccounts())
                    .equals(Boolean.FALSE)){
                leadDatabase.getPersona().getBankAccounts().addAll(lead.getPersona().getBankAccounts());
            }
            if(this.existsEntity.verifyAddress(leadDatabase.getPersona().getAddresses(), lead.getPersona().getAddresses())
                    .equals(Boolean.FALSE)){
                leadDatabase.getPersona().getAddresses().addAll(lead.getPersona().getAddresses());
            }
            if(this.existsEntity.verifyEmail(leadDatabase.getPersona().getContacts(), lead.getPersona().getContacts())
                    .equals(Boolean.FALSE)){
                leadDatabase.getPersona().getContacts().addAll(lead.getPersona().getContacts());
            }
            if(this.existsEntity.verifyPhone(leadDatabase.getPersona().getPhones(),lead.getPersona().getPhones())
                    .equals(Boolean.FALSE)){
                leadDatabase.getPersona().getPhones().addAll(lead.getPersona().getPhones());
            }
            return this.leadRepository.save(leadDatabase);
        }
        if(leadDatabase == null){
            Persona createdLead = this.createdPersona(
                    lead,null, null, null, null, null, null);
            lead.setPersona(createdLead);
            return this.leadRepository.save(lead);
        }
        return null;
    }

    @Override
    public Finder updateFinder(Finder finder) {

        Finder finderDatabse = this.finderRespository.findByTaxId(finder.getCpf());

        if(finderDatabse != null){

            finderDatabse.getPersona().setName(
                    finderDatabse.getName() != null ? finder.getName().toUpperCase() : null);

            if(finderDatabse.getPersona().getPersonaType().equals(PersonaType.LEGAL_PERSON)){
                finderDatabse.getPersona().getCompanyData().setFantasyName(finder.getName().toUpperCase());
                finderDatabse.getPersona().getCompanyData().setCorporateName(finder.getName().toUpperCase());
            }

            if(this.existsEntity.verifyAccount(finderDatabse.getPersona().getBankAccounts(), finder.getPersona().getBankAccounts())
                    .equals(Boolean.FALSE)){
                finderDatabse.getPersona().getBankAccounts().addAll(finder.getPersona().getBankAccounts());
            }
            if(this.existsEntity.verifyAddress(finderDatabse.getPersona().getAddresses(), finder.getPersona().getAddresses())
                    .equals(Boolean.FALSE)){
                finderDatabse.getPersona().getAddresses().addAll(finder.getPersona().getAddresses());
            }
            if(this.existsEntity.verifyEmail(finderDatabse.getPersona().getContacts(), finder.getPersona().getContacts())
                    .equals(Boolean.FALSE)){
                finderDatabse.getPersona().getContacts().addAll(finder.getPersona().getContacts());
            }
            if(this.existsEntity.verifyPhone(finderDatabse.getPersona().getPhones(),finder.getPersona().getPhones())
                    .equals(Boolean.FALSE)){
                finderDatabse.getPersona().getPhones().addAll(finder.getPersona().getPhones());
            }
        }
        if(finderDatabse == null){
            Persona createdFinder = this.createdPersona(
                    null,null, null, finder, null, null, null);
            finder.setPersona(createdFinder);
            return this.finderRespository.save(finder);
        }

        return null;
    }

    @Override
    public Investor updateInvestor(Investor investor) {
        Investor investorDatabse = this.investorRepository.findByInvestorTaxId(investor.getCnpj());

        if(investorDatabse != null){

            investorDatabse.getPersona().setName(
                    investorDatabse.getName() != null ? investor.getName().toUpperCase() : null);

            if(investorDatabse.getPersona().getPersonaType().equals(PersonaType.LEGAL_PERSON)){
                investorDatabse.getPersona().getCompanyData().setFantasyName(investor.getName().toUpperCase());
                investorDatabse.getPersona().getCompanyData().setCorporateName(investor.getName().toUpperCase());
            }
            if(this.existsEntity.verifyEmail(investorDatabse.getPersona().getContacts(), investor.getPersona().getContacts())
                    .equals(Boolean.FALSE)){
                investorDatabse.getPersona().getContacts().addAll(investor.getPersona().getContacts());
            }
            if(this.existsEntity.verifyPhone(investorDatabse.getPersona().getPhones(),investor.getPersona().getPhones())
                    .equals(Boolean.FALSE)){
                investorDatabse.getPersona().getPhones().addAll(investor.getPersona().getPhones());
            }
            return this.investorRepository.save(investorDatabse);
        }
        if(investorDatabse == null){
            Persona createdInvestor = this.createdPersona(
                    null,null, null, null, investor, null, null);
            investor.setPersona(createdInvestor);
            return this.investorRepository.save(investor);
        }
        return  null;
    }

    @Override
    public User updatedUser(User user,User userDatabse) {

            User userSaved = null;
            if(userDatabse == null){
                 userSaved = this.userRepository.findByUserTaxId(user.getCpf());
            }
            if(userDatabse != null){
                userSaved = userDatabse;
            }

            if(userSaved != null){

                userSaved.getPersona().setName(
                        userSaved.getName() != null ? user.getName().toUpperCase() : null);

                if(userSaved.getPersona().getPersonaType().equals(PersonaType.LEGAL_PERSON)){
                    userSaved.getPersona().getCompanyData().setFantasyName(user.getName().toUpperCase());
                    userSaved.getPersona().getCompanyData().setCorporateName(user.getName().toUpperCase());
                }
                if(this.existsEntity.verifyEmail(userSaved.getPersona().getContacts(), user.getPersona().getContacts())
                        .equals(Boolean.FALSE)){
                    userSaved.getPersona().getContacts().addAll(user.getPersona().getContacts());
                }
                if(this.existsEntity.verifyPhone(userSaved.getPersona().getPhones(),user.getPersona().getPhones())
                        .equals(Boolean.FALSE)){
                    userSaved.getPersona().getPhones().addAll(user.getPersona().getPhones());
                }
                return this.userRepository.save(userSaved);
            }
            if(userSaved == null){
                Persona createdUser = this.createdPersona(
                        null,null, null, null, null, user, null);
                user.setPersona(createdUser);
                return this.userRepository.save(user);
            }
       return null;
    }

    @Override
    public void updatePersona(Persona persona) {

        List<Persona> personas = this.personaRepository.findAll();

        List<Persona> distinctPersonas = personas
                .stream().distinct().collect(Collectors.toList());

        List<Persona> normalizedPersonas = new ArrayList<>();

        distinctPersonas.parallelStream().forEach(p-> {
            Persona updatePersona = this.createdPersona(
                    null,null, null, null, null, null, persona);
            normalizedPersonas.add(updatePersona);
        });
        this.personaRepository.saveAll(normalizedPersonas);
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
        newPersona.setProponentType(this.proponentType);

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

            ProposalProponent proponent = new ProposalProponent();
            proponent = this.create.createProponent(
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

            proponent.setPersona(persona);
            proponent.setProposal(persona.getProposal());

        return proponent;
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

}
