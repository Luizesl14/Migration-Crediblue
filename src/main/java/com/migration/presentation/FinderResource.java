package com.migration.presentation;

import com.migration.application.core.FinderService;
import com.migration.presentation.http.HttpMethod;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = "/finders")
public class FinderResource {

    @Autowired
    private FinderService finderService;

    @GetMapping
    public void start(){
        this.finderService.findAll();
    }
}
