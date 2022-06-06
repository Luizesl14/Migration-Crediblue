package com.migration.application.core;

import com.migration.domain.Proposal;
import com.migration.domain.ProposalProponent;
import com.migration.domain.persona.aggregation.LeadProposalDocument;
import com.migration.domain.persona.aggregation.PersonaDocument;
import com.migration.infrastructure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class LeadProposalDocumentService {


    @Autowired
    private IPersonaDocumentRepository personaDocumentRepository;

    @Autowired
    private  IProposalProponentRepository proposalProponentRepository;

    @Autowired
    private ILeadProposalDocumentRepository leadProposalDocumentRepository;

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private IProposalRepository proposalRepository;



    public Boolean findAll() {
        List<LeadProposalDocument> documents = this.leadProposalDocumentRepository.findAll();
        System.out.println("Quantidade de documentos: " + documents.size());

        this.normalizedDocument(documents);
        return Boolean.TRUE ;
    }

    public Boolean normalizedDocument (List<LeadProposalDocument> documentList){
        int index = 0;
        for (LeadProposalDocument document: documentList) {
            if(document.getLeadProposal()  != null && document.getLeadProposal().getProposal() != null){

                ProposalProponent proposalProponent  =
                        this.proposalProponentRepository.findAllDByProposalByPersona(
                                document.getLeadProposal().getCpfCnpj(),
                                document.getLeadProposal().getProposal().getId());

                if(proposalProponent != null){
//                    document.setProponent(proposalProponent);
//                    this.leadProposalDocumentRepository.save(document);
                    System.out.println("## ID ## " + document.getLeadProposal().getId()
                            + " ### DOCUMENT UPDATED " + document.getProponent().getPersona().getName());
                    System.out.println();

                }else{
                    System.out.println("### Documento não referenciado " + index++);
                }
            }
        }
        return Boolean.TRUE;
    }
}
