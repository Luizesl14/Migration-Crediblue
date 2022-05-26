package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.domain.Finder;
import com.migration.domain.Lead;
import com.migration.domain.enums.IncomeType;
import com.migration.domain.enums.PersonaType;
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

import java.util.List;

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
        this.normalizationStepOne(finders);
        return Boolean.TRUE ;
    }

    public Boolean normalizationStepOne (List<Finder> findersDatabase){

        List<Finder> normalizationStepOne = findersDatabase
                .stream()
                .filter(finder -> finder.getCpf().equals(finder.getCpf())
                        && finder.getPartner().getId().equals(finder.getPartner().getId())
                ).toList();

        System.out.println("Finders Normalisados Step One: " + normalizationStepOne.size());
        this.createPersona(normalizationStepOne);
        return Boolean.TRUE;

    }


    @Transactional
    public Boolean createPersona (List<Finder> finderNormalized){

        Integer count = 0;
        for (Finder finder: finderNormalized) {
            Persona persona = new Persona();
            if(finder != null){
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
                finder.setPersona(persona);
                if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON))
                    count++;
                    System.out.println( count + " - New Person ** PF ** : " + persona.getName());
            }
        }
        System.out.println("Total de Personas criadas:  " + finderNormalized.size());
        this.save(finderNormalized);
        return Boolean.TRUE;
    }


    @Transactional
    public void save (List<Finder> findersNormalized) {
        for (Finder finder: findersNormalized){
            Persona persona = this.personaRepository.save(finder.getPersona());
            finder.setPersona(persona);
            this.finderRespository.save(finder);
            System.out.println("Persona save: " + persona.getName() + " ** Finder **");
        }
    }
}
