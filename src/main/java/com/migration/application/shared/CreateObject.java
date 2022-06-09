package com.migration.application.shared;

import com.migration.domain.ProposalProponent;
import com.migration.domain.enums.*;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;


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
}
