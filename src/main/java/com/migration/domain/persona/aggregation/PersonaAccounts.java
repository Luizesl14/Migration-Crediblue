package com.migration.domain.persona.aggregation;

import com.migration.domain.persona.Persona;
import com.migration.domain.enums.BankAccount;
import com.migration.domain.enums.BankAccountType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@Entity
public class PersonaAccounts {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "persona_id")
    private Persona persona;

    @OneToOne
    @JoinColumn(name = "account_id")
    private BankAccount account;

    private BankAccountType type;
    private Boolean principal;
    private Date createdAt;
}
