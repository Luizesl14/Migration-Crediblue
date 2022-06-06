package com.migration.presentation;

import com.migration.application.core.CompanionService;
import com.migration.application.core.PersonaDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/persona-document")
public class PersonaDocumentResource {

    @Autowired
    private PersonaDocumentService personaDocumentService;

    @GetMapping
    public void start(){
        this.personaDocumentService.findAll();
    }
}
