package com.migration.application.shared;

import com.migration.domain.persona.aggregation.ContactEmail;
import com.migration.domain.persona.aggregation.PersonaAccounts;
import com.migration.domain.persona.aggregation.PersonaAddress;
import com.migration.domain.persona.aggregation.PersonaPhone;
import com.migration.infrastructure.IContactEmailRepository;
import com.migration.infrastructure.IPersonaAccountRepository;
import com.migration.infrastructure.IPersonaAddressRepository;
import com.migration.infrastructure.IPersonaPhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ExistsEntity {


    public Boolean verifyAccount(List<PersonaAccounts> personaAccountsDatabase, List<PersonaAccounts> personaAccounts) {
        boolean result;
        result = personaAccountsDatabase
                .stream()
                .filter(account->
                        account.getAccount().getFinancialInstitutionCode() != null
                                && account.getAccount().getAccountNumber() != null
                                && account.getAccount().getAccountBranch() != null)
                .anyMatch(account-> personaAccounts
                        .stream()
                        .anyMatch(newAccount-> newAccount.getAccount().getFinancialInstitutionCode()
                                .equals(account.getAccount().getFinancialInstitutionCode())
                                && newAccount.getAccount().getAccountNumber().equals(account.getAccount().getAccountNumber())
                                && newAccount.getAccount().getAccountBranch().equals(account.getAccount().getAccountBranch())));

        return result;

    }

    public Boolean verifyAddress(List<PersonaAddress> personaAddressesDatabase, List<PersonaAddress> personaAddresses) {
        boolean result;
        result = personaAddressesDatabase
                .stream()
                .anyMatch(address-> personaAddresses
                        .stream()
                        .anyMatch(newAddress-> newAddress.getData().getCep().equals(address.getData().getCep())));
        return result;
    }

    public Boolean verifyEmail(List<ContactEmail> contactEmailsDatabase, List<ContactEmail> contactEmails) {
        boolean result;
        result = contactEmailsDatabase
                .stream()
                .anyMatch(email-> contactEmails
                        .stream()
                        .anyMatch(newEmail-> newEmail.getEmail().equals(email.getEmail())));
        return result;
    }

    public Boolean verifyPhone(List<PersonaPhone> personaPhonesDatabase, List<PersonaPhone> personaPhones) {
        boolean result;
        result = personaPhonesDatabase
                .stream()
                .anyMatch(phone-> personaPhones
                        .stream()
                        .anyMatch(newPhone-> newPhone.getPhone().getNumber().equals(phone.getPhone().getNumber())));
        return result;
    }

}
