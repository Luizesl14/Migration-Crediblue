package com.migration.presentation;

import com.migration.application.core.PersonaNormalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/personas-normalizaton")
public class PersonaNormalizationResource {

    @Autowired
    private PersonaNormalizationService personaNormalizationService;

    @GetMapping
    public void normalization(){
        this.personaNormalizationService.findAll();
    }
}
