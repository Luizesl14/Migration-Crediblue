package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.domain.Finder;
import com.migration.domain.Lead;
import com.migration.domain.enums.IncomeType;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.enums.ProponentType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Company;
import com.migration.domain.persona.aggregation.ComposeIncome;
import com.migration.domain.persona.aggregation.PersonaComposeIncome;
import com.migration.domain.persona.aggregation.Phone;
import com.migration.infrastructure.IFinderRespository;
import com.migration.infrastructure.IPersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class FinderService {

    @Autowired
    private IFinderRespository finderRespository;

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private ConvertLocalDataTime convert;

    @Autowired
    private CreateObject create;

    public Boolean findAll() {
        List<Finder> finders = this.finderRespository.findAll();
        System.out.println("Quantidade de finders do banco: " + finders.size());
        this.createPersona(finders);
        return Boolean.TRUE ;
    }

    @Transactional
    public Boolean createPersona (List<Finder> finderNormalized){
        Integer count = 0;
        for (Finder finder: finderNormalized) {
            Persona persona = new Persona();
            if(finder != null){

                List<Persona> personaDatabase = null;
                if(finder.getCpf() != null){
                    personaDatabase  = this.personaRepository.findByTaxIdOld(finder.getCpf());
                }

                persona.setPersonaType(PersonaType.NATURAL_PERSON);
                persona.setTaxId(finder.getCpf());
                persona.setName(finder.getName());

                if(finder.getAddress() != null ){
                    persona.getAddresses().add(this.create.createAddress(finder.getAddress(),
                            finder.getAddress().getCreatedAt()));
                }
                if(finder.getEmail() != null){
                    persona.getContacts().add(this.create.createEmail(finder.getEmail(), null));
                }
                if(finder.getTelephone() != null){
                    Phone phone = new Phone();
                    phone.setNumber(finder.getTelephone());
                    phone.setIsWhatsApp(Boolean.FALSE);
                    persona.getPhones().add(this.create.createPhone(phone, null));
                }

                if(personaDatabase != null){
                    List<Persona> personaNormalized = personaDatabase
                            .stream().filter(p -> p.getCpfCnpj() != null).toList();
                    if(personaNormalized != null){
                        persona.setId(personaNormalized.get(0).getId());
                        this.personaRepository.save(persona);
                        finder.setPersona(persona);
                        this.save(finder);
                    }
                    if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
                        System.out.println("New Person ** PF ** : " + persona.getName());
                    }else{
                        System.out.println("New Person ** PJ ** : " + persona.getCompanyData().getCorporateName());
                    }
                }else{
                    finder.setPersona(persona);
                    this.save(finder);
                    if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
                        System.out.println("New Person ** PF ** : " + persona.getName());
                    }else{
                        System.out.println("New Person ** PJ ** : " + persona.getCompanyData().getCorporateName());
                    }
                }
            }
        }

        return Boolean.TRUE;
    }


    @Transactional
    public void save (Finder finder) {
            this.finderRespository.save(finder);
            System.out.println("Persona save: " + finder.getPersona().getName() + " ** Finder **");
    }
}
