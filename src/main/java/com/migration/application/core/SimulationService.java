package com.migration.application.core;


import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.domain.Lead;
import com.migration.domain.Simulation;
import com.migration.domain.enums.IncomeType;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.enums.ProponentType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.*;
import com.migration.infrastructure.ILeadRepository;
import com.migration.infrastructure.IPersonaRepository;
import com.migration.infrastructure.ISimulatonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class SimulationService {

    @Autowired
    private ISimulatonRepository simulatonRepository;

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private ILeadRepository leadRepository;


    @Autowired
    private ConvertLocalDataTime convert;

    @Autowired
    private CreateObject create;



    public Boolean findAll() {
        List<Simulation>  simulations = this.simulatonRepository.findAll();
        System.out.println("Quantidade de Simulação no banco: " + simulations.size());
        this.createPersona(simulations);
        return Boolean.TRUE ;
    }


    @Transactional
    public Boolean createPersona (List<Simulation> simulationsNormalized){
        for (Simulation simulation: simulationsNormalized) {
            Persona persona = new Persona();
            if( simulation.getLead() != null){
                List<Persona> personaDatabase = null;
                if(simulation.getLead().getCpfCnpj()!= null){
                    personaDatabase  = this.personaRepository.findByTaxIdOld(simulation.getLead().getCpfCnpj());
                    if(personaDatabase.size() == 0){
                        personaDatabase = null;
                    }
                }
                persona.setPersonaType(
                        simulation.getLead().getCpfCnpj()
                                .length() == 11 ? PersonaType.NATURAL_PERSON: PersonaType.LEGAL_PERSON);
                persona.setTaxId( simulation.getLead().getCpfCnpj());

                if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
                    persona.setName( simulation.getLead().getName());
                    persona.setMaritalStatus( simulation.getLead().getMaritalStatus());
                    persona.setBirthDate( simulation.getLead().getBirthDate());
                }else{
                    Company company = new Company();
                    company.setCorporateName( simulation.getLead().getName());
                    persona.setCompanyData(company);
                }

                persona.setMaritalStatus(simulation.getLead().getMaritalStatus());
                persona.setBirthDate(simulation.getLead().getBirthDate());
                persona.setMaritalStatus(simulation.getLead().getMaritalStatus());

                if(simulation.getLead().getSpouseName() != null){
                    PersonaCompanion personaCompanion = new PersonaCompanion();
                    Persona companion = new Persona();
                    companion.setName(simulation.getLead().getSpouseName());
                    personaCompanion.setData(companion);
                }

                if(simulation.getLead().getFamilyIncome() != null){
                    simulation.setFamilyIncome(simulation.getLead().getFamilyIncome());
                }

                if( simulation.getLead().getAddress() != null ){
                    persona.getAddresses().add(this.create.createAddress( simulation.getLead().getAddress(),
                            simulation.getLead().getAddress().getCreatedAt()));
                }
                if( simulation.getLead().getEmail() != null){
                    persona.getContacts().add(this.create.createEmail( simulation.getLead().getEmail(), null));
                }

                if( simulation.getLead().getTelephone() != null){
                    Phone phone = new Phone();
                    phone.setNumber( simulation.getLead().getTelephone());
                    phone.setIsWhatsApp(Boolean.FALSE);
                    persona.getPhones().add(this.create.createPhone(phone, null));
                }
                if(personaDatabase != null){    // sempre vai existir
                    List<Persona> personaNormalized = personaDatabase
                            .stream().filter(p -> p.getProponentType().equals(ProponentType.SPOUSE) && p.getCpfCnpj() != null).toList();

                    if(personaNormalized != null){
                        simulation.setPersona(personaNormalized.get(0));
                        this.saveSimulation(simulation);
                    }
                    if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
                        System.out.println("New Person ** PF ** : " + persona.getName());
                    }else{
                        System.out.println("New Person ** PJ ** : " + persona.getCompanyData().getCorporateName());
                    }
                }
            }
        }

        System.out.println("Total de Personas criadas:  " + simulationsNormalized.size());
        //this.save(leadNormalized);
        return Boolean.TRUE;
    }


    @Transactional
    public Boolean save(Persona persona){
            this.personaRepository.save(persona);
            System.out.println("*** Save persona : " + persona.getName());
        return Boolean.TRUE;
    }


    @Transactional
    public Boolean saveSimulation(Simulation simulation){
        this.simulatonRepository.save(simulation);
        System.out.println("*** Save simulation: " + simulation.getPersona().getName());
        return Boolean.TRUE;
    }
}
