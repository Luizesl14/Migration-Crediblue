package com.migration.application.shared;

import com.migration.domain.Lead;
import com.migration.domain.LeadProposal;
import com.migration.domain.Partner;
import com.migration.domain.ProposalProponent;
import com.migration.domain.enums.*;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component
public class CreateObject {

    @Autowired
    private ConvertLocalDataTime convert;


    public PersonaAccounts createAccount(String financialInstitutionCode, String accountBranch,
                                         String accountNumber, String accountDigit, Date createdAt){
        PersonaAccounts personaAccounts = new PersonaAccounts();
        BankAccount account = new BankAccount();
        account.setFinancialInstitutionCode(financialInstitutionCode);
        account.setAccountBranch(accountBranch);
        account.setAccountNumber(accountNumber);
        account.setAccountDigit(accountDigit);
        personaAccounts.setAccount(account);
        personaAccounts.setCreatedAt(
                createdAt == null ? new Date() : createdAt);
        personaAccounts.setPrincipal(Boolean.TRUE);
        personaAccounts.setType(BankAccountType.CC_PF);
        return personaAccounts;
    }


    public PersonaAddress createAddress(Address address, LocalDateTime createdAt, Persona persona){
        PersonaAddress personaAddress = new PersonaAddress();
        personaAddress.setData(address);
        personaAddress.setPrincipal(Boolean.TRUE);
        personaAddress.setCreatedAt(
                 createdAt == null ? new Date() : this.convert.covertLocalDataTimeToDate(createdAt));
        if(persona.getPersonaType() == PersonaType.NATURAL_PERSON){
            personaAddress.setType(AddressType.RESIDENTIAL);
        }else {
            personaAddress.setType(AddressType.COMMERCIAL);
        }


        return personaAddress;
    }

    public ContactEmail createEmail (String email, Date createdAt){
        ContactEmail contactEmail = new ContactEmail();
        contactEmail.setEmail(email);
        contactEmail.setCreatedAt(
                createdAt == null ? new Date() : createdAt);
        contactEmail.setPrincipal(Boolean.TRUE);
        contactEmail.setType(EmailType.PERSONAL);
        return  contactEmail;
    }

    public PersonaPhone createPhone(Phone phone, Date createdAt){
        PersonaPhone personaPhone = new PersonaPhone();
        personaPhone.setPhone(phone);
        personaPhone.setCreatedAt(
                createdAt == null ? new Date() : createdAt);
        personaPhone.setPrincipal(Boolean.TRUE);
        personaPhone.setType(CategoryType.COMMERCIAL);
        return personaPhone;
    }


    public ProposalProponent createProponentPrincipal(Date createdAt, ProponentType type){
        ProposalProponent proposalProponent = new ProposalProponent();
        proposalProponent.setCreatedAt(
                createdAt == null ? new Date() : createdAt);
        proposalProponent.setType(type);
        return  proposalProponent;
    }



    public ProposalProponent createProponent(Persona persona, Date createdAt, ProponentType type) {
        ProposalProponent proposalProponent = new ProposalProponent();
        proposalProponent.setCreatedAt(
                createdAt == null ? new Date() : createdAt);
        proposalProponent.setType(type);
        return proposalProponent;
    }


    public Persona createPersonaByLeadProposal(LeadProposal personaParam){

        Persona persona = new Persona();
        persona.setCpfCnpj(personaParam.getCpfCnpj());
        persona.setMaritalStatus(personaParam.getMaritalStatus());
        persona.setBirthDate(personaParam.getBirthDate());
        persona.setMaritalStatus(personaParam.getMaritalStatus());
        persona.setRg(personaParam.getRg());
        persona.setOrgaoEmissor(personaParam.getOrgaoEmissor());
        persona.setNationality(personaParam.getNationality());
        persona.setMotherName(personaParam.getMother());
        persona.setCitizenship(personaParam.getCitizenship());
        persona.setPep(personaParam.getPep());
        persona.setOccupation(personaParam.getOccupation());
        persona.setPersonaType(
                personaParam.getCpfCnpj()
                        .length() == 11 ? PersonaType.NATURAL_PERSON : PersonaType.LEGAL_PERSON);

        if (persona.getPersonaType().equals(PersonaType.NATURAL_PERSON))
            persona.setName(personaParam.getName().toUpperCase());

        if(persona.getPersonaType().equals(PersonaType.LEGAL_PERSON)){
            Company company = new Company();
            company.setFantasyName(personaParam.getName().toUpperCase());
            company.setCorporateName(personaParam.getName().toUpperCase());
            company.setType(personaParam.getCompanyType());
            company.setCnae(personaParam.getCnaeCode());

            if (personaParam.getCompanyFoundingDate() != null)
                company.setFoundationDate(personaParam.getCompanyFoundingDate());

            persona.setCompanyData(company);
        }

        if(personaParam.getCreatedAt() != null)
            persona.setCreatedAt(this.convert.covertLocalDataTimeToDate(personaParam.getCreatedAt()));

        if (personaParam.getFinancialInstitutionCode() != null) {
            persona.getBankAccounts().add(
            this.createAccount(
                    personaParam.getFinancialInstitutionCode(), personaParam.getAccountBranch(),
                    personaParam.getAccountNumber(), personaParam.getAccountDigit(),
                    this.convert.covertLocalDataTimeToDate(personaParam.getCreatedAt())));
        }
        if (personaParam.getAddress() != null) {
            persona.getAddresses().add(
                    this.createAddress(personaParam.getAddress(), personaParam.getAddress().getCreatedAt(), persona));
        }
        if (personaParam.getEmail() != null) {
            persona.getContacts().add(
                    this.createEmail(personaParam.getEmail(),
                            this.convert.covertLocalDataTimeToDate(personaParam.getCreatedAt())));
        }
        if (personaParam.getTelephone() != null) {
            Phone phone = new Phone();
            phone.setNumber(personaParam.getTelephone());
            phone.setIsWhatsApp(Boolean.FALSE);
            persona.getPhones().add(this.createPhone(
                    phone, this.convert.covertLocalDataTimeToDate(personaParam.getCreatedAt())));
        }
        persona.setLeadProposal(personaParam);
        return persona;
    }

    public Persona createPersonaLead(Lead personaParam){
        Persona persona = new Persona();
        persona.setTaxId(personaParam.getCpfCnpj());
        persona.setPersonaType(
                persona.getTaxId()
                        .length() == 11 ? PersonaType.NATURAL_PERSON: PersonaType.LEGAL_PERSON);

        if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
            persona.setName(personaParam.getName().toUpperCase());
            persona.setMaritalStatus(personaParam.getMaritalStatus());
            persona.setBirthDate(personaParam.getBirthDate());
        }else{
            Company company = new Company();
            company.setFantasyName(personaParam.getName().toUpperCase());
            company.setCorporateName(personaParam.getName().toUpperCase());
            persona.setCompanyData(company);
        }

        PersonaComposeIncome personaComposeIncome = new PersonaComposeIncome();
        if(personaParam.getFamilyIncome() != null){
            ComposeIncome composeIncome = new ComposeIncome();
            personaComposeIncome.setType(IncomeType.FIXED_INCOME);
            personaComposeIncome.setComposeIncome(composeIncome);
            personaComposeIncome.getComposeIncome().setAmount(personaParam.getFamilyIncome());
            personaComposeIncome.getComposeIncome().setDescription("Renda Familiar");
            persona.getComposeIncomes().add(personaComposeIncome);
        }
        List<PersonaAddress> personaAddressList = new ArrayList<>();
        if(personaParam.getAddress() != null){
            PersonaAddress personaAddress =
                    this.createAddress(personaParam.getAddress(), personaParam.getAddress().getCreatedAt(), persona);
            personaAddressList.add(personaAddress);
            persona.setAddresses(personaAddressList);

        }
        List<ContactEmail> contactEmailList = new ArrayList<>();
        if(personaParam.getEmail() != null){
            ContactEmail contactEmail =
                    this.createEmail(personaParam.getEmail(),
                            this.convert.covertLocalDataTimeToDate(personaParam.getCreatedAt()));
            contactEmailList.add(contactEmail);
            persona.setContacts(contactEmailList);
        }

        List<PersonaPhone> personaPhoneList = new ArrayList<>();
        if(personaParam.getTelephone() != null){
            Phone phone = new Phone();
            phone.setNumber(personaParam.getTelephone());
            phone.setIsWhatsApp(Boolean.FALSE);
            PersonaPhone personaPhone =
                    this.createPhone(phone, this.convert.covertLocalDataTimeToDate(personaParam.getCreatedAt()));
            personaPhoneList.add(personaPhone);
            persona.setPhones(personaPhoneList);
        }

        return persona;
    }

    public Persona createPersona(Partner personaParam){
        Persona persona = new Persona();

        persona.setTaxId(personaParam.getCpfCnpj());
        persona.setPersonaType(
                personaParam.getCpfCnpj()
                        .length() == 11 ? PersonaType.NATURAL_PERSON: PersonaType.LEGAL_PERSON);

        if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
            persona.setName(personaParam.getName().toUpperCase());
        }else{
            Company company = new Company();
            if(personaParam.getName() != null){
                company.setFantasyName(personaParam.getName().toUpperCase());
                company.setCorporateName(personaParam.getName().toUpperCase());
            }
            persona.setCompanyData(company);
        }

        List<PersonaAccounts> personaAccountsList = new ArrayList<>();
        if(personaParam.getFinancialInstitutionCode() != null){
            PersonaAccounts personaAccounts =  this.createAccount(
                    personaParam.getFinancialInstitutionCode(),personaParam.getAccountBranch(),
                    personaParam.getAccountNumber(), personaParam.getAccountDigit(),
                    this.convert.covertLocalDataTimeToDate(personaParam.getCreatedAt()));
            personaAccountsList.add(personaAccounts);
            persona.setBankAccounts(personaAccountsList);
        }

        if(personaParam.getAddress() != null){
            List<PersonaAddress> personaAddressList = new ArrayList<>();
            PersonaAddress personaAddress =
                    this.createAddress(personaParam.getAddress(), personaParam.getAddress().getCreatedAt(), persona);
            personaAddressList.add(personaAddress);
            persona.setAddresses(personaAddressList);

        }

        if(personaParam.getEmail() != null){
            List<ContactEmail> contactEmailList = new ArrayList<>();
            ContactEmail contactEmail = this.createEmail(personaParam.getEmail(),
                    this.convert.covertLocalDataTimeToDate(personaParam.getCreatedAt()));
            contactEmailList.add(contactEmail);
            persona.setContacts(contactEmailList);
        }

        if(personaParam.getTelephone() != null){
            List<PersonaPhone> personaPhoneList = new ArrayList<>();
            Phone phone = new Phone();
            phone.setNumber(personaParam.getTelephone());
            phone.setIsWhatsApp(Boolean.FALSE);
            PersonaPhone personaPhone = this.createPhone(phone,
                    this.convert.covertLocalDataTimeToDate(personaParam.getCreatedAt()));
            personaPhoneList.add(personaPhone);
            persona.setPhones(personaPhoneList);
        }
        return persona;
    }
}
