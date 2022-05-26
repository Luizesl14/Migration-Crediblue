package com.migration.presentation;

import com.migration.application.core.LeadService;
import com.migration.presentation.http.HttpMethod;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = "/lead")
public class LeadResource {

    @Autowired
    private LeadService leadService;

    @GetMapping
    public Object start(){
        return this.leadService.findAll();
    }
}
