package com.migration.application.core;


import com.migration.domain.LeadProposal;
import com.migration.domain.Simulation;
import com.migration.domain.persona.Persona;
import com.migration.infrastructure.IPersonaRepository;
import com.migration.infrastructure.SimulatonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SimulationService {

    @Autowired
    private SimulatonRepository simulatonRepository;

    @Autowired
    private IPersonaRepository personaRepository;

    public void findAll() {
        List<Simulation> simulations = this.simulatonRepository.findAll();
        System.out.println("Quantidade de Simulações no banco: " + simulations.size());

        simulations.forEach(simulation -> {
            Persona persona = this.personaRepository.findByTaxId(simulation.getLead().getCpfCnpj());
            if(persona != null){
                simulation.setPersona(persona);
                this.save(simulation);
            }
        });
    }


    @Transactional
    public void save (Simulation simulation) {
        this.simulatonRepository.save(simulation);
        System.out.println("Simulação salva com Persona " + simulation);
    }
}
