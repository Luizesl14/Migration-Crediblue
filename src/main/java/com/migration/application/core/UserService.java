package com.migration.application.core;

import com.migration.application.shared.CreateObject;
import com.migration.domain.Investor;
import com.migration.domain.Partner;
import com.migration.domain.User;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Phone;
import com.migration.infrastructure.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Transactional
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

    @Autowired
    private IPartnerRepository partnerRepository;

    @Autowired
    private IInvestorRepository investorRepository;


    public Boolean findAll() {
        List<User> users = this.userRepository.findAll();
        System.out.println("Quantidade de Users do banco: " + users.size());
        this.verifyUser(users);
        this.userResolver();
        return Boolean.TRUE;
    }

    public void verifyUser(List<User> users){
        for (User user : users) {
            if(user.getPartner() != null){
                this.createPersonaByPartner(user);
            }else if(user.getPartner() == null && user.getInvestor() == null){
                this.createPersona(user);
            }else if(user.getInvestor() != null){
                this.createPersonaByInvestor(user);
            }
        }
    }

    public Boolean createPersona(User user) {
        Persona persona = new Persona();
       Persona personaDatabase = this.personaRepository.findPersonaUser(
                user.getName(), user.getEmail(), user.getTelephone(), user.getCpf());

            if (user.getCpf() != null) {
                persona.setPersonaType(
                        user.getCpf()
                                .length() == 11 ? PersonaType.NATURAL_PERSON : PersonaType.LEGAL_PERSON);
                persona.setTaxId(user.getCpf());
            }
            persona.setName(user.getName());
            if (user.getEmail() != null) {
                persona.getContacts().add(
                        this.create.createEmail(user.getEmail(), user.getCreatedAt()));
            }
            if (user.getTelephone() != null) {
                Phone phone = new Phone();
                phone.setNumber(user.getTelephone());
                phone.setIsWhatsApp(Boolean.FALSE);
                persona.getPhones().add(this.create.createPhone(phone, null));
            }
            if (personaDatabase != null) {
                    persona.setId(personaDatabase.getId());
                    BeanUtils.copyProperties(persona ,personaDatabase, "id", "taxId", "cpf", "createdAt");
                    this.personaRepository.save(personaDatabase);
                    user.setPersona(persona);
                    this.save(user);

                    if (persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)) {
                        System.out.println("Persona Atualizada ** PF ** : " + persona.getName());
                    } else {
                        System.out.println("Persona Atualizada ** PJ ** : " + persona.getCompanyData().getCorporateName());
                    }
            }else {
                user.setPersona(persona);
                this.save(user);
                if (persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)) {
                    System.out.println("New Person ** PF ** : " + persona.getName());
                } else {
                    System.out.println("New Person ** PJ ** : " + persona.getCompanyData().getCorporateName());
                }
            }
        return Boolean.TRUE;
    }

    public Boolean createPersonaByPartner(User user) {

            Persona persona = new Persona();
            Partner partnerDatabase = null;
            if (user.getPartner().getId() != null) {
                partnerDatabase = this.partnerRepository.findByPartnerId(user.getId());

             if(user.getCpf() != null){
                 persona.setPersonaType(
                         user.getCpf()
                                 .length() == 11 ? PersonaType.NATURAL_PERSON: PersonaType.LEGAL_PERSON);
                 persona.setTaxId(user.getCpf());
             }

            if (user.getEmail() != null) {
                persona.getContacts().add(
                        this.create.createEmail(user.getEmail(), user.getCreatedAt()));
            }
            if (user.getTelephone() != null) {
                Phone phone = new Phone();
                phone.setNumber(user.getTelephone());
                phone.setIsWhatsApp(Boolean.FALSE);
                persona.getPhones().add(this.create.createPhone(phone, null));
            }
            if (partnerDatabase != null) {
                persona.setId(partnerDatabase.getPersona().getId());
                BeanUtils.copyProperties(persona ,partnerDatabase.getPersona(), "id", "taxId", "cpf", "createdAt");
                this.personaRepository.save(partnerDatabase.getPersona());
                user.setPersona(persona);
                this.save(user);
                if(partnerDatabase.getPersona().getPersonaType() != null){
                    if (partnerDatabase.getPersona().getPersonaType().equals(PersonaType.NATURAL_PERSON)) {
                        System.out.println("Persona do parceiro Atualizada ** PF ** : " + partnerDatabase.getPersona().getName());
                    } else {
                        System.out.println("Persona do parceiro Atualizada ** PJ ** : " + partnerDatabase.getPersona().getCompanyData().getCorporateName());
                    }
                }

            }else {
                    System.out.println("INTERNAL ERROR 500 ** : " + persona.getName());
            }
        }
        return Boolean.TRUE;
    }

    public Boolean createPersonaByInvestor(User user) {

        Persona persona = new Persona();
        Investor partnerDatabase = null;
        if (user.getInvestor().getId() != null) {
            partnerDatabase = this.investorRepository.findByInvestorId(user.getId());

            if (user.getEmail() != null) {
                persona.getContacts().add(
                        this.create.createEmail(user.getEmail(), user.getCreatedAt()));
            }
            if (user.getTelephone() != null) {
                Phone phone = new Phone();
                phone.setNumber(user.getTelephone());
                phone.setIsWhatsApp(Boolean.FALSE);
                persona.getPhones().add(this.create.createPhone(phone, null));
            }
            if (partnerDatabase != null) {
                persona.setId(partnerDatabase.getPersona().getId());
                BeanUtils.copyProperties(persona ,partnerDatabase.getPersona(), "id", "name", "taxId", "cpf", "createdAt");
                this.personaRepository.save(partnerDatabase.getPersona());
                user.setPersona(persona);
                this.save(user);
                if (persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)) {
                    System.out.println("Persona do investidor Atualizada ** PF ** : " + persona.getName());
                } else {
                    System.out.println("Persona do investidor Atualizada ** PJ ** : " + persona.getCompanyData().getCorporateName());
                }
            }else {
                System.out.println("INTERNAL ERROR 500 ** : " + persona.getName());
            }
        }
        return Boolean.TRUE;
    }



    public void userResolver(){
        List<User> usersNull = this.userRepository.findByUserTaxIdIsNull();
        System.out.println("### Persona database " + usersNull.size());
        int index = 0;
        for (User user: usersNull) {
            Persona persona = this.personaRepository.findPersonaUser(
                    user.getName(), user.getEmail(), user.getTelephone(), null);
            if(persona != null){
                System.out.println("### Persona encontrada " + persona.getName() + "index " + index++);
                user.setPersona(persona);
                this.userRepository.save(user);
            }else{
                String token = UUID.randomUUID().toString().toUpperCase(Locale.ROOT);
                user.setCpf(token.substring(0,10));
                User userDatabase = this.userRepository.save(user);
                System.out.println("### Persona não encontrada set token " + userDatabase.getCpf());
            }
        }

    }

    public void save(User user) {
            this.userRepository.save(user);
            System.out.println("Persona save: " + user.getPersona().getName());
    }
}
