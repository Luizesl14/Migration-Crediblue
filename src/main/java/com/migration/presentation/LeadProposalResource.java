package com.migration.presentation;

import com.migration.application.core.LeadProposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/lead-proposal")
public class LeadProposalResource {

    @Autowired
    private LeadProposalService leadProposalService;

    @GetMapping
    public void start(){this.leadProposalService.findAll();}
}
