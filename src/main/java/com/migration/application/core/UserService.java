package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.application.shared.ExistsEntity;
import com.migration.domain.Investor;
import com.migration.domain.Partner;
import com.migration.domain.User;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Company;
import com.migration.domain.persona.aggregation.ContactEmail;
import com.migration.domain.persona.aggregation.PersonaPhone;
import com.migration.domain.persona.aggregation.Phone;
import com.migration.infrastructure.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Autowired
    private ConvertLocalDataTime convert;

    @Autowired
    private ExistsEntity existsEntity;

    public Boolean findAll() {
        List<User> users = this.userRepository.findAll();
        System.out.println("Quantidade de Users do banco: " + users.size());
        this.userResolver();
        this.verifyUser(users);
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

        if(user.getCpf() != null) {
            Persona personaDatabase = this.personaRepository.findByTaxId(user.getCpf());

            if (user.getCpf() != null) {
                persona.setPersonaType(
                        user.getCpf()
                                .length() <= 11 ? PersonaType.NATURAL_PERSON : PersonaType.LEGAL_PERSON);
                persona.setTaxId(user.getCpf());
            }

            if (persona.getPersonaType() != null) {
                if (persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)) {
                    persona.setName(user.getName().toUpperCase());
                } else {
                    Company company = new Company();
                    if (user.getName() != null)
                        company.setFantasyName(user.getName().toUpperCase());
                    company.setCorporateName(user.getName().toUpperCase());

                    persona.setCompanyData(company);
                }
            }

            if (user.getEmail() != null) {
                persona.getContacts().add(
                        this.create.createEmail(user.getEmail(), this.convert.covertLocalDataTimeToDate(user.getCreatedAt())));
            }
            if (user.getTelephone() != null) {
                Phone phone = new Phone();
                phone.setNumber(user.getTelephone());
                phone.setIsWhatsApp(Boolean.FALSE);
                persona.getPhones().add(this.create.createPhone(phone, null));
            }
            if (personaDatabase != null) {

                persona.getContacts().forEach(mail -> mail.setPersona(personaDatabase));
                persona.getPhones().forEach(ph -> ph.setPersona(personaDatabase));
                BeanUtils.copyProperties(persona, personaDatabase, "id", "createdAt");

                Persona personaSave = this.personaRepository.save(personaDatabase);
                user.setPersona(personaSave);
                this.save(user);
                System.out.println("Persona Atualizada ******* : " + persona.getName());

            } else {
                user.setPersona(persona);
                this.save(user);
                System.out.println("NOVA PERSONA ********** : " + persona.getName());
            }
        }
        return Boolean.TRUE;
    }

    public Boolean createPersonaByPartner(User user) {

            Persona persona = new Persona();

            if (user.getPartner().getId() != null) {
                Partner partnerDatabase = this.partnerRepository.findByPartnerId(user.getId());

             if(user.getCpf() != null){
                 persona.setPersonaType(
                         user.getCpf()
                                 .length() == 11 ? PersonaType.NATURAL_PERSON: PersonaType.LEGAL_PERSON);
                 persona.setTaxId(user.getCpf());
             }
                List<ContactEmail> contactEmailList = new ArrayList<>();
                if(user.getEmail() != null){
                    ContactEmail contactEmail = this.create.createEmail(
                            user.getEmail(), this.convert.covertLocalDataTimeToDate(user.getCreatedAt()));
                    contactEmailList.add(contactEmail);
                    persona.setContacts(contactEmailList);
                }

                List<PersonaPhone> personaPhoneList = new ArrayList<>();
                if(user.getTelephone() != null){
                    Phone phone = new Phone();
                    phone.setNumber(user.getTelephone());
                    phone.setIsWhatsApp(Boolean.FALSE);
                    PersonaPhone personaPhone = this.create.createPhone(
                            phone, this.convert.covertLocalDataTimeToDate(user.getCreatedAt()));
                    personaPhoneList.add(personaPhone);
                    persona.setPhones(personaPhoneList);
                }
                if(partnerDatabase != null){

                    persona.getContacts().forEach(mail -> mail.setPersona(partnerDatabase.getPersona()));
                    persona.getPhones().forEach(ph -> ph.setPersona(partnerDatabase.getPersona()));
                    BeanUtils.copyProperties(persona, partnerDatabase.getPersona(), "id", "createdAt");

                    Persona personaSave = this.personaRepository.save(partnerDatabase.getPersona());
                    user.setPersona(personaSave);
                    this.save(user);
                    System.out.println("Persona Atualizada ******* : " + persona.getName());

                    user.setPersona(partnerDatabase.getPersona());
                    this.save(user);
                }else{
                    System.out.println("CREATE NEW PERSONA ** : " + persona.getName());
                    this.createPersona(user);
                }

        }
        return Boolean.TRUE;
    }

    public Boolean createPersonaByInvestor(User user) {

        Persona persona = new Persona();
        if (user.getInvestor().getId() != null) {
            Investor investDatabase = this.investorRepository.findByInvestorId(user.getId());

            List<ContactEmail> contactEmailList = new ArrayList<>();
            if(user.getEmail() != null){
                ContactEmail contactEmail = this.create.createEmail(
                        user.getEmail(), this.convert.covertLocalDataTimeToDate(user.getCreatedAt()));
                contactEmailList.add(contactEmail);
                persona.setContacts(contactEmailList);
            }

            List<PersonaPhone> personaPhoneList = new ArrayList<>();
            if(user.getTelephone() != null){
                Phone phone = new Phone();
                phone.setNumber(user.getTelephone());
                phone.setIsWhatsApp(Boolean.FALSE);
                PersonaPhone personaPhone = this.create.createPhone(
                        phone, this.convert.covertLocalDataTimeToDate(user.getCreatedAt()));
                personaPhoneList.add(personaPhone);
                persona.setPhones(personaPhoneList);
            }
            if(investDatabase != null){

                persona.getContacts().forEach(mail -> mail.setPersona(investDatabase.getPersona()));
                persona.getPhones().forEach(ph -> ph.setPersona(investDatabase.getPersona()));
                BeanUtils.copyProperties(persona, investDatabase.getPersona(), "id", "createdAt");

                Persona personaSave = this.personaRepository.save(investDatabase.getPersona());

                user.setPersona(personaSave);
                this.save(user);
            }else{
                System.out.println("CREATE NEW PERSONA ** : " + persona.getName());
                this.createPersona(user);
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
                user.setCpf(token.substring(0,8));
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
