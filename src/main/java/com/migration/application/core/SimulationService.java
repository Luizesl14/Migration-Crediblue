package com.migration.application.core;


import com.migration.application.shared.CreateObject;
import com.migration.domain.Simulation;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Company;
import com.migration.domain.persona.aggregation.Phone;
import com.migration.infrastructure.IPersonaRepository;
import com.migration.infrastructure.ISimulatonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        for (Simulation simulation: simulationsNormalized) {
            Persona persona = new Persona();
            if( simulation.getLead() != null){
              Persona personaDatabase = null;
                if(simulation.getLead().getCpfCnpj()!= null){
                    personaDatabase  = this.personaRepository.findByTaxId(simulation.getLead().getCpfCnpj());
                    if(personaDatabase != null)
                        this.print(personaDatabase);
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
                    company.setFantasyName( simulation.getLead().getName());
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
                if( simulation.getLead().getEmail() != null){
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
                        this.saveSimulation(simulation);
                    }else {
                    Persona personaUpdated = this.personaRepository.save(persona);
                    simulation.setPersona(personaUpdated);
                    this.saveSimulation(simulation);
                    System.out.println();
                }
            }
        }
        return Boolean.TRUE;
    }

    public Boolean saveSimulation(Simulation simulation){
        Persona persona = this.simulatonRepository.save(simulation).getPersona();
        if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
            System.out.println("SIMULAÇÃO ATUALIZADA ** PF ** : " + persona.getName());
            System.out.println();
        }else{
            System.out.println("SIMULAÇÃO ATUALIZADA ** PJ ** : " + persona.getCompanyData().getCorporateName());
            System.out.println();
        }
        return Boolean.TRUE;
    }


    public void print(Persona persona){
        if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
            System.out.println("*********** Existe Persona: PF ** : " + persona.getName());
        }else{
            System.out.println("*********** Existe Persona: ** PJ ** : " + persona.getCompanyData().getCorporateName());
        }
    }

    public void printSavePersona(Persona persona){
        if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
            System.out.println("######### Persona Atualizada: PF ** : " + persona.getName());
        }else{
            System.out.println("######### Persona Atualizada: ** PJ ** : " + persona.getCompanyData().getCorporateName());
        }
    }
}
