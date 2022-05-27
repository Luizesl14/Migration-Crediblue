package com.migration.application.core;

import com.migration.application.shared.CreateObject;
import com.migration.domain.Partner;
import com.migration.domain.User;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Company;
import com.migration.domain.persona.aggregation.ContactEmail;
import com.migration.domain.persona.aggregation.Phone;
import com.migration.infrastructure.IContactEmailRepository;
import com.migration.infrastructure.IPersonaRepository;
import com.migration.infrastructure.IUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private IContactEmailRepository contactEmailRepository;

    @Autowired
    private CreateObject create;


    public Boolean findAll() {
        List<User> users = this.userRepository.findAll();
        System.out.println("Quantidade de Users do banco: " + users.size());
        this.normalizationStepOne(users);
        return Boolean.TRUE;
    }


    public Boolean normalizationStepOne(List<User> usersDatabase) {
        List<User> usersNormalized = new ArrayList<>();
        List<User> usersRemaining = new ArrayList<>();

        for (User user : usersDatabase) {
            if (!Objects.equals(user.getEmail(), user.getEmail())
                    && !Objects.equals(user.getLogin(), user.getLogin())) {
                usersRemaining.add(user);
            } else {
                usersNormalized.add(user);
            }
        }
        System.out.println("Lead Normalised: " + usersNormalized.size());
        System.out.println("lead Remaining: " + usersRemaining.size());

        for (User user : usersNormalized) {
            if(user.getPartner() != null){
                this.createPersonaPartner(user);
            }else if(user.getPartner() == null && user.getInvestor() == null){
                this.createPersona(user);
            }else if(user.getInvestor() != null){
                this.createPersonaInvestor(user);
            }
        }
        return Boolean.TRUE;
    }


    @Transactional
    public Boolean createPersona(User user) {
        Persona persona = new Persona();
        Persona personaDatabase = null;

        if( user.getCpf() != null){
           personaDatabase  = this.personaRepository.findByTaxId(user.getCpf());
            persona.setPersonaType(
                    user.getCpf()
                            .length() == 11 ? PersonaType.NATURAL_PERSON: PersonaType.LEGAL_PERSON);
            persona.setTaxId(user.getCpf());
        }
        if(user.getTelephone() != null){
            Phone phone = new Phone();
            phone.setNumber(user.getTelephone());
            phone.setIsWhatsApp(Boolean.FALSE);
            persona.getPhones().add(this.create.createPhone(phone, null));
        }

        if(personaDatabase != null){
            user.setPersona(personaDatabase);
            //BeanUtils.copyProperties(persona, personaDatabase , "createdAt");
            //this.personaRepository.save(personaDatabase);

        }else{
            user.setPersona(persona);
            //this.personaRepository.save(persona);
        }
        System.out.println("New Person ** Partner PF ** : " +  user.getId() + " " + user.getName());
        //this.save(user);
        return Boolean.TRUE;
    }

    public Persona getPersonaByTaxId(String taxId){
        return this.personaRepository.findByTaxId(taxId);
    }

    @Transactional
    public Boolean createPersonaPartner(User user) {

        Persona personaDatabasePartner = null;
        if(user.getPartner().getCpfCnpj() != null) {
            personaDatabasePartner = this.getPersonaByTaxId(user.getPartner().getCpfCnpj());
        }
        if (personaDatabasePartner != null) {
            user.setPersona(personaDatabasePartner);
            //BeanUtils.copyProperties(persona, personaDatabase , "createdAt");
            //this.personaRepository.save(personaDatabase);
            if (personaDatabasePartner.getPersonaType().equals(PersonaType.NATURAL_PERSON)) {
                System.out.println("New Person ** Partner PF ** : "
                        + personaDatabasePartner.getId() + "-" + personaDatabasePartner.getName());
            } else {
                System.out.println("New Person ** Partner PJ ** : "
                        + personaDatabasePartner.getId() + "-" + personaDatabasePartner.getCompanyData().getCorporateName());
            }
        }
        //this.save(user);
        return Boolean.TRUE;
    }


    @Transactional
    public Boolean createPersonaInvestor(User user) {
        Persona personaDatabaseFinder = null;
        if(user.getInvestor().getCnpj() != null) {
            personaDatabaseFinder = this.getPersonaByTaxId(user.getInvestor().getCnpj());
        }
        if (personaDatabaseFinder != null) {
            user.setPersona(personaDatabaseFinder);
            //BeanUtils.copyProperties(persona, personaDatabase , "createdAt");
            //this.personaRepository.save(personaDatabase);
            if (personaDatabaseFinder.getPersonaType().equals(PersonaType.NATURAL_PERSON)) {
                System.out.println("New Person ** Partner PF ** : "
                        + personaDatabaseFinder.getId() + "-" + personaDatabaseFinder.getName());
            } else {
                System.out.println("New Person ** Partner PJ ** : "
                        + personaDatabaseFinder.getId() + "-" + personaDatabaseFinder.getCompanyData().getCorporateName());
            }

        }
        //this.save(user);
        return Boolean.TRUE;
    }


    @Transactional
    public void save(User user) {
            this.userRepository.save(user);
            System.out.println("Persona save: " + user.getPersona().getName());
    }
}
