package com.migration.domain;

import com.migration.domain.enums.ContactType;
import com.migration.domain.enums.Gender;

import java.time.LocalDate;

public class Contact {

    private Integer id;
    private String name;
    private String cpf;
    private String email;
    private String telephone;
    private String role;
    private ContactType contactType;
    private LocalDate birthDate;
    private Gender gender;
    private String bestTimeToCall;
    private String bestContactCanal;
    private String instagram;
    private String facebook;
    private String linkedin;
}
