package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.domain.LeadProposal;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.PersonaCompanion;
import com.migration.domain.persona.aggregation.PersonaComposeIncome;
import com.migration.domain.persona.aggregation.Phone;
import com.migration.infrastructure.ILeadProposalRespository;
import com.migration.infrastructure.IPersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LeadProposalService {

    @Autowired
    private ILeadProposalRespository leadProposalRespository;

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private ConvertLocalDataTime convert;

    @Autowired
    private CreateObject create;

    public void findAll() {
        List<LeadProposal> leadsProposal = this.leadProposalRespository.findAll();
        System.out.println("Quantidade de leads proposal do banco: " + leadsProposal.size());
        this.normalizationStepOne(leadsProposal);
    }

    public void normalizationStepOne (List<LeadProposal> leadsProposalDatabase){

        List<LeadProposal> normalizationStepOne = leadsProposalDatabase
                .stream()
                .filter(leadProposal -> leadProposal.getCpfCnpj().equals(leadProposal.getCpfCnpj())
                ).toList();

        System.out.println("Finders leadsProposal Step One: " + normalizationStepOne.size());
        normalizationStepOne.forEach(System.out::println);

        this.updatePersona(normalizationStepOne);
    }

    @Transactional
    public void updatePersona(List<LeadProposal> leadProposalNormalized){
        leadProposalNormalized.forEach(leadProposal -> {
            Persona personaDatabase = this.personaRepository.findByTaxId(leadProposal.getCpfCnpj());
            Persona persona = this.createPersona(leadProposal);

            if(personaDatabase != null){
                persona.setId(personaDatabase.getId());
                this.personaRepository.save(persona);
            }else {
                System.out.println("lead normalizados : " + leadProposalNormalized.size());
                leadProposalNormalized.forEach(System.out::println);

                this.save(persona);
            }
        });
    }


    @Transactional
    public Persona createPersona (LeadProposal leadProposal) {

        Persona persona = new Persona();
        persona.setName(leadProposal.getName());
        persona.setTaxId(leadProposal.getCpfCnpj());
        persona.setPersonaType(
                leadProposal.getCpfCnpj()
                        .length() == 11 ? PersonaType.NATURAL_PERSON: PersonaType.LEGAL_PERSON);
        persona.setMaritalStatus(leadProposal.getMaritalStatus());
        persona.setBirthDate(this.convert.convertToLocalDate(leadProposal.getBirthDate()));
        persona.setMaritalStatus(leadProposal.getMaritalStatus());
        persona.setRg(leadProposal.getRg());
        persona.setOrgaoEmissor(leadProposal.getOrgaoEmissor());
        persona.setNationality(leadProposal.getNationality());
        persona.setMotherName(leadProposal.getMother());
        persona.setCitizenship(leadProposal.getCitizenship());
        persona.setPep(leadProposal.getPep());
        persona.setOccupation(leadProposal.getOccupation());

        if(leadProposal.getCompanyType() != null){
            persona.getCompanyData().setType(leadProposal.getCompanyType());
            persona.getCompanyData().setCnae(leadProposal.getCnaeCode());
            persona.getCompanyData().setFoundationDate(
                    this.convert.convertToLocalDate(leadProposal.getCompanyFoundingDate()));
        }

        if(leadProposal.getSpouseName() != null){
            PersonaCompanion personaCompanion = new PersonaCompanion();
            personaCompanion.getPersona().setName(leadProposal.getSpouseName());
            personaCompanion.setRegime(leadProposal.getTypeRegimeCompanion());
        }

        if(leadProposal.getMonthlyRevenue() != null
        && leadProposal.getMonthlyExpenses() != null
        && leadProposal.getTotalRevenue() != null){
            PersonaComposeIncome composeIncome = new PersonaComposeIncome();
            composeIncome.getComposeIncome().setMonthlyRevenue(leadProposal.getMonthlyRevenue());
            composeIncome.getComposeIncome().setMonthlyRevenue(leadProposal.getMonthlyExpenses());
            composeIncome.getComposeIncome().setTotalRevenue(leadProposal.getTotalRevenue());
            persona.getComposeIncomes().add(composeIncome);
        }

        if(leadProposal.getAccountInfo() != null){
            persona.getBankAccounts().add(
                    this.create.createAccount(leadProposal.getAccountInfo(), null));
        }
        if(leadProposal.getAddress() != null){
            persona.getAddresses().add(
                    this.create.createAddress(leadProposal.getAddress(), leadProposal.getAddress().getCreatedAt()));
        }
        if(leadProposal.getEmail() != null){
            persona.getContacts().add(
                    this.create.createEmail(leadProposal.getEmail(), null));
        }
        if(leadProposal.getTelephone() != null){
            Phone phone = new Phone();
            phone.setNumber(leadProposal.getTelephone());
            phone.setIsWhatsApp(Boolean.FALSE);
            persona.getPhones().add(this.create.createPhone(phone, null));
        }
        return  persona;
    }

    @Transactional
    public void save (Persona persona) {
        this.personaRepository.save(persona);
        System.out.println("lead proposal salvo como persona " + persona);
    }
}
