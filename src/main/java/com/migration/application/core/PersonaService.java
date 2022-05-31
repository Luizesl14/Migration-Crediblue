package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.domain.Proposal;
import com.migration.domain.ProposalProponent;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.enums.ProponentType;
import com.migration.domain.enums.TypeRegimeCompanion;
import com.migration.domain.persona.Companion;
import com.migration.domain.persona.OldPersona;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Company;
import com.migration.domain.persona.aggregation.CompanyRepresentatives;
import com.migration.domain.persona.aggregation.PersonaCompanion;
import com.migration.domain.persona.aggregation.Phone;
import com.migration.infrastructure.IOldPersonaRepository;
import com.migration.infrastructure.IPersonaRepository;
import com.migration.infrastructure.IProposalProponentRepository;
import com.migration.infrastructure.IProposalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        List<OldPersona> oldPersonas = this.oldPersonaRepository.findAll();
        System.out.println("Quantidade de leads OldPersonas do banco: " + oldPersonas.size());
        this.createPersona(oldPersonas);
    }


    @Transactional
    public Boolean createPersona (List<OldPersona> oldPersonas) {

        for (OldPersona oldPersona: oldPersonas) {
            Persona personaDatabase = null;
            List<OldPersona> oldPersonaDatabase = null;
            if(oldPersona.getCpfCnpj()!= null){
                personaDatabase  = this.personaRepository.findByTaxId(oldPersona.getCpfCnpj());
                if(personaDatabase == null)
                    oldPersonaDatabase  = this.oldPersonaRepository.findByCpfCnpj(oldPersona.getCpfCnpj());
                if(oldPersonaDatabase != null){
                    oldPersonaDatabase.forEach(o-> {
                        System.out.println("  ## ID ##: "+ o.getId()  + " ###################### Persona já existente: " + o.getName());
                    });
                }
            }

            Persona persona = new Persona();
            persona.setTaxId(oldPersona.getCpfCnpj());
            persona.setId(oldPersona.getId());

            persona.setPersonaType(
                    oldPersona.getCpfCnpj()
                            .length() == 11 ? PersonaType.NATURAL_PERSON: PersonaType.LEGAL_PERSON);

            if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
                persona.setName(oldPersona.getName());
            }

            persona.setMaritalStatus(oldPersona.getMaritalStatus());
            persona.setBirthDate(oldPersona.getBirthDate());
            persona.setMaritalStatus(oldPersona.getMaritalStatus());
            persona.setRg(oldPersona.getRg());
            persona.setOrgaoEmissor(oldPersona.getOrgaoEmissor());
            persona.setNationality(oldPersona.getNationality());
            persona.setMotherName(oldPersona.getMotherName());
            persona.setCitizenship(oldPersona.getCitizenship());
            persona.setOccupation(oldPersona.getOccupation());
            persona.setSourceIncome(oldPersona.getSourceIncome());


            if(oldPersona.getFinancialInstitutionCode() != null){
                persona.getBankAccounts().add(
                        this.create.createAccount(
                                oldPersona.getFinancialInstitutionCode(),oldPersona.getAccountBranch(),
                                oldPersona.getAccountNumber(), oldPersona.getAccountDigit(), null));
            }
            if(oldPersona.getAddress() != null){
                persona.getAddresses().add(
                        this.create.createAddress(oldPersona.getAddress(), oldPersona.getAddress().getCreatedAt()));
            }
            if(oldPersona.getEmail() != null){
                persona.getContacts().add(
                        this.create.createEmail(oldPersona.getEmail(), null));
            }
            if(oldPersona.getTelephone() != null){
                Phone phone = new Phone();
                phone.setNumber(oldPersona.getTelephone());
                phone.setIsWhatsApp(Boolean.FALSE);
                persona.getPhones().add(this.create.createPhone(phone, null));
            }

            if(persona.getPersonaType().equals(PersonaType.LEGAL_PERSON)){
                Company company = new Company();
                company.setCorporateName(oldPersona.getName());
                if(oldPersona.getOpeningDate() != null){
                    company.setFoundationDate(this.convert.convertToLocalDate(oldPersona.getOpeningDate()));
                }
                if(oldPersona.getOpeningDate()!= null){
                    company.setFoundationDate(
                            this.convert.convertToLocalDate(oldPersona.getOpeningDate()));
                }
                persona.setCompanyData(company);
                if(oldPersona.isLegalRepresentative()){
                    CompanyRepresentatives  companyRepresentatives = new CompanyRepresentatives();
                    companyRepresentatives.setRepresentative(persona);
                    if(oldPersona.getParticipationPercentage() != 0){
                        companyRepresentatives.setPercentParticipation(oldPersona.getParticipationPercentage());
                        company.getRepresentatives().add(companyRepresentatives);
                    }
                }
            }

            if(oldPersona.getCompanion() != null){
              Persona companion = this.createCompanion(oldPersona.getCompanion());
                PersonaCompanion personaCompanion = new PersonaCompanion();
                personaCompanion.setData(companion);

                if(oldPersona.getPropertySystem().name().equals(TypeRegimeCompanion.PARTIAL_COMMUNION.name())){
                    personaCompanion.setType(TypeRegimeCompanion.PARTIAL_COMMUNION);

                }else if(oldPersona.getPropertySystem().name().equals(TypeRegimeCompanion.TOTAL_SEPARATION.name())){
                    personaCompanion.setType(TypeRegimeCompanion.TOTAL_SEPARATION);

                }else if(oldPersona.getPropertySystem().name().equals(TypeRegimeCompanion.UNIVERSIAL_COMMUNION.name())){
                    personaCompanion.setType(TypeRegimeCompanion.UNIVERSIAL_COMMUNION);

                }else if(oldPersona.getPropertySystem().name().equals(TypeRegimeCompanion.FINAL_PARTICIPATION_IN_AQUESTOS.name())){
                    personaCompanion.setType(TypeRegimeCompanion.FINAL_PARTICIPATION_IN_AQUESTOS);
                }

                persona.setCompanion(personaCompanion);
                System.out.println(" <<<<<< Companion criado >>>>> : " + companion.getName());
            }

            if(personaDatabase != null){
                Persona personaSave = this.personaRepository.save(personaDatabase);
                this.saveProponent(
                        oldPersona, personaSave, oldPersona.getCreatedAt(), oldPersona.getProponentType(), oldPersona.getProposal());

            }else if(oldPersonaDatabase != null){
                 persona.setId(oldPersonaDatabase.get(0).getId());
                Persona personaSave = this.personaRepository.save(persona);
                 this.saveProponent(
                oldPersona, personaSave, oldPersona.getCreatedAt(), oldPersona.getProponentType(), oldPersona.getProposal());

            }else{
                this.save(persona);
                this.saveProponent(
                      oldPersona,  persona, oldPersona.getCreatedAt(), oldPersona.getProponentType(), oldPersona.getProposal());
            }

            if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
                System.out.println(" ## ID ##: " + persona.getId() + " Persona Atualizado ** PF ** : "+ persona.getName());
            }else{
                System.out.println(" ## ID ##: " + persona.getId() + " Persona Atualizado ** PJ ** : " + persona.getCompanyData().getCorporateName());
            }

        }
        return  Boolean.TRUE;
    }


    public Persona createCompanion (Companion companion) {

        Persona persona = new Persona();
        persona.setName(companion.getName() != null ? companion.getName() : null);
        persona.setNationality(companion.getNationality() != null ? companion.getNationality() : null);
        persona.setOccupation(companion.getOccupation() != null ? companion.getOccupation() : null);
        persona.setRg(companion.getRg() != null ? companion.getRg() : null);
        persona.setOrgaoEmissor(companion.getOrgaoEmissor() != null ? companion.getOrgaoEmissor() : null);
        persona.setTaxId(companion.getCpf() != null ? companion.getCpf() : null);
        if(persona.getPersonaType() != null){
            persona.setPersonaType(PersonaType.NATURAL_PERSON);
        }

        if(companion.getEmail() != null){
            persona.getContacts().add(
                    this.create.createEmail(companion.getEmail(), null));
        }
        persona.setMotherName(companion.getMotherName() != null ? companion.getMotherName() : null);
        persona.setBirthDate(companion.getBirthDate() != null ? companion.getBirthDate() : null);
        persona.setPep(companion.getPep());
        return  persona;
    }


    @Transactional
    public void save (Persona persona) {
        this.personaRepository.save(persona);
        System.out.println(" ##### #### ###  ## # Persona Atualizado para novo padrão " + persona.getId() + persona.getName());
    }

    @Transactional
    public  Boolean saveProponent(OldPersona oldPersona, Persona persona, LocalDateTime createdAt, ProponentType proponentType, Proposal proposal){
        ProposalProponent proponent = this.create.createProponent(oldPersona, persona,createdAt, proponentType);
        if(persona.getSourceIncome() != null){
            proponent.setComposeIncome(Boolean.TRUE);
            proponent.setMonthlyIncome(oldPersona.getMonthlyIncome());
        }
        if(proponentType.equals(ProponentType.PRINCIPAL)){
           proponent.setScrConsulted(proposal.getLeadProposal().getScrConsulted());
        }
        if(oldPersona.getParticipationPercentage() != 0){
            proponent.setPercentageOfCommitment(oldPersona.getParticipationPercentage());
        }
        ProposalProponent proposalProponentSaved = this.proposalProponentRepository.save(proponent);
        proposalProponentSaved.setPersona(persona);
        proposalProponentSaved.setProposal(proposal);
        this.proposalProponentRepository.save(proposalProponentSaved);

        System.out.println(" ## ID ##: " + persona.getId() + " Proponent Salvo ** PF ** : "+ persona.getName());
        return Boolean.TRUE;
    }


}
