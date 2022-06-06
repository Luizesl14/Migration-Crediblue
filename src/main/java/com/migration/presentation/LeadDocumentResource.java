package com.migration.presentation;

import com.migration.application.core.LeadProposalDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/lead-proposal-document")
public class LeadDocumentResource {

    @Autowired
    private LeadProposalDocumentService leadProposalDocumentService;

    @GetMapping
    public void start(){
        this.leadProposalDocumentService.findAll();
    }
}
