package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.domain.Finder;
import com.migration.domain.Lead;
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
import java.util.Objects;
import java.util.stream.Collectors;

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
                .filter(p-> p.getCpfCnpj() != null)
                .filter(partner ->
                        Objects.equals(partner.getCpfCnpj(), partner.getCpfCnpj())
                ).toList();
        System.out.println("Partner Normalised Step One: " + normalizationStepOne.size());
        this.createPersona(normalizationStepOne);
        return Boolean.TRUE;

    }


    @Transactional
    public Boolean createPersona (List<Partner> partnerNormalized){

        for (Partner partner: partnerNormalized) {
            Persona persona = new Persona();
            if(partner != null){
                persona.setPersonaType(
                        partner.getCpfCnpj()
                                .length() == 11 ? PersonaType.NATURAL_PERSON: PersonaType.LEGAL_PERSON);
                persona.setTaxId(partner.getCpfCnpj());

                if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
                    persona.setName(partner.getName());
                }else{
                    Company company = new Company();
                    company.setCorporateName(partner.getName());
                    persona.setCompanyData(company);
                }

                if(partner.getAddress() != null ){
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
                partner.setPersona(persona);
                if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
                    System.out.println("New Person ** PF ** : " + persona.getName());
                }else{
                    System.out.println("New Person ** PJ ** : " + persona.getCompanyData().getCorporateName());
                }
            }
        }
        System.out.println("Total de Personas criadas:  " + partnerNormalized.size());
        this.save(partnerNormalized);
        return Boolean.TRUE;
    }




    @Transactional
    public void save (List<Partner> partnerNormalized) {
        for (Partner partner: partnerNormalized){
            Persona persona = this.personaRepository.save(partner.getPersona());
            partner.setPersona(persona);
            this.partnerRepository.save(partner);
            System.out.println("Persona save: " + persona.getName() + " ** Partner **");
        }
    }
}
