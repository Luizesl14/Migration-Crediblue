package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.application.shared.ExistsEntity;
import com.migration.domain.Finder;
import com.migration.domain.Investor;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Company;
import com.migration.domain.persona.aggregation.ContactEmail;
import com.migration.domain.persona.aggregation.PersonaPhone;
import com.migration.domain.persona.aggregation.Phone;
import com.migration.infrastructure.IInvestorRepository;
import com.migration.infrastructure.IPersonaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Autowired
    private ConvertLocalDataTime convert;

    @Autowired
    private ExistsEntity existsEntity;


    public Boolean findAll() {
        List<Investor> investors = this.investorRepository.findAll();
        System.out.println("Quantidade de investors do banco: " + investors.size());
        this.createPersona(investors);
        return Boolean.TRUE ;
    }


    public Boolean createPersona (List<Investor> allInvestor){
        for (Investor investor: allInvestor) {
            Persona persona = new Persona();
            if(investor.getCnpj() != null){
                Persona personaDatabase  = this.personaRepository.findByTaxId(investor.getCnpj());
                persona.setPersonaType(PersonaType.LEGAL_PERSON);
                persona.setTaxId(investor.getCnpj());

                if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
                    persona.setName(investor.getName().toUpperCase());
                }else{
                    Company company = new Company();
                    company.setFantasyName(investor.getName().toUpperCase());
                    company.setCorporateName(investor.getName().toUpperCase());
                    persona.setCompanyData(company);
                }
                if(investor.getEmail() != null){
                    List<ContactEmail> contactEmailList = new ArrayList<>();
                    ContactEmail contactEmail = this.create.createEmail(
                            investor.getEmail(), this.convert.covertLocalDataTimeToDate(investor.getCreatedAt()));
                    contactEmailList.add(contactEmail);
                    persona.setContacts(contactEmailList);
                }
                if(investor.getTelephone() != null){
                    List<PersonaPhone> personaPhoneList = new ArrayList<>();
                    Phone phone = new Phone();
                    phone.setNumber(investor.getTelephone());
                    phone.setIsWhatsApp(Boolean.FALSE);
                    PersonaPhone personaPhone = this.create.createPhone(phone, this.convert.covertLocalDataTimeToDate(investor.getCreatedAt()));
                    personaPhoneList.add(personaPhone);
                    persona.setPhones(personaPhoneList);
                }
                if(personaDatabase != null){

                    if(this.existsEntity.verifyEmail(personaDatabase.getContacts(), persona.getContacts())
                            .equals(Boolean.FALSE)){

                        System.out.println("-----------EMAIL DIFERENTE ADICIONADO-----------");
                        personaDatabase.getContacts().addAll(persona.getContacts());
                    }

                    if( this.existsEntity.verifyPhone(personaDatabase.getPhones(),persona.getPhones())
                            .equals(Boolean.FALSE)){
                        System.out.println("-----------PHONE DIFERENTE ADICIONADO-----------");
                        personaDatabase.getPhones().addAll(persona.getPhones());
                    }
                    investor.setPersona(personaDatabase);
                    this.save(investor);
                }else{
                    investor.setPersona(persona);
                    this.save(investor);
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
