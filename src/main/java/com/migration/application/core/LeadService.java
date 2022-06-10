package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
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


    public Boolean findAll() {
        List<Lead> leads = this.leadRepository.findAll();
        System.out.println("Quantidade de Lead do banco: " + leads.size());
        this.createPersona(leads);
        return Boolean.TRUE ;
    }



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
                    persona.setName(lead.getName().toUpperCase());
                    persona.setMaritalStatus(lead.getMaritalStatus());
                    persona.setBirthDate(lead.getBirthDate());
                }else{
                    Company company = new Company();
                    company.setFantasyName(lead.getName().toUpperCase());
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

                List<PersonaAddress> personaAddressList = new ArrayList<>();
                if(lead.getAddress() != null){
                    PersonaAddress personaAddress = this.create.createAddress(lead.getAddress(), lead.getAddress().getCreatedAt(), persona);
                    personaAddressList.add(personaAddress);
                    persona.setAddresses(personaAddressList);

                }

                List<ContactEmail> contactEmailList = new ArrayList<>();
                if(lead.getEmail() != null){
                    ContactEmail contactEmail = this.create.createEmail(lead.getEmail(), this.convert.covertLocalDataTimeToDate(lead.getCreatedAt()));
                    contactEmailList.add(contactEmail);
                    persona.setContacts(contactEmailList);
                }

                List<PersonaPhone> personaPhoneList = new ArrayList<>();
                if(lead.getTelephone() != null){
                    Phone phone = new Phone();
                    phone.setNumber(lead.getTelephone());
                    phone.setIsWhatsApp(Boolean.FALSE);
                    PersonaPhone personaPhone = this.create.createPhone(phone, this.convert.covertLocalDataTimeToDate(lead.getCreatedAt()));
                    personaPhoneList.add(personaPhone);
                    persona.setPhones(personaPhoneList);
                }
                if(personaDatabase != null){

                    if(!personaAddressList.isEmpty())
                        personaDatabase.getAddresses().addAll(personaAddressList);

                    if(!contactEmailList.isEmpty())
                        personaDatabase.getContacts().addAll(contactEmailList);

                    if(!personaPhoneList.isEmpty())
                        personaDatabase.getPhones().addAll(personaPhoneList);

                    lead.setPersona(personaDatabase);
                    this.leadRepository.save(lead);
                    this.printUpdated(persona);
                }else{
                    lead.setPersona(persona);
                    this.leadRepository.save(lead);
                    this.printSaved(persona);
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
