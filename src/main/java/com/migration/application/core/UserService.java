package com.migration.application.core;

import com.migration.application.shared.ConvertLocalDataTime;
import com.migration.application.shared.CreateObject;
import com.migration.domain.Investor;
import com.migration.domain.Partner;
import com.migration.domain.User;
import com.migration.infrastructure.IInvestorRepository;
import com.migration.infrastructure.IPartnerRepository;
import com.migration.infrastructure.IPersonaRepository;
import com.migration.infrastructure.IUserRepository;
import org.hibernate.PropertyNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private IInvestorRepository investorRepository;

    @Autowired
    private IPartnerRepository partnerRepository;

    @Autowired
    private ConvertLocalDataTime convert;

    @Autowired
    private CreateObject create;

    public void findAll() {
        List<User> users = this.userRepository.findAll();
        System.out.println("Quantidade de Users  do banco: " + users.size());

       users.forEach(user -> {
           if(user.getPartner() != null){
               Partner partner = this.partnerRepository.findById(user.getPartner().getId())
                       .orElseThrow(()-> new PropertyNotFoundException("Partner not found!!"));
               user.setPersona(partner.getPersona());
           }

           if(user.getInvestor() != null){
               Investor investor = this.investorRepository.findById(user.getInvestor().getId())
                       .orElseThrow(()-> new PropertyNotFoundException("Investor not found!!"));
               user.setPersona(investor.getPersona());
           }

           this.save(user);
       });

    }

    @Transactional
    public void save (User user) {
        this.userRepository.save(user);
        System.out.println("Usuario atualizado " + user);
    }
}
