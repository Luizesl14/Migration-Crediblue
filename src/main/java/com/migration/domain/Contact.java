package com.migration.domain;

import com.migration.domain.enums.ContactType;
import com.migration.domain.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Entity
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
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
