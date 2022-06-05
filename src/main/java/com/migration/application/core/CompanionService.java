package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.domain.ProposalProponent;
import com.migration.domain.enums.MaritalStatus;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.enums.ProponentType;
import com.migration.domain.enums.TypeRegimeCompanion;
import com.migration.domain.persona.Companion;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.PersonaCompanion;
import com.migration.infrastructure.IPersonaRepository;
import com.migration.infrastructure.IProposalProponentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class CompanionService {

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private ConvertLocalDataTime convert;

    @Autowired
    private CreateObject create;

    @Autowired
    private IProposalProponentRepository proposalProponentRepository;


    public Boolean createCompanion () {
        List<ProposalProponent> proponentsNomalized = this.proposalProponentRepository.findAll();
        System.out.println("################## TOTAL DE PROPONENTS NORMALIZADOS " + proponentsNomalized.size());


        for (ProposalProponent proponent: proponentsNomalized) {
            Companion companion = proponent.getPersona().getCompanion();

            if (companion != null) {
                Persona personaDatabase = this.personaRepository.findByTaxId(companion.getCpf());

                PersonaCompanion personaCompanion = new PersonaCompanion();
                Persona newPerson = new Persona();

                if (personaDatabase == null) {
                    newPerson.setName(companion.getName() != null ? companion.getName() : null);
                    newPerson.setNationality(companion.getNationality() != null ? companion.getNationality() : null);
                    newPerson.setOccupation(companion.getOccupation() != null ? companion.getOccupation() : null);
                    newPerson.setRg(companion.getRg() != null ? companion.getRg() : null);
                    newPerson.setOrgaoEmissor(companion.getOrgaoEmissor() != null ? companion.getOrgaoEmissor() : null);
                    newPerson.setTaxId(companion.getCpf() != null ? companion.getCpf() : null);

                    if(proponent.getPersona().getMaritalStatus().equals(MaritalStatus.CASADO)){
                        newPerson.setMaritalStatus(MaritalStatus.CASADO);
                    }

                    if (newPerson.getPersonaType() != null) {
                        newPerson.setPersonaType(PersonaType.NATURAL_PERSON);
                    }
                    if (companion.getEmail() != null) {
                        newPerson.getContacts().add(
                                this.create.createEmail(companion.getEmail(), null));
                    }
                    newPerson.setMotherName(companion.getMotherName() != null ? companion.getMotherName() : null);
                    newPerson.setBirthDate(companion.getBirthDate() != null ? companion.getBirthDate() : null);
                    newPerson.setPep(companion.getPep());

                    personaCompanion.setType(this.createType(proponent.getPersona()));
                    personaCompanion.setData(newPerson);
//                    this.personaRepository.save(newPerson);
                    proponent.getPersona().setPersonaCompanionId(personaCompanion);

                    System.out.println("################## Companio salvo " + companion.getName());
                    System.out.println();

                } else{
                        personaCompanion.setData(personaDatabase);
                        personaCompanion.setType(this.createType(personaDatabase));
                        proponent.getPersona().setPersonaCompanionId(personaCompanion);
//                        this.personaRepository.save(proponent.getPersona());
                        System.out.println("################## Companion salvo " + companion.getName());
                        System.out.println();
                    }
                }
            }
        return Boolean.TRUE;
    }


    public TypeRegimeCompanion createType(Persona persona){
        if(persona.getPropertySystem() != null){
            if (persona.getPropertySystem().name().equals(TypeRegimeCompanion.PARTIAL_COMMUNION.name())) {
                return TypeRegimeCompanion.PARTIAL_COMMUNION;

            } else if (persona.getPropertySystem().name().equals(TypeRegimeCompanion.TOTAL_SEPARATION.name())) {
                return  TypeRegimeCompanion.TOTAL_SEPARATION;

            } else if (persona.getPropertySystem().name().equals(TypeRegimeCompanion.UNIVERSIAL_COMMUNION.name())) {
                return TypeRegimeCompanion.UNIVERSIAL_COMMUNION;

            } else if (persona.getPropertySystem().name().equals(TypeRegimeCompanion.FINAL_PARTICIPATION_IN_AQUESTOS.name())) {
                return TypeRegimeCompanion.FINAL_PARTICIPATION_IN_AQUESTOS;
            }
        }
        return null;
    }


}
