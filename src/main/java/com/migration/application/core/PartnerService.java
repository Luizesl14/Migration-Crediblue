package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.domain.Finder;
import com.migration.domain.Partner;
import com.migration.domain.enums.*;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.*;
import com.migration.infrastructure.IPartnerRepository;
import com.migration.infrastructure.IPersonaRepository;
import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PartnerService {

    @Autowired
    private IPartnerRepository partnerRepository;

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private ConvertLocalDataTime convert;

    @Autowired
    private CreateObject create;

    public Boolean findAll() {
        List<Partner> partners = this.partnerRepository.findAll();
        System.out.println("Quantidade de partner do banco: " + partners.size());
        this.normalizationStepOne(partners);
        return Boolean.TRUE ;
    }

    public Boolean normalizationStepOne (List<Partner> partnerDatabase){

        List<Partner> normalizationStepOne = partnerDatabase
                .stream()
                .filter(partner ->
                        partner.getCpfCnpj().equals(partner.getCpfCnpj())
                                && partner.getEmail().equals(partner.getEmail())
                                && partner.getTelephone().equals(partner.getTelephone())
                                && partner.getName().equals(partner.getName())
                ).toList();

        System.out.println("Partner Normalisados Step One: " + normalizationStepOne.size());
        normalizationStepOne.forEach(System.out::println);

        this.updatePersona(normalizationStepOne);
        return Boolean.TRUE;

    }


    @Transactional
    public Boolean updatePersona(List<Partner> partnersNormalized){

        Partner partner = partnersNormalized.stream().findFirst().get();
        Persona personaDatabase = this.personaRepository.findByTaxId(partner.getCpfCnpj());
        Persona createPartner = this.createPersona(partner);

        if(personaDatabase != null){
            createPartner.setId(personaDatabase.getId());
            this.personaRepository.save(createPartner);
        }else {
            partnersNormalized.forEach(findersAdd -> {
                findersAdd.setPersona(createPartner);
            });
        }
        System.out.println("Findes Finais: " + partnersNormalized.size());
        partnersNormalized.forEach(System.out::println);

        this.save(partnersNormalized);
        return Boolean.TRUE;
    }


    @Transactional
    public Persona createPersona ( Partner partner) {

        Persona persona = new Persona();
        persona.setName(partner.getName());
        persona.setTaxId(partner.getCpfCnpj());
        persona.setPersonaType(
                partner.getCpfCnpj()
                        .length() == 11 ? PersonaType.NATURAL_PERSON: PersonaType.LEGAL_PERSON);

        if(partner.getFinancialInstitutionCode() != null){
            persona.getBankAccounts().add(this.create.createAccount(
                    partner.getFinancialInstitutionCode(), partner.getAccountBranch(),
                    partner.getAccountNumber(), partner.getAccountDigit(), null));
        }
        if(partner.getAddress() != null){
            persona.getAddresses().add(this.create.createAddress(partner.getAddress(),
                    partner.getAddress().getCreatedAt()));
        }
        if(partner.getEmail() != null){
            persona.getContacts().add(this.create.createEmail(partner.getEmail(), null));
        }

        if(partner.getTelephone() != null){
            Phone phone = new Phone();
            phone.setNumber(partner.getTelephone());
            phone.setIsWhatsApp(Boolean.FALSE);
            persona.getPhones().add(this.create.createPhone(phone, null));
        }
        return  persona;
    }

    @Transactional
    public Boolean save (List<Partner> findersNormalized) {
        this.partnerRepository.saveAll(findersNormalized);

        findersNormalized.forEach(
                finder -> System.out.println("Finders salvo " + findersNormalized));
        return Boolean.TRUE;
    }
}
