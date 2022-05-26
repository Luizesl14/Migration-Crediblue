package com.migration.presentation;

import com.migration.application.core.InvestorService;
import com.migration.presentation.http.HttpMethod;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = "/investors")
public class InvestorResource {

    @Autowired
    private InvestorService investorService;

   @GetMapping
    public void start() {
        this.investorService.findAll();
    }

}
