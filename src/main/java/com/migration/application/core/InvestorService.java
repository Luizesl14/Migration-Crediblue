package com.migration.application.core;

import com.migration.application.shared.CreateObject;
import com.migration.domain.Investor;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Phone;
import com.migration.infrastructure.IInvestorRepository;
import com.migration.infrastructure.IPersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InvestorService {

    @Autowired
    private IInvestorRepository investorRepository;

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private CreateObject create;

    public Boolean findAll() {
        List<Investor> investors = this.investorRepository.findAll();
        System.out.println("Quantidade de investors do banco: " + investors.size());
        this.normalizationStepOne(investors);
        return Boolean.TRUE ;
    }

    public Boolean normalizationStepOne (List<Investor> investorsDatabase){

        List<Investor> normalizationStepOne = investorsDatabase
                .stream()
                .filter(investor -> investor.getCnpj().equals(investor.getCnpj())
                ).toList();

        System.out.println("Investors Normalisados Step One: " + normalizationStepOne.size());
        normalizationStepOne.forEach(System.out::println);

        this.updatePersona(normalizationStepOne);
        return Boolean.TRUE;

    }

    @Transactional
    public Boolean updatePersona(List<Investor> partnersNormalized){

        Investor investor = partnersNormalized.stream().findFirst().get();
        Persona personaDatabase = this.personaRepository.findByTaxId(investor.getCnpj());
        Persona createPartner = this.createPersona(investor);

        if(personaDatabase != null){
            createPartner.setId(personaDatabase.getId());
            this.personaRepository.save(createPartner);
        }else {
            partnersNormalized.forEach(findersAdd -> {
                findersAdd.setPersona(createPartner);
            });
        }
        System.out.println("Findes Finais: " + partnersNormalized.size());
        partnersNormalized.forEach(System.out::println);

        this.save(partnersNormalized);
        return Boolean.TRUE;
    }


    @Transactional
    public Persona createPersona ( Investor investor) {

        Persona persona = new Persona();
        persona.setName(investor.getName());
        persona.setTaxId(investor.getCnpj());
        persona.setPersonaType(PersonaType.LEGAL_PERSON);

        if(investor.getEmail() != null){
            persona.getContacts().add(this.create.createEmail(investor.getEmail(), null));
        }

        if(investor.getTelephone() != null){
            Phone phone = new Phone();
            phone.setNumber(investor.getTelephone());
            phone.setIsWhatsApp(Boolean.FALSE);
            persona.getPhones().add(this.create.createPhone(phone, null));
        }
        return  persona;
    }


    @Transactional
    public Boolean save(List<Investor> investorslized){
        this.investorRepository.saveAll(investorslized);
        investorslized.forEach(investor -> System.out.println("Investidor salvo " + investorslized));
        return Boolean.TRUE;
    }

}
