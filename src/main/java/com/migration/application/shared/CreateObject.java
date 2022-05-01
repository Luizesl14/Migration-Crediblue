package com.migration.application.shared;

import com.migration.domain.enums.*;
import com.migration.domain.persona.aggregation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;
import java.util.Date;


@Component
public class CreateObject {

    @Autowired
    private ConvertLocalDataTime convert;


    public PersonaAccounts createAccount(BankAccount account, LocalDateTime createdAt){
        PersonaAccounts personaAccounts = new PersonaAccounts();
        personaAccounts.getAccount().setFinancialInstitutionCode(account.getFinancialInstitutionCode());
        personaAccounts.getAccount().setAccountBranch(account.getAccountBranch());
        personaAccounts.getAccount().setAccountNumber(account.getAccountNumber());
        personaAccounts.getAccount().setAccountDigit(account.getAccountDigit());
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
}
