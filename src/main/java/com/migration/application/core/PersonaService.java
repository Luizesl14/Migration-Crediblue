package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.domain.ProposalProponent;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.enums.ProponentType;
import com.migration.domain.enums.TypeRegimeCompanion;
import com.migration.domain.persona.Companion;
import com.migration.domain.persona.OldPersona;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Company;
import com.migration.domain.persona.aggregation.PersonaCompanion;
import com.migration.domain.persona.aggregation.Phone;
import com.migration.infrastructure.IOldPersonaRepository;
import com.migration.infrastructure.IPersonaRepository;
import com.migration.infrastructure.IProposalProponentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PersonaService {

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private IOldPersonaRepository oldPersonaRepository;

    @Autowired
    private ConvertLocalDataTime convert;

    @Autowired
    private CreateObject create;

    @Autowired
    private IProposalProponentRepository proposalProponentRepository;


    public void findAll() {
        List<Persona> oldPersonas = this.personaRepository.findAll();
        System.out.println("Quantidade de leads OldPersonas do banco: " + oldPersonas.size());
        this.saveProponent(oldPersonas);
        this.createPersona(oldPersonas);
    }


    @Transactional
    public Boolean createPersona (List<Persona> oldPersonas) {

        for (Persona oldPersona: oldPersonas) {
            List<Persona> personaDatabases = null;
            if(oldPersona.getCpfCnpj()!= null){
                personaDatabases = this.personaRepository.findByTaxIdOld(oldPersona.getCpfCnpj());
                        if(personaDatabases != null && !personaDatabases.isEmpty()){
                            personaDatabases.forEach(o->{
                                System.out.println("  ## ID ##: "+ o.getId()  + " ###################### Persona já existente: " + o.getName());
                            });
                        }
                }
            Persona newPerson = new Persona();
            newPerson.setTaxId(oldPersona.getCpfCnpj());
            newPerson.setId(oldPersona.getId());

            if(oldPersona.getCpfCnpj() != null){
                newPerson.setPersonaType(
                        oldPersona.getCpfCnpj()
                                .length() == 11 ? PersonaType.NATURAL_PERSON: PersonaType.LEGAL_PERSON);
            }
            if(newPerson.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
                newPerson.setName(oldPersona.getName());
            }
            newPerson.setMaritalStatus(oldPersona.getMaritalStatus());
            newPerson.setBirthDate(oldPersona.getBirthDate());
            newPerson.setMaritalStatus(oldPersona.getMaritalStatus());
            newPerson.setRg(oldPersona.getRg());
            newPerson.setOrgaoEmissor(oldPersona.getOrgaoEmissor());
            newPerson.setNationality(oldPersona.getNationality());
            newPerson.setMotherName(oldPersona.getMotherName());
            newPerson.setCitizenship(oldPersona.getCitizenship());
            newPerson.setOccupation(oldPersona.getOccupation());
            newPerson.setSourceIncome(oldPersona.getSourceIncome());

            if(oldPersona.getFinancialInstitutionCode() != null){
                newPerson.getBankAccounts().add(
                        this.create.createAccount(
                                oldPersona.getFinancialInstitutionCode(),oldPersona.getAccountBranch(),
                                oldPersona.getAccountNumber(), oldPersona.getAccountDigit(), null));
            }
            if(oldPersona.getAddress() != null){
                newPerson.getAddresses().add(
                        this.create.createAddress(oldPersona.getAddress(), oldPersona.getAddress().getCreatedAt()));
            }
            if(oldPersona.getEmail() != null){
                newPerson.getContacts().add(
                        this.create.createEmail(oldPersona.getEmail(), null));
            }
            if(oldPersona.getTelephone() != null){
                Phone phone = new Phone();
                phone.setNumber(oldPersona.getTelephone());
                phone.setIsWhatsApp(Boolean.FALSE);
                newPerson.getPhones().add(this.create.createPhone(phone, null));
            }

            if(newPerson.getPersonaType().equals(PersonaType.LEGAL_PERSON)){
                Company company = new Company();
                company.setCorporateName(oldPersona.getName());
                if(oldPersona.getOpeningDate() != null){
                    company.setFoundationDate(this.convert.convertToLocalDate(oldPersona.getOpeningDate()));
                }
                if(oldPersona.getOpeningDate()!= null){
                    company.setFoundationDate(
                            this.convert.convertToLocalDate(oldPersona.getOpeningDate()));
                }
                newPerson.setCompanyData(company);
            }

            if(personaDatabases != null ){
                List<Persona> personaNormalized = personaDatabases
                        .stream().filter(p -> !p.getProponentType().equals(ProponentType.SPOUSE)).toList();

                if(personaNormalized != null && !personaNormalized.isEmpty()){
                    newPerson.setId(personaNormalized.get(0).getId());
                    Persona personaSave = this.personaRepository.save(personaNormalized.get(0));
                }
            } else{
                newPerson.setId(oldPersona.getId());
                this.save(newPerson);
            }
            if(newPerson.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
                System.out.println(" ## ID ##: " + newPerson.getId() + " Persona Atualizado ** PF ** : "+ newPerson.getName());
            }else{
                System.out.println(" ## ID ##: " + newPerson.getId() + " Persona Atualizado ** PJ ** : " + newPerson.getCompanyData().getCorporateName());
            }
        }
        return  Boolean.TRUE;
    }


    @Transactional
    public  Boolean saveProponent(List<Persona> oldPersonas){
        for ( Persona persona: oldPersonas) {
            ProposalProponent proponent = this.create.createProponent(persona, persona.getProponentType());
            if(persona.getSourceIncome() != null){
                proponent.setComposeIncome(Boolean.TRUE);
                proponent.setMonthlyIncome(persona.getMonthlyIncome());
            }
            if(persona.getProponentType().equals(ProponentType.PRINCIPAL)){
                proponent.setScrConsulted(persona.getProposal().getLeadProposal().getScrConsulted());
            }
            if(persona.getParticipationPercentage() != 0){
                proponent.setPercentageOfCommitment(persona.getParticipationPercentage());
            }
            ProposalProponent proposalProponentSaved = this.proposalProponentRepository.save(proponent);
            proposalProponentSaved.setPersona(persona);
            proposalProponentSaved.setProposal(persona.getProposal());
            this.proposalProponentRepository.save(proposalProponentSaved);

            System.out.println(" ## ID ##: " + persona.getId() + " Proponent Salvo ** PF ** : "+ persona.getName());
        }
        return Boolean.TRUE;
    }


    public Boolean createCompanion (List<Persona> oldPersonas, Companion companion) {

        for (Persona oldPersona: oldPersonas) {
            PersonaCompanion personaCompanion = new PersonaCompanion();
            Persona newPerson = new Persona();
            newPerson.setName(companion.getName() != null ? companion.getName() : null);
            newPerson.setNationality(companion.getNationality() != null ? companion.getNationality() : null);
            newPerson.setOccupation(companion.getOccupation() != null ? companion.getOccupation() : null);
            newPerson.setRg(companion.getRg() != null ? companion.getRg() : null);
            newPerson.setOrgaoEmissor(companion.getOrgaoEmissor() != null ? companion.getOrgaoEmissor() : null);
            newPerson.setTaxId(companion.getCpf() != null ? companion.getCpf() : null);
            if(newPerson.getPersonaType() != null){
                newPerson.setPersonaType(PersonaType.NATURAL_PERSON);
            }
            if(companion.getEmail() != null){
                newPerson.getContacts().add(
                        this.create.createEmail(companion.getEmail(), null));
            }
            newPerson.setMotherName(companion.getMotherName() != null ? companion.getMotherName() : null);
            newPerson.setBirthDate(companion.getBirthDate() != null ? companion.getBirthDate() : null);
            newPerson.setPep(companion.getPep());

            personaCompanion.setData(newPerson);

            List<Persona> personaSave = this.personaRepository.findByTaxIdOld(companion.getCpf());

            if(personaSave == null && personaSave.isEmpty()){
                if(oldPersona.getPropertySystem().name().equals(TypeRegimeCompanion.PARTIAL_COMMUNION.name())){
                    personaCompanion.setType(TypeRegimeCompanion.PARTIAL_COMMUNION);

                }else if(oldPersona.getPropertySystem().name().equals(TypeRegimeCompanion.TOTAL_SEPARATION.name())){
                    personaCompanion.setType(TypeRegimeCompanion.TOTAL_SEPARATION);

                }else if(oldPersona.getPropertySystem().name().equals(TypeRegimeCompanion.UNIVERSIAL_COMMUNION.name())){
                    personaCompanion.setType(TypeRegimeCompanion.UNIVERSIAL_COMMUNION);

                }else if(oldPersona.getPropertySystem().name().equals(TypeRegimeCompanion.FINAL_PARTICIPATION_IN_AQUESTOS.name())){
                    personaCompanion.setType(TypeRegimeCompanion.FINAL_PARTICIPATION_IN_AQUESTOS);
                }
                oldPersona.setPersonaCompanionId(personaCompanion);
                System.out.println(" <<<<<< Companion criado >>>>> : " + companion.getName());
            }else if(personaSave != null && !personaSave.isEmpty()){
                List<Persona> personaNormalized = personaSave
                        .stream().filter(p -> !p.getProponentType().equals(ProponentType.SPOUSE)).toList();

                if(personaNormalized != null  && !personaNormalized.isEmpty()){
                    personaCompanion.setData(personaNormalized.get(0));
                }else if(personaNormalized == null && personaNormalized.isEmpty()){
                    personaCompanion.setData(personaSave.get(0));
                }
                oldPersona.setPersonaCompanionId(personaCompanion);
            }
            oldPersona.setPersonaCompanionId(personaCompanion);
            this.personaRepository.save(oldPersona);

        }

        return Boolean.TRUE;
    }


    @Transactional
    public void save (Persona persona) {
        this.personaRepository.save(persona);
        System.out.println(" ##### #### ###  ## # Persona Atualizado para novo padrão " + persona.getId() + persona.getName());
    }



}
