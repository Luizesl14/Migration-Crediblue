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
            if(finder != null){
                Persona personaDatabase = null;
                if(finder.getCpf() != null){
                    personaDatabase  = this.personaRepository.findByTaxId(finder.getCpf());
                    if(personaDatabase != null){
                            System.out.println("Persona já existe no banco ** PF ** : " + personaDatabase.getName());
                    }
                }
                persona.setPersonaType(PersonaType.NATURAL_PERSON);
                persona.setTaxId(finder.getCpf());
                persona.setName(finder.getName().toUpperCase());

                List<PersonaAddress> personaAddressList = new ArrayList<>();
                if(finder.getAddress() != null){
                    PersonaAddress personaAddress = this.create.createAddress(finder.getAddress(), finder.getAddress().getCreatedAt(), persona);
                    personaAddressList.add(personaAddress);
                    persona.setAddresses(personaAddressList);

                }

                List<ContactEmail> contactEmailList = new ArrayList<>();
                if(finder.getEmail() != null){
                    ContactEmail contactEmail = this.create.createEmail(finder.getEmail(), this.convert.covertLocalDataTimeToDate(finder.getCreatedAt()));
                    contactEmailList.add(contactEmail);
                    persona.setContacts(contactEmailList);
                }

                List<PersonaPhone> personaPhoneList = new ArrayList<>();
                if(finder.getTelephone() != null){
                    Phone phone = new Phone();
                    phone.setNumber(finder.getTelephone());
                    phone.setIsWhatsApp(Boolean.FALSE);
                    PersonaPhone personaPhone = this.create.createPhone(phone, this.convert.covertLocalDataTimeToDate(finder.getCreatedAt()));
                    personaPhoneList.add(personaPhone);
                    persona.setPhones(personaPhoneList);
                }
                if(personaDatabase != null){

                    if (personaDatabase.getPersonaType().equals(PersonaType.NATURAL_PERSON)) {
                        personaDatabase.setName(finder.getName().toUpperCase());
                    }
                    if (personaDatabase.getPersonaType().equals(PersonaType.LEGAL_PERSON)) {
                        personaDatabase.getCompanyData().setFantasyName(finder.getName().toUpperCase());
                        personaDatabase.getCompanyData().setCorporateName(finder.getName().toUpperCase());
                    }

                    if(!personaAddressList.isEmpty()
                            && this.existsEntity.verifyAddress(personaDatabase.getAddresses(), personaAddressList)
                            .equals(Boolean.TRUE)){

                        System.out.println("-----------ADDRESS DIFERENTE ADICIONADO-----------");
                        personaDatabase.getAddresses().addAll(personaAddressList);
                    }
                    if(!contactEmailList.isEmpty()
                            && this.existsEntity.verifyEmail(personaDatabase.getContacts(), contactEmailList)
                            .equals(Boolean.TRUE)){

                        System.out.println("-----------EMAIL DIFERENTE ADICIONADO-----------");
                        personaDatabase.getContacts().addAll(contactEmailList);
                    }

                    if(!personaPhoneList.isEmpty()
                            && this.existsEntity.verifyPhone(personaDatabase.getPhones(),personaPhoneList )
                            .equals(Boolean.TRUE)){
                        System.out.println("-----------PHONE DIFERENTE ADICIONADO-----------");
                        personaDatabase.getPhones().addAll(personaPhoneList);
                    }
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
