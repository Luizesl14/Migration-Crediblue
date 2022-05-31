package com.migration.presentation;

import com.migration.application.core.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/personas")
public class PersonaResource {

    @Autowired
    private PersonaService personaService;

    @GetMapping
    public void start(){
        this.personaService.findAll();
    }
}
