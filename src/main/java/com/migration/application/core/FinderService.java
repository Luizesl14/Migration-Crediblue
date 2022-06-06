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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private CreateObject create;

    public Boolean findAll() {
        List<Finder> finders = this.finderRespository.findAll();
        System.out.println("Quantidade de finders do banco: " + finders.size());
        this.createPersona(finders);
        return Boolean.TRUE ;
    }


    public Boolean createPersona (List<Finder> finderNormalized){
        Integer count = 0;
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
                        BeanUtils.copyProperties(persona ,personaDatabase, "id", "taxId", "cpf", "createdAt");
                        Persona personaSave = this.personaRepository.save(personaDatabase);
                        finder.setPersona(personaSave);
                        this.save(finder);
                    System.out.println();
                }else{
                    finder.setPersona(persona);
                    this.save(finder);
                    System.out.println();
                }
            }
        }
        return Boolean.TRUE;
    }


    public void save (Finder finder) {
            Persona persona = this.finderRespository.save(finder).getPersona();
            System.out.println("Persona salva ** PF ** : " + persona.getName());
            System.out.println();
    }
}
