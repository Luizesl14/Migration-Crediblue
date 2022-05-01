package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.domain.Finder;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.persona.Persona;
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
                ).toList();

        System.out.println("Finders Normalisados Step One: " + normalizationStepOne.size());
        normalizationStepOne.forEach(System.out::println);

        this.normalizationStepTwo(normalizationStepOne);
        return Boolean.TRUE;

    }

    public Boolean normalizationStepTwo (List<Finder> finders){

        List<Finder> normalizationTwo = null;

        if(finders.size() > 1){
            normalizationTwo = finders.stream()
                    .filter(lead ->
                            lead.getPartner().getId().equals(lead.getPartner().getId())
                    ).toList();

            System.out.println("Finders Normalisados Step Two: " + normalizationTwo.size());
            normalizationTwo.forEach(System.out::println);

            this.updatePersona(normalizationTwo);
        }else {
            this.updatePersona(finders);
        }
        return  Boolean.TRUE;
    }


    @Transactional
    public Boolean updatePersona(List<Finder> findersNormalized){

        Finder finder = findersNormalized.stream().findFirst().get();
        Persona personaDatabase = this.personaRepository.findByTaxId(finder.getCpf());
        Persona persona = this.createPersona(finder);

        if(personaDatabase != null){
            persona.setId(personaDatabase.getId());
            this.personaRepository.save(persona);
        }else {
            findersNormalized.forEach(findersAdd -> {
                findersAdd.setPersona(persona);
            });
        }
        System.out.println("Findes Finais: " + findersNormalized.size());
        findersNormalized.forEach(System.out::println);

        this.save(findersNormalized);
        return Boolean.TRUE;
    }


    @Transactional
    public Persona createPersona ( Finder finder) {

            Persona persona = new Persona();
            persona.setName(finder.getName());
            persona.setTaxId(finder.getCpf());
            persona.setPersonaType(PersonaType.NATURAL_PERSON);

            if(finder.getAccountInfo() != null){
                persona.getBankAccounts().add(
                        this.create.createAccount(finder.getAccountInfo(), null));
            }
            if(finder.getAddress() != null){
                persona.getAddresses().add(
                        this.create.createAddress(finder.getAddress(), finder.getAddress().getCreatedAt()));
            }
            if(finder.getEmail() != null){
                persona.getContacts().add(
                        this.create.createEmail(finder.getEmail(), null));
            }
            if(finder.getTelephone() != null){
                Phone phone = new Phone();
                phone.setNumber(finder.getTelephone());
                phone.setIsWhatsApp(Boolean.FALSE);
                persona.getPhones().add(this.create.createPhone(phone, null));
            }
            return  persona;
    }

    @Transactional
    public Boolean save (List<Finder> findersNormalized) {

        this.finderRespository.saveAll(findersNormalized);
        findersNormalized.forEach(finder -> System.out.println("Finders salvo " + findersNormalized));
        return Boolean.TRUE;
    }
}
