package com.migration.application.core;

import com.migration.application.shared.CreateObject;
import com.migration.domain.Investor;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Company;
import com.migration.domain.persona.aggregation.Phone;
import com.migration.infrastructure.IInvestorRepository;
import com.migration.infrastructure.IPersonaRepository;
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
                persona.setPersonaType(PersonaType.LEGAL_PERSON);
                persona.setTaxId(investor.getCnpj());
                Company company = new Company();
                company.setCorporateName(investor.getName());
                persona.setCompanyData(company);
                persona.setName(investor.getName());

                if(investor.getEmail() != null){
                    persona.getContacts().add(this.create.createEmail(investor.getEmail(), null));
                }
                if(investor.getTelephone() != null){
                    Phone phone = new Phone();
                    phone.setNumber(investor.getTelephone());
                    phone.setIsWhatsApp(Boolean.FALSE);
                    persona.getPhones().add(this.create.createPhone(phone, null));
                }
                 investor.setPersona(persona);
                if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON))
                System.out.println(" New Person ** PJ ** : " + persona.getName());
            }
        }
        System.out.println("Total de Personas criadas:  " + allInvestor.size());
        //this.save(allInvestor);
        return Boolean.TRUE;
    }


    public void save (List<Investor> investorNormalized) {
        for (Investor investor: investorNormalized){
            Persona persona = this.personaRepository.save(investor.getPersona());
            investor.setPersona(persona);
            this.investorRepository.save(investor);
            System.out.println("Persona save: " + persona.getName() + " ** Invesor **");
        }
    }

}
