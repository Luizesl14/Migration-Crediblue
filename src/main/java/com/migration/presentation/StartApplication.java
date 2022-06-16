package com.migration.presentation;

import com.migration.application.core.Start;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/start")
public class StartApplication {

    @Autowired
    private Start start;

    @GetMapping
    public void start(){
        this.start.satartApplication();
    }
}
