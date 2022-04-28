package com.migration.application.core;

import com.migration.domain.Finder;
import com.migration.domain.persona.Persona;
import com.migration.infrastructure.IFinderRespository;
import com.migration.infrastructure.IPersonaRepository;
import org.hibernate.PropertyNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FinderService {

    @Autowired
    private IFinderRespository finderRespository;

    @Autowired
    private IPersonaRepository personaRepository;

    public List<Finder> normalizationOne() {
        return this.finderRespository
                .findAll()
                .stream()
                .distinct()
                .toList();
    }


    public Finder normalizationTwo() {
        return this.finderRespository
                .findAll()
                .stream()
                .distinct()
                .findFirst()
                .orElseThrow(()-> new PropertyNotFoundException("Not Found"));
    }


    @Transactional
    public void migrationEntity(){
        Finder finderNotRead = this.normalizationTwo();
        Persona finderPersona = new Persona();

        this.save(finderNotRead);
    }


    @Transactional
    public Finder save(Finder finderRead){
      return  this.finderRespository.save(finderRead);
    }
}
