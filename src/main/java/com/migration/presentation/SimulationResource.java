package com.migration.presentation;

import com.migration.application.core.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/simulations")
public class SimulationResource {

    @Autowired
    private SimulationService simulationService;

    @GetMapping
    public void start(){this.simulationService.findAll();}
}
