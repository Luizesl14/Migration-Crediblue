package com.migration.application.core;

import com.migration.domain.Proposal;
import com.migration.domain.ProposalProponent;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.PersonaDocument;
import com.migration.infrastructure.IPersonaDocumentRepository;
import com.migration.infrastructure.IPersonaRepository;
import com.migration.infrastructure.IProposalProponentRepository;
import com.migration.infrastructure.IProposalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class PersonaDocumentService {


    @Autowired
    private IPersonaDocumentRepository personaDocumentRepository;

    @Autowired
    private IProposalProponentRepository proposalProponentRepository;

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private IProposalRepository proposalRepository;



    public Boolean findAll() {
        List<PersonaDocument> documents = this.personaDocumentRepository.findAll();
        System.out.println("Quantidade de Lead do banco: " + documents.size());
//        this.normalization(documents);
        this.createPersona(documents);
        return Boolean.TRUE ;
    }

    public void normalization(List<PersonaDocument> documents){
        for (PersonaDocument doc: documents) {
            if(doc.getPersona().getTaxId() != null){
                Persona persona = this.personaRepository.findByTaxId(doc.getPersona().getTaxId());
                if(persona != null){
                    doc.setPersona(persona);
                    this.personaDocumentRepository.save(doc);
                    System.out.println("<<<<<< Persona encontrado documento Atualizado! " + doc.getPersona().getName());
                }
            }

        }
    }

    public Boolean createPersona (List<PersonaDocument> documents){
        int index = 0;
        for (PersonaDocument personaDocument: documents) {
            if(personaDocument.getPersona() != null && personaDocument.getPersona().getProposal() != null){

                ProposalProponent proposalProponent  =
                        this.proposalProponentRepository.findAllDByProposalByPersona(
                                personaDocument.getPersona().getTaxId(),
                                personaDocument.getPersona().getProposal().getId());

                if(proposalProponent != null){
                    personaDocument.setProponent(proposalProponent);
                    this.personaDocumentRepository.save(personaDocument);
                    System.out.println("## ID ## " + personaDocument.getPersona().getId()
                            + " ### DOCUMENT UPDATED " + personaDocument.getProponent().getPersona().getName());
                    System.out.println();

                }else{
                    Persona persona = this.personaRepository.findByTaxId(personaDocument.getPersona().getTaxId());
                    if(persona != null){
                        personaDocument.setPersona(persona);
                        this.personaDocumentRepository.save(personaDocument);
                        System.out.println("### Persona Atualizada"
                                + personaDocument.getPersona().getId() + " para -->> " + persona.getId());
                    }
                    System.out.println("### Documento não referenciado " + index++);
                    System.out.println("## ID ## " + personaDocument.getPersona().getId()
                            + " ### Documento não referenciado nome " + personaDocument.getPersona().getName());
                    System.out.println();
                }
            }
        }
        return Boolean.TRUE;
    }
}
