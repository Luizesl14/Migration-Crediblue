package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.application.shared.ExistsEntity;
import com.migration.domain.Partner;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.*;
import com.migration.infrastructure.IPartnerRepository;
import com.migration.infrastructure.IPersonaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class PartnerService {



    @Autowired
    private IPartnerRepository partnerRepository;

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private CreateObject create;

    @Autowired
    private ConvertLocalDataTime convert;

    @Autowired
    private ExistsEntity existsEntity;

    public Boolean findAll() {
        List<Partner> partners = this.partnerRepository.findAll();
        System.out.println("Quantidade de partner do banco: " + partners.size());
        partnerResolver();
        this.createPersona(partners);
        return Boolean.TRUE ;
    }


    public void partnerResolver(){
        List<Partner> partnerNull = this.partnerRepository.findByPartnerTaxIdIsNull();
        System.out.println("### Persona database " + partnerNull.size());
            int index = 0;
            for (Partner partner: partnerNull) {
                Persona persona = this.personaRepository.findPersonaUser(
                        partner.getName(), partner.getEmail(), partner.getTelephone(), null);
                if(persona != null){
                    System.out.println("### Persona encontrada " + persona.getName() + "index " + index++);
                    partner.setPersona(persona);
                    this.partnerRepository.save(partner);
                }else{
                    String token = UUID.randomUUID().toString().toUpperCase(Locale.ROOT);
                    partner.setCpfCnpj(token.substring(0,8));
                    Partner partnerDatabase = this.partnerRepository.save(partner);
                    System.out.println("### Persona não encontrada set token " + partnerDatabase.getCpfCnpj());
                }
        }

    }


    public Boolean createPersona (List<Partner> partnerNormalized){

        int indexDatabase = 0, indexNew = 0;
        for (Partner partner: partnerNormalized) {
            Persona persona = this.create.createPersona(partner);

          Persona personaDatabase  = this.personaRepository.findByTaxId(partner.getCpfCnpj());
                if(personaDatabase != null){

                    persona.getBankAccounts().forEach(bk-> bk.setPersona(personaDatabase));
                    persona.getAddresses().forEach(adr-> adr.setPersona(personaDatabase));
                    persona.getContacts().forEach(mail-> mail.setPersona(personaDatabase));
                    persona.getPhones().forEach(ph-> ph.setPersona(personaDatabase));

                    BeanUtils.copyProperties(persona, personaDatabase, "id", "createdAt");
                    partner.setPersona(personaDatabase);
                    this.partnerRepository.save(partner);
                    System.out.println("INDEX PERSONA JA EXISTENTE NO BANCO: " +  indexDatabase++);
                }else{
                    partner.setPersona(persona);
                    this.partnerRepository.save(partner);
                    System.out.println("INDEX PERSONA NOVO: " +  indexNew++);
                    System.out.println();
                }
            }
        return Boolean.TRUE;
    }


}
