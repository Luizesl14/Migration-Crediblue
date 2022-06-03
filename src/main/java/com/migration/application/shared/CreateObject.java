package com.migration.application.shared;

import com.migration.domain.ProposalProponent;
import com.migration.domain.enums.*;
import com.migration.domain.persona.PersonaMigration;
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
                                         String accountNumber, String accountDigit, LocalDateTime createdAt){
        PersonaAccounts personaAccounts = new PersonaAccounts();
        BankAccount account = new BankAccount();
        account.setFinancialInstitutionCode(financialInstitutionCode);
        account.setAccountBranch(accountBranch);
        account.setAccountNumber(accountNumber);
        account.setAccountDigit(accountDigit);
        personaAccounts.setAccount(account);
        personaAccounts.setCreatedAt(
                createdAt == null ? new Date() : this.convert.covertLocalDataTimeToDate(createdAt));
        personaAccounts.setPrincipal(Boolean.TRUE);
        personaAccounts.setType(BankAccountType.CC_PF);
        return personaAccounts;
    }


    public PersonaAddress createAddress(Address address, LocalDateTime createdAt){
        PersonaAddress personaAddress = new PersonaAddress();
        personaAddress.setData(address);
        personaAddress.setPrincipal(Boolean.TRUE);
        personaAddress.setCreatedAt(
                 createdAt == null ? new Date() : this.convert.covertLocalDataTimeToDate(createdAt));
        personaAddress.setType(AddressType.OTHERS);

        return personaAddress;
    }

    public ContactEmail createEmail (String email, LocalDateTime createdAt){
        ContactEmail contactEmail = new ContactEmail();
        contactEmail.setEmail(email);
        contactEmail.setCreatedAt(
                createdAt == null ? new Date() : this.convert.covertLocalDataTimeToDate(createdAt));
        contactEmail.setPrincipal(Boolean.TRUE);
        contactEmail.setType(EmailType.PERSONAL);
        return  contactEmail;
    }

    public PersonaPhone createPhone(Phone phone, LocalDateTime createdAt){
        PersonaPhone personaPhone = new PersonaPhone();
        personaPhone.setPhone(phone);
        personaPhone.setCreatedAt(
                createdAt == null ? new Date() : this.convert.covertLocalDataTimeToDate(createdAt));
        personaPhone.setPrincipal(Boolean.TRUE);
        personaPhone.setType(CategoryType.PERSONAL);
        return personaPhone;
    }


    public ProposalProponent createProponentPrincipal(LocalDateTime createdAt, ProponentType type){
        ProposalProponent proposalProponent = new ProposalProponent();
        proposalProponent.setCreatedAt(
                createdAt == null ? new Date() : this.convert.covertLocalDataTimeToDate(createdAt));
        proposalProponent.setType(type);
        return  proposalProponent;
    }



    public ProposalProponent createProponent(Persona persona,LocalDateTime createdAt, ProponentType type) {
        ProposalProponent proposalProponent = new ProposalProponent();
        proposalProponent.setCreatedAt(
                createdAt == null ? new Date() : this.convert.covertLocalDataTimeToDate(createdAt));
        proposalProponent.setType(type);
        return proposalProponent;
    }
}
