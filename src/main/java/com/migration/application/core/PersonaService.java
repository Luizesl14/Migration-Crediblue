package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.domain.Proposal;
import com.migration.domain.ProposalProponent;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.enums.ProponentType;
import com.migration.domain.persona.OldPersona;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Company;
import com.migration.domain.persona.aggregation.Phone;
import com.migration.infrastructure.IOldPersonaRepository;
import com.migration.infrastructure.IPersonaRepository;
import com.migration.infrastructure.IProposalProponentRepository;
import com.migration.infrastructure.IProposalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            }

            Persona persona = new Persona();
            persona.setTaxId(oldPersona.getCpfCnpj());
            persona.setId(oldPersona.getId());

            persona.setPersonaType(
                    oldPersona.getCpfCnpj()
                            .length() == 11 ? PersonaType.NATURAL_PERSON: PersonaType.LEGAL_PERSON);

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

            if(persona.getPersonaType().equals(PersonaType.LEGAL_PERSON)){
                Company company = new Company();
                company.setCorporateName(oldPersona.getName());
                if(oldPersona.getOpeningDate()!= null){
                    company.setFoundationDate(
                            this.convert.convertToLocalDate(oldPersona.getOpeningDate()));
                }
                persona.setCompanyData(company);
            }else{
                persona.setName(oldPersona.getName());
            }

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

            if(personaDatabase != null){
                //Persona personaSave = this.personaRepository.save(personaDatabase);
               // this.saveProponent(
                        //personaSave, oldPersona.getCreatedAt(), oldPersona.getProponentType(), oldPersona.getProposal());

            }else if(oldPersonaDatabase != null){
                 //persona.setId(oldPersonaDatabase.getId());
                //Persona personaSave = this.personaRepository.save(persona);
                // this.saveProponent(
                //personaSave, oldPersona.getCreatedAt(), oldPersona.getProponentType(), oldPersona.getProposal());

            }else{
               // this.save(persona);
                //this.saveProponent(
                        //persona, oldPersona.getCreatedAt(), oldPersona.getProponentType(), oldPersona.getProposal());
            }

            if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
                System.out.println(" ## ID ##: " + persona.getId() + " Persona Atualizado ** PF ** : "+ persona.getName());
            }else{
                System.out.println(" ## ID ##: " + persona.getId() + " Persona Atualizado ** PJ ** : " + persona.getCompanyData().getCorporateName());
            }

        }
        return  Boolean.TRUE;
    }

    @Transactional
    public void save (Persona persona) {
        this.personaRepository.save(persona);
        System.out.println("Persona Atualizado para novo padrão " + persona);
    }

    @Transactional
    public  Boolean saveProponent(Persona persona, LocalDateTime createdAt, ProponentType proponentType, Proposal proposal){
        ProposalProponent proponent = this.create.createProponent(persona,createdAt, proponentType);
        ProposalProponent proposalProponentSaved = this.proposalProponentRepository.save(proponent);
        proposalProponentSaved.setProposal(proposal);
        this.proposalProponentRepository.save(proposalProponentSaved);

        return Boolean.TRUE;
    }


}
