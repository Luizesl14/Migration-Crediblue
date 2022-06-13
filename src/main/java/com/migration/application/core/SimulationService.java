package com.migration.application.core;


import com.migration.application.shared.CreateObject;
import com.migration.domain.Simulation;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Company;
import com.migration.domain.persona.aggregation.Phone;
import com.migration.infrastructure.IPersonaRepository;
import com.migration.infrastructure.ISimulatonRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class SimulationService {

    @Autowired
    private ISimulatonRepository simulatonRepository;

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private CreateObject create;

    public Boolean findAll() {
        List<Simulation>  simulations = this.simulatonRepository.findAll();
        System.out.println("Quantidade de Simulação no banco: " + simulations.size());
        this.createPersona(simulations);
        return Boolean.TRUE ;
    }

    public Boolean createPersona (List<Simulation> simulationsNormalized){

        List<Simulation> simulationList = new ArrayList<>();
        int index = 0;
        for (Simulation simulation: simulationsNormalized) {
            Persona persona = new Persona();
            if( simulation.getLead() != null){
              Persona personaDatabase = null;
                if(simulation.getLead().getCpfCnpj()!= null)
                    personaDatabase  = this.personaRepository.findByTaxId(simulation.getLead().getCpfCnpj());

                persona.setPersonaType(
                        simulation.getLead().getCpfCnpj()
                                .length() == 11 ? PersonaType.NATURAL_PERSON: PersonaType.LEGAL_PERSON);
                persona.setTaxId( simulation.getLead().getCpfCnpj());

                if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
                    persona.setName( simulation.getLead().getName().toUpperCase());
                    persona.setMaritalStatus( simulation.getLead().getMaritalStatus());
                    persona.setBirthDate( simulation.getLead().getBirthDate());
                }else{
                    Company company = new Company();
                    if(simulation.getLead().getName() != null){
                        company.setFantasyName(simulation.getLead().getName().toUpperCase());
                        company.setCorporateName(simulation.getLead().getName().toUpperCase());
                    }
                    persona.setCompanyData(company);
                }

                persona.setMaritalStatus(simulation.getLead().getMaritalStatus());
                persona.setBirthDate(simulation.getLead().getBirthDate());
                persona.setMaritalStatus(simulation.getLead().getMaritalStatus());

                if(simulation.getLead().getFamilyIncome() != null){
                    simulation.setFamilyIncome(simulation.getLead().getFamilyIncome());
                }

                if( simulation.getLead().getAddress() != null ){
                    persona.getAddresses().add(this.create.createAddress( simulation.getLead().getAddress(),
                            simulation.getLead().getAddress().getCreatedAt(), persona));
                }
                if(simulation.getLead().getEmail() != null){
                    persona.getContacts().add(this.create.createEmail( simulation.getLead().getEmail(), null));
                }

                if( simulation.getLead().getTelephone() != null){
                    Phone phone = new Phone();
                    phone.setNumber( simulation.getLead().getTelephone());
                    phone.setIsWhatsApp(Boolean.FALSE);
                    persona.getPhones().add(this.create.createPhone(phone, null));
                }
                if(personaDatabase != null){
                    simulation.setPersona(personaDatabase);
                    simulationList.add(simulation);
                    System.out.println("SIMULATION NORMALIZED " + index++);
                    }else {
                    Persona personaUpdated = this.personaRepository.save(persona);
                    simulation.setPersona(personaUpdated);
                    simulationList.add(simulation);
                    System.out.println("SIMULATION NORMALIZED " + index++);
                }
            }
        }
        this.simulatonRepository.saveAll(simulationList);
        return Boolean.TRUE;
    }

}
