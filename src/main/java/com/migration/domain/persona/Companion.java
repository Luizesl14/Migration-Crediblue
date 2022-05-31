package com.migration.domain.persona;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;



@Getter
@Setter
@Entity
@Table(name = "credi_companion")
public class Companion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "rg")
    private String rg;

    @Column(name = "orgao_emissor")
    private String orgaoEmissor;

    @Column(name = "cpf")
    private String cpf;

    @Column(name = "email")
    private String email;

    @Column(name = "mother_name")
    private String motherName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "pep")
    private Boolean pep;
}
