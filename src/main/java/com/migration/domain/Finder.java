package com.migration.domain;

import com.migration.domain.enums.BankAccount;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
public class Finder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String name;
    private String cpf;
    private String telephone;
    private String email;

    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToOne
    @JoinColumn(name = "id")
    private BankAccount accountInfo;

    @OneToOne
    @JoinColumn(name = "persona_id")
    private Persona persona;

    @OneToOne
    @JoinColumn(name = "partner_id")
    private Partner partner;

    private boolean active = true;
    private LocalDateTime createdAt;
}
