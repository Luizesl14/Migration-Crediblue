package com.migration.application.core;

import com.migration.application.shared.CreateObject;
import com.migration.domain.User;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Phone;
import com.migration.infrastructure.IContactEmailRepository;
import com.migration.infrastructure.IPersonaRepository;
import com.migration.infrastructure.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        this.createPersona(users);
        return Boolean.TRUE;
    }



    @Transactional
    public Boolean createPersona(List<User> users) {

        for (User user: users) {
            Persona persona = new Persona();
            List<Persona> personaDatabase = null;

            if (user.getCpf() != null) {
                personaDatabase = this.personaRepository.findByTaxIdOld(user.getCpf());

                if (personaDatabase.size() == 0) {
                    personaDatabase = null;
                }

                persona.setPersonaType(
                        user.getCpf()
                                .length() == 11 ? PersonaType.NATURAL_PERSON : PersonaType.LEGAL_PERSON);
                persona.setTaxId(user.getCpf());
            }
            if (user.getTelephone() != null) {
                Phone phone = new Phone();
                phone.setNumber(user.getTelephone());
                phone.setIsWhatsApp(Boolean.FALSE);
                persona.getPhones().add(this.create.createPhone(phone, null));
            }

            if (personaDatabase != null) {
                List<Persona> personaNormalized = personaDatabase
                        .stream().filter(p -> p.getCpfCnpj() != null).toList();
                if (personaNormalized != null) {
                    persona.setId(personaNormalized.get(0).getId());
                    this.personaRepository.save(persona);
                    user.setPersona(persona);
                    this.save(user);
                }
                if (persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)) {
                    System.out.println("New Person ** PF ** : " + persona.getName());
                } else {
                    System.out.println("New Person ** PJ ** : " + persona.getCompanyData().getCorporateName());
                }

            } else {
                user.setPersona(persona);
                this.save(user);
                if (persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)) {
                    System.out.println("New Person ** PF ** : " + persona.getName());
                } else {
                    System.out.println("New Person ** PJ ** : " + persona.getCompanyData().getCorporateName());
                }
            }
        }
        return Boolean.TRUE;
    }


    @Transactional
    public void save(User user) {
            this.userRepository.save(user);
            System.out.println("Persona save: " + user.getPersona().getName());
    }
}
