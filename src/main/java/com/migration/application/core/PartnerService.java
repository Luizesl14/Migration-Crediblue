package com.migration.application.core;

import com.migration.application.shared.CreateObject;
import com.migration.domain.Partner;
import com.migration.domain.enums.PersonaType;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Company;
import com.migration.domain.persona.aggregation.Phone;
import com.migration.infrastructure.IPartnerRepository;
import com.migration.infrastructure.IPersonaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class PartnerService {



    @Autowired
    private IPartnerRepository partnerRepository;

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private CreateObject create;

    public Boolean findAll() {
        List<Partner> partners = this.partnerRepository.findAll();
        System.out.println("Quantidade de partner do banco: " + partners.size());
        partnerResolver();
        this.createPersona(partners);
        return Boolean.TRUE ;
    }


    public void partnerResolver(){
        List<Partner> partnerNull = this.partnerRepository.findByPartnerTaxIdIsNull();
        System.out.println("### Persona database " + partnerNull.size());
        int index = 0;
        for (Partner partner: partnerNull) {
            Persona persona = this.personaRepository.findPersonaUser(
                    partner.getName(), partner.getEmail(), partner.getTelephone(), null);
            if(persona != null){
                System.out.println("### Persona encontrada " + persona.getName() + "index " + index++);
                partner.setPersona(persona);
              this.partnerRepository.save(partner);
            }else{
                String token = UUID.randomUUID().toString().toUpperCase(Locale.ROOT);
                partner.setCpfCnpj(token.substring(0,10));
                Partner partnerDatabase = this.partnerRepository.save(partner);
                System.out.println("### Persona não encontrada set token " + partnerDatabase.getCpfCnpj());
            }
        }

    }



    public Boolean createPersona (List<Partner> partnerNormalized){

        for (Partner partner: partnerNormalized) {
            Persona persona = new Persona();

          Persona personaDatabase = null;
            if(partner.getCpfCnpj()!= null){
                personaDatabase  = this.personaRepository.findByTaxId(partner.getCpfCnpj());
                if(personaDatabase != null)
                    if (personaDatabase.getPersonaType().equals(PersonaType.NATURAL_PERSON)) {
                        System.out.println("Persona Existe no banco ** PF ** : " + personaDatabase.getName());
                    } else {
                        System.out.println("Persona Existe no banco  ** PJ ** : " + personaDatabase.getCompanyData().getCorporateName());
                    }
            }
               if(partner.getCpfCnpj() != null){
                   persona.setPersonaType(
                           partner.getCpfCnpj()
                                   .length() == 11 ? PersonaType.NATURAL_PERSON: PersonaType.LEGAL_PERSON);
                   persona.setTaxId(partner.getCpfCnpj());
               }else{
                   persona.setPersonaType(PersonaType.NATURAL_PERSON);
               }

                if(persona.getPersonaType().equals(PersonaType.NATURAL_PERSON)){
                    persona.setName(partner.getName());
                }else{
                    Company company = new Company();
                    company.setCorporateName(partner.getName());
                    persona.setCompanyData(company);
                }

                if(partner.getAddress() != null ){
                    persona.getAddresses().add(this.create.createAddress(partner.getAddress(),
                            partner.getAddress().getCreatedAt()));
                }
                if(partner.getEmail() != null){
                    persona.getContacts().add(this.create.createEmail(partner.getEmail(), null));
                }

                if(partner.getTelephone() != null){
                    Phone phone = new Phone();
                    phone.setNumber(partner.getTelephone());
                    phone.setIsWhatsApp(Boolean.FALSE);
                    persona.getPhones().add(this.create.createPhone(phone, null));
                }
                if(personaDatabase != null){
                        persona.setId(personaDatabase.getId());

                      BeanUtils.copyProperties(persona ,personaDatabase,
                              "id", "name", "cpfCnpj", "createdAt");

                        Persona personaSave = this.personaRepository.save(persona);
                        partner.setPersona(personaSave);
                        this.save(partner);
                }else{
                   Persona personaSave = this.personaRepository.save(persona);
                   partner.setPersona(personaSave);
                   this.save(partner);
                }
            }
        return Boolean.TRUE;
    }

    public void save (Partner partner) {
            this.partnerRepository.save(partner);
        if(partner.getPersona().getPersonaType().equals(PersonaType.NATURAL_PERSON)){
            System.out.println("New Person ** PF ** : " + partner.getPersona().getName());
        }else{
            System.out.println("New Person ** PJ ** : " + partner.getPersona().getCompanyData().getCorporateName());
        }
        }
}
