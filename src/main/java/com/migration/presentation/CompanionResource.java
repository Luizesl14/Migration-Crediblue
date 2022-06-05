package com.migration.presentation;

import com.migration.application.core.CompanionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/companion")
public class CompanionResource {

    @Autowired
    private CompanionService companionService;

    @GetMapping
    public void start(){
        this.companionService.createCompanion();
    }
}
