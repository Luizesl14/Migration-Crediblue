package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.application.shared.ExistsEntity;
import com.migration.domain.Finder;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.ContactEmail;
import com.migration.domain.persona.aggregation.PersonaAddress;
import com.migration.domain.persona.aggregation.PersonaPhone;
import com.migration.domain.persona.aggregation.Phone;
import com.migration.infrastructure.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class FinderService {

    @Autowired
    private IFinderRespository finderRespository;

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private ConvertLocalDataTime convert;

    @Autowired
    private ExistsEntity existsEntity;

    @Autowired
    private IPersonaAccountRepository personaAccountRepository;

    @Autowired
    private IPersonaAddressRepository personaAddressRepository;

    @Autowired
    private IContactEmailRepository contactEmailRepository;

    @Autowired
    private IPersonaPhoneRepository personaPhoneRepository;

    @Autowired
    private CreateObject create;

    public Boolean findAll() {
        List<Finder> finders = this.finderRespository.findAll();
        System.out.println("Quantidade de finders do banco: " + finders.size());
        this.createPersona(finders);
        return Boolean.TRUE ;
    }


    public Boolean createPersona (List<Finder> finderNormalized){
        int indexDatabase = 0, indexNew = 0;
        for (Finder finder: finderNormalized) {
            Persona persona = new Persona();
            if(finder.getCpf() != null){

                Persona  personaDatabase  = this.personaRepository.findByTaxId(finder.getCpf());
                persona.setPersonaType(PersonaType.NATURAL_PERSON);
                persona.setTaxId(finder.getCpf());
                persona.setName(finder.getName().toUpperCase());

                List<PersonaAddress> personaAddressList = new ArrayList<>();
                if(finder.getAddress() != null){
                    PersonaAddress personaAddress = this.create.createAddress(
                            finder.getAddress(), finder.getAddress().getCreatedAt(), persona);
                    personaAddressList.add(personaAddress);
                    persona.setAddresses(personaAddressList);
                }
                List<ContactEmail> contactEmailList = new ArrayList<>();
                if(finder.getEmail() != null){
                    ContactEmail contactEmail = this.create.createEmail(
                            finder.getEmail(), this.convert.covertLocalDataTimeToDate(finder.getCreatedAt()));
                    contactEmailList.add(contactEmail);
                    persona.setContacts(contactEmailList);
                }

                List<PersonaPhone> personaPhoneList = new ArrayList<>();
                if(finder.getTelephone() != null){
                    Phone phone = new Phone();
                    phone.setNumber(finder.getTelephone());
                    phone.setIsWhatsApp(Boolean.FALSE);
                    PersonaPhone personaPhone = this.create.createPhone(
                            phone, this.convert.covertLocalDataTimeToDate(finder.getCreatedAt()));
                    personaPhoneList.add(personaPhone);
                    persona.setPhones(personaPhoneList);
                }
                if(personaDatabase != null){

                    persona.getBankAccounts().forEach(bk-> bk.setPersona(personaDatabase));
                    persona.getAddresses().forEach(adr-> adr.setPersona(personaDatabase));
                    persona.getContacts().forEach(mail-> mail.setPersona(personaDatabase));
                    persona.getPhones().forEach(ph-> ph.setPersona(personaDatabase));

                    BeanUtils.copyProperties(persona, personaDatabase, "id", "createdAt");

                    finder.setPersona(personaDatabase);
                    this.finderRespository.save(finder);
                    System.out.println("##### FINDER JA EXISTENTE : " + indexDatabase++);
                }else{
                    finder.setPersona(persona);
                    this.finderRespository.save(finder);
                    System.out.println("##### NOVA PERSONA FINDER  : " + indexNew++);
                }
            }
        }
        return Boolean.TRUE;
    }

}
