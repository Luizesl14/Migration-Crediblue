package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.domain.Lead;
import com.migration.domain.enums.*;
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
         .stream().filter(lead -> lead.getCpfCnpj().equals(lead.getCpfCnpj())
                     && lead.getPartner().getId().equals(lead.getPartner().getId())).toList();

     System.out.println("Leads Normalizados Step One: " + normalizationStepOne.size());

     this.createPersona(normalizationStepOne);
     return Boolean.TRUE;

    }



    @Transactional
    public Boolean createPersona (List<Lead> leadNormalized){


        for (Lead lead: leadNormalized) {
            Persona persona = new Persona();
            if(lead != null){

                persona.setPersonaType(
                        lead.getCpfCnpj()
                                .length() == 11 ? PersonaType.NATURAL_PERSON: PersonaType.LEGAL_PERSON);
                   persona.setTaxId(lead.getCpfCnpj());

                if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
                    persona.setName(lead.getName());
                    persona.setMaritalStatus(lead.getMaritalStatus());
                    persona.setBirthDate(lead.getBirthDate());
                }else{
                    Company company = new Company();
                    company.setCorporateName(lead.getName());
                    persona.setCompanyData(company);
                }

                PersonaComposeIncome personaComposeIncome = new PersonaComposeIncome();
                if(lead.getFamilyIncome() != null){
                    ComposeIncome composeIncome = new ComposeIncome();
                    personaComposeIncome.setType(IncomeType.FIXED_INCOME);
                    personaComposeIncome.setComposeIncome(composeIncome);
                    personaComposeIncome.getComposeIncome().setAmount(lead.getFamilyIncome());
                    personaComposeIncome.getComposeIncome().setDescription("Renda Familiar");
                    persona.getComposeIncomes().add(personaComposeIncome);
                }

                if(lead.getAddress() != null ){
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
                lead.setPersona(persona);
                if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
                    System.out.println("New Person ** PF ** : " + persona.getName());
                }else{
                    System.out.println("New Person ** PJ ** : " + persona.getCompanyData().getCorporateName());
                }
            }
        }

        System.out.println("Total de Personas criadas:  " + leadNormalized.size());
        this.save(leadNormalized);
        return Boolean.TRUE;
    }


    @Transactional
    public Boolean save(List<Lead> leadNormalized){
        for (Lead lead: leadNormalized) {
            this.leadRepository.save(lead);
            System.out.println("*** Save : " + lead.getPersona().getName());
        }
        return Boolean.TRUE;
    }

}
