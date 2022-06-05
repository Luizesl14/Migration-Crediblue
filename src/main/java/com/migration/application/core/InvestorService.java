package com.migration.application.core;

import com.migration.application.shared.CreateObject;
import com.migration.domain.Finder;
import com.migration.domain.Investor;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Company;
import com.migration.domain.persona.aggregation.Phone;
import com.migration.infrastructure.IInvestorRepository;
import com.migration.infrastructure.IPersonaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class InvestorService {

    @Autowired
    private IInvestorRepository investorRepository;

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private CreateObject create;

    public Boolean findAll() {
        List<Investor> investors = this.investorRepository.findAll();
        System.out.println("Quantidade de investors do banco: " + investors.size());
        this.createPersona(investors);
        return Boolean.TRUE ;
    }


    public Boolean createPersona (List<Investor> allInvestor){
        for (Investor investor: allInvestor) {
            Persona persona = new Persona();
            if(investor != null){
                Persona personaDatabase = null;
                if(investor.getCnpj() != null){
                    personaDatabase  = this.personaRepository.findByTaxId(investor.getCnpj());
                    if(personaDatabase != null){
                            System.out.println("Persona já existe no banco ** PJ ** : " + personaDatabase.getCompanyData().getCorporateName());
                    }
                }
                persona.setPersonaType(PersonaType.LEGAL_PERSON);
                persona.setTaxId(investor.getCnpj());

                if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
                    persona.setName(investor.getName());
                }else{
                    Company company = new Company();
                    company.setCorporateName(investor.getName());
                    persona.setCompanyData(company);
                }

                if(investor.getEmail() != null){
                    persona.getContacts().add(this.create.createEmail(investor.getEmail(), null));
                }
                if(investor.getTelephone() != null){
                    Phone phone = new Phone();
                    phone.setNumber(investor.getTelephone());
                    phone.setIsWhatsApp(Boolean.FALSE);
                    persona.getPhones().add(this.create.createPhone(phone, null));
                }
                if(personaDatabase != null){
                        persona.setId(persona.getId());
                        BeanUtils.copyProperties(persona ,personaDatabase, "id", "taxId", "cpfCnpj", "createdAt");
                        Persona personaSave = this.personaRepository.save(personaDatabase);
                        investor.setPersona(personaSave);
                        this.save(investor);
                    System.out.println();
                }else{
                    investor.setPersona(persona);
                    this.save(investor);
                    System.out.println();
                }
            }

        }
        return Boolean.TRUE;
    }


    public void save (Investor investor) {
        Persona persona = this.investorRepository.save(investor).getPersona();
        System.out.println("Persona save: " + persona.getCompanyData().getCorporateName() + " ** Investor **");

    }

}
