package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.application.shared.ExistsEntity;
import com.migration.domain.Lead;
import com.migration.domain.enums.IncomeType;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.*;
import com.migration.infrastructure.ILeadRepository;
import com.migration.infrastructure.IPartnerRepository;
import com.migration.infrastructure.IPersonaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Transactional
@Service
public class LeadService {


    @Autowired
    private ILeadRepository leadRepository;

    @Autowired
    private CreateObject create;

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private ConvertLocalDataTime convert;

    @Autowired
    private ExistsEntity existsEntity;


    public Boolean findAll() {
        List<Lead> leads = this.leadRepository.findAll();
        System.out.println("QUANTIDADE DE LEADS : " + leads.size());
        this.createPersona(leads);
        return Boolean.TRUE ;
    }

    public Boolean createPersona (List<Lead> leadNormalized){

        int indexDatabase = 0 , indexNewPersona = 0;
        for (Lead lead: leadNormalized) {
            Persona persona = this.create.createPersonaLead(lead);
            Persona personaDatabase = this.personaRepository.findByTaxId(lead.getCpfCnpj());

            if(personaDatabase != null){
            System.out.println("QUANTIDADE DE PERSONAS:  : " + personaDatabase.getName());

            persona.getAddresses().forEach(adr-> adr.setPersona(personaDatabase));
            persona.getContacts().forEach(mail-> mail.setPersona(personaDatabase));
            persona.getPhones().forEach(ph-> ph.setPersona(personaDatabase));

            BeanUtils.copyProperties(persona, personaDatabase, "id", "createdAt");
            lead.setPersona(personaDatabase);
            this.leadRepository.save(lead);
            System.out.println("INDEX PERSONA JÁ EXISTENTE " + indexDatabase++);

            }else{
                lead.setPersona(persona);
                this.leadRepository.save(lead);
                System.out.println("INDEX NOVA PERSONA " + indexNewPersona++);
            }
        }
        return Boolean.TRUE;
    }


}
