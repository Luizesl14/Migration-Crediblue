package com.migration.application.core;

import com.migration.application.shared.CreateObject;
import com.migration.domain.Lead;
import com.migration.domain.enums.IncomeType;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Company;
import com.migration.domain.persona.aggregation.ComposeIncome;
import com.migration.domain.persona.aggregation.PersonaComposeIncome;
import com.migration.domain.persona.aggregation.Phone;
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

@Service
public class LeadService {


    @Autowired
    private ILeadRepository leadRepository;

    @Autowired
    private CreateObject create;

    @Autowired
    private IPersonaRepository personaRepository;



    public Boolean findAll() {
        List<Lead> leads = this.leadRepository.findAll();
        System.out.println("Quantidade de Lead do banco: " + leads.size());
        this.createPersona(leads);
        return Boolean.TRUE ;
    }


    @Transactional
    public Boolean createPersona (List<Lead> leadNormalized){

        for (Lead lead: leadNormalized) {
            Persona persona = new Persona();
            if(lead != null){
                Persona personaDatabase = null;
                if(lead.getCpfCnpj()!= null){
                    personaDatabase  = this.personaRepository.findByTaxId(lead.getCpfCnpj());
                    if(personaDatabase != null)
                        if (personaDatabase.getPersonaType().equals(PersonaType.NATURAL_PERSON)) {
                            System.out.println("Persona Existe no banco ** PF ** : " + personaDatabase.getName());
                        } else {
                            System.out.println("Persona Existe no banco  ** PJ ** : " + personaDatabase.getCompanyData().getCorporateName());
                        }
                }
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
                if(personaDatabase != null){
                    persona.setId(personaDatabase.getId());

                  BeanUtils.copyProperties(persona ,personaDatabase,
                          "id", "name", "cpfCnpj", "createdAt");

                    Persona personaSave = this.personaRepository.save(persona);
                    lead.setPersona(personaSave);
                    this.save(lead);
                    this.printUpdated(personaSave);

                }else{
                   Persona personaSave = this.personaRepository.save(persona);
                   lead.setPersona(personaSave);
                   this.save(lead);
                    this.printSaved(personaSave);
                }
            }
        }
        return Boolean.TRUE;
    }

    public void save(Lead lead){
        this.leadRepository.save(lead);

    }

    public void printUpdated(Persona persona){
        if (persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)) {
            System.out.println("UPDATED PERSONA  ** PF ** : " + persona.getName());
        } else {
            System.out.println("UPDATED PERSONA   ** PJ ** : " + persona.getCompanyData().getCorporateName());
        }
        System.out.println();
    }

    public void printSaved(Persona persona){
        if (persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)) {
            System.out.println("SAVED PERSONA  ** PF ** : " + persona.getName());
        } else {
            System.out.println("SAVED PERSONA   ** PJ ** : " + persona.getCompanyData().getCorporateName());
        }
        System.out.println();
    }

}
