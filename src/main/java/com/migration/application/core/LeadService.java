package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.domain.Lead;
import com.migration.domain.enums.AddressType;
import com.migration.domain.enums.CategoryType;
import com.migration.domain.enums.EmailType;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.*;
import com.migration.infrastructure.ILeadRepository;
import com.migration.infrastructure.IPersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LeadService {


    @Autowired
    private ILeadRepository leadRepository;

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private ConvertLocalDataTime convert;

    @Autowired
    private CreateObject create;


    public Boolean findAll() {
        List<Lead> leads = this.leadRepository.findAll();
        System.out.println("Quantidade de Lead do banco: " + leads.size());
        this.normalizationStepOne(leads);
        return Boolean.TRUE ;
    }

    public Boolean normalizationStepOne (List<Lead> leadDatabase){

     List<Lead> normalizationStepOne = leadDatabase
         .stream()
         .filter(lead ->
                     lead.getCpfCnpj().equals(lead.getCpfCnpj()))
             .toList();

     System.out.println("Leads Normalisados Step One: " + normalizationStepOne.size());
     normalizationStepOne.forEach(System.out::println);

     this.normalizationStepTwo(normalizationStepOne);
     return Boolean.TRUE;

    }


    public Boolean normalizationStepTwo (List<Lead> leads){
        List<Lead> normalizationTwo = null;

        if(leads.size() > 1){
            normalizationTwo = leads.stream()
            .filter(lead ->
                    lead.getPartner().getId().equals(lead.getPartner().getId())
            ).toList();

            System.out.println("Leads Normalisados Step Two: " + normalizationTwo.size());
            normalizationTwo.forEach(System.out::println);

            this.createPersona(normalizationTwo);
        }else {
            this.createPersona(leads);
        }
        return  Boolean.TRUE;
    }



    @Transactional
    public Boolean createPersona (List<Lead> leadNormalized){

        Persona persona = new Persona();
        Lead lead = leadNormalized.get(0);

        if(lead != null){
            persona.setName(lead.getName());
            persona.setTaxId(lead.getCpfCnpj());
            persona.setPersonaType(
            lead.getCpfCnpj()
                    .length() == 11 ? PersonaType.NATURAL_PERSON: PersonaType.LEGAL_PERSON);
            persona.setMaritalStatus(lead.getMaritalStatus());
            persona.setBirthDate(this.convert.convertToLocalDate(lead.getBirthDate()));

            PersonaComposeIncome composeIncome = new PersonaComposeIncome();
            composeIncome.getComposeIncome().setAmount(lead.getFamilyIncome());
            composeIncome.getComposeIncome().setDescription("Renda Familiar");
            persona.getComposeIncomes().add(composeIncome);

            if(lead.getAddress() != null){
                persona.getAddresses().add(this.create.createAddress(lead.getAddress(),
                        lead.getAddress().getCreatedAt()));
            }
            if(lead.getEmail() != null){
                persona.getContacts().add(this.create.createEmail(lead.getEmail(), null));
            }

            if(lead.getTelephone() != null){
                Phone phone = new Phone();
                phone.setNumber(lead.getTelephone());
                phone.setIsWhatsApp(Boolean.FALSE);
                persona.getPhones().add(this.create.createPhone(phone, null));
            }
        }

        leadNormalized.forEach(leads->{
            leads.setPersona(persona);
        });

        System.out.println("Leads Finais: " + leadNormalized.size());
        leadNormalized.forEach(System.out::println);

        this.save(leadNormalized);
        return Boolean.TRUE;
    }


    @Transactional
    public Boolean save(List<Lead> leadNormalized){
        this.leadRepository.saveAll(leadNormalized);
        return Boolean.TRUE;
    }

}
