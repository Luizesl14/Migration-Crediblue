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
        List<Lead> leadList = new ArrayList<>();
        for (Lead lead: leadNormalized) {
            Persona persona = this.create.createPersonaLead(lead);
            Persona personaDatabase = this.personaRepository.findByTaxId(lead.getCpfCnpj());

            if(personaDatabase != null){
                if (personaDatabase.getPersonaType().equals(PersonaType.NATURAL_PERSON))
                    personaDatabase.setName(lead.getName().toUpperCase());

                if (personaDatabase.getPersonaType().equals(PersonaType.LEGAL_PERSON)) {
                    personaDatabase.getCompanyData().setFantasyName(lead.getName().toUpperCase());
                    personaDatabase.getCompanyData().setCorporateName(lead.getName().toUpperCase());
                }
                if(this.existsEntity.verifyAddress(personaDatabase.getAddresses(), persona.getAddresses())
                        .equals(Boolean.FALSE)){
                    personaDatabase.getAddresses().addAll(persona.getAddresses());
                    System.out.println("-----------ADDRESS DIFERENTE ADICIONADO LEAD -----------");
                }

                if(this.existsEntity.verifyEmail(personaDatabase.getContacts(), persona.getContacts())
                        .equals(Boolean.FALSE)){
                    personaDatabase.getContacts().addAll(persona.getContacts());
                    System.out.println("-----------EMAIL DIFERENTE ADICIONADO LEAD -----------");
                }

                if(this.existsEntity.verifyPhone(personaDatabase.getPhones(), persona.getPhones())
                        .equals(Boolean.FALSE)){
                    personaDatabase.getPhones().addAll(persona.getPhones());
                    System.out.println("-----------PHONE DIFERENTE ADICIONADO LEAD -----------");
                }

                lead.setPersona(personaDatabase);
                leadList.add(lead);
            System.out.println("INDEX PERSONA JÁ EXISTENTE " + indexDatabase++);

            }else{
                lead.setPersona(persona);
                leadList.add(lead);
                System.out.println("INDEX NOVA PERSONA " + indexNewPersona++);
            }
        }
        this.leadRepository.saveAll(leadList);
        return Boolean.TRUE;
    }


}
