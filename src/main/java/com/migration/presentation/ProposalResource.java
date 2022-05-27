package com.migration.presentation;

import com.migration.application.core.ProposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/proposal")
public class ProposalResource {

    @Autowired
    private ProposalService proposalService;

    @GetMapping
    public void start(){this.proposalService.findAll();}
}
