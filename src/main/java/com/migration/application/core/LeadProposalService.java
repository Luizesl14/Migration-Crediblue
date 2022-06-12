package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.domain.Proposal;
import com.migration.domain.ProposalProponent;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.enums.ProponentType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Company;
import com.migration.domain.persona.aggregation.PersonaCompanion;
import com.migration.domain.persona.aggregation.Phone;
import com.migration.infrastructure.IPersonaRepository;
import com.migration.infrastructure.IProposalProponentRepository;
import com.migration.infrastructure.IProposalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class LeadProposalService {

    @Autowired
    private IProposalRepository proposalRepository;

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private ConvertLocalDataTime convert;

    @Autowired
    private CreateObject create;

    @Autowired
    private IProposalProponentRepository proposalProponentRepository;


    public void findAll() {
        List<Proposal> proposals = this.proposalRepository.findAllByProposal();
        System.out.println("Quantidade de leads proposal do banco: " + proposals.size());
        this.createPersona(proposals);
        this.saveProponent();
    }

    public Boolean createPersona (List<Proposal> proposals) {

        proposals.parallelStream().forEach(proposal-> {
            Persona persona = this.create.createPersonaByLeadProposal(proposal.getLeadProposal());
            this.personaRepository.save(persona);
            System.out.println("### INDEX PROPONENTE PRINCIPAL CRIADO");
        });
        return  Boolean.TRUE;
    }

    public  Boolean saveProponent(){
        List<Persona> personas = this.personaRepository.personaLeadProposal();
        int index = 0;
        for (Persona personaDatabase: personas) {
            ProposalProponent proponent = this.create.createProponentPrincipal(personaDatabase.getCreatedAt(), ProponentType.PRINCIPAL);
            proponent.setComposeIncome(Boolean.FALSE);
            proponent.setPercentageOfCommitment(0.0);

            if(personaDatabase.getLeadProposal() != null)
                proponent.setMonthlyIncome(personaDatabase.getLeadProposal().getFamilyIncome());

            if(personaDatabase.getProponentType() != null){
                if(personaDatabase.getProponentType().equals(ProponentType.PRINCIPAL))
                    proponent.setScrConsulted(personaDatabase.getProposal().getLeadProposal().getScrConsulted());
            }
            ProposalProponent proposalProponentSaved = this.proposalProponentRepository.save(proponent);
            proposalProponentSaved.setPersona(personaDatabase);
            proposalProponentSaved.setProposal(personaDatabase.getLeadProposal().getProposal());
            this.proposalProponentRepository.save(proposalProponentSaved);

            System.out.println("QUANTIDADE DE PROPONENTE DE PRINCIPAL: " +  index++);
        }
        return Boolean.TRUE;
    }

}
