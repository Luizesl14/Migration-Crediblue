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

    @Autowired
    private IPersonaAccountRepository personaAccountRepository;

    @Autowired
    private IPersonaAddressRepository personaAddressRepository;

    @Autowired
    private IContactEmailRepository contactEmailRepository;

    @Autowired
    private IPersonaPhoneRepository personaPhoneRepository;


    public Boolean verifyAccount(List<PersonaAccounts> personaAccounts, Integer personaId) {
        if(!personaAccounts.isEmpty()){
            PersonaAccounts accounts =
                    personaAccounts.stream().filter(a-> a.getAccount().getFinancialInstitutionCode() != null).findFirst().get();

            List<PersonaAccounts>  personaDatabase = this.personaAccountRepository.existsAccount(
                    accounts.getAccount().getFinancialInstitutionCode(), accounts.getAccount().getAccountBranch(),
                    accounts.getAccount().getAccountNumber(), personaId);
            if(!personaDatabase.isEmpty())
                return Boolean.TRUE;
        }



      return Boolean.FALSE;
    }

    public Boolean verifyAddress(List<PersonaAddress> personaAddresses, Integer personaId) {

        if(!personaAddresses.isEmpty()){
            PersonaAddress address =
                    personaAddresses.stream().filter(a-> a.getData().getCep() != null).findFirst().get();
            List<PersonaAddress> personaAddressDatabase = this.personaAddressRepository.existeAddress(
                    address.getData().getCep(), address.getData().getStreet(), address.getData().getNumber(), personaId);
            if(!personaAddressDatabase.isEmpty())
                return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public Boolean verifyEmail(List<ContactEmail> contactEmails, Integer personaId) {
        if(!contactEmails.isEmpty()){
            ContactEmail contactEmail =
                    contactEmails.stream().filter(c-> c.getEmail() != null).findFirst().get();
            List<ContactEmail> contactEmailDatabase =
                    this.contactEmailRepository.findContactEmailsByEmail(contactEmail.getEmail(),personaId);
            if(!contactEmailDatabase.isEmpty())
                return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public Boolean verifyPhone(List<PersonaPhone> personaPhones, Integer personaId) {

        if(!personaPhones.isEmpty()){
            PersonaPhone phone =
                    personaPhones.stream().filter(p-> p.getPhone().getNumber() != null).findFirst().get();
            List<PersonaPhone> personaPhoneDatabase = this.personaPhoneRepository.existsPhone(phone.getPhone().getNumber(), personaId);
            if(!personaPhoneDatabase.isEmpty())
                return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }
}
