package com.migration.domain.persona.aggregation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.migration.domain.persona.Persona;
import com.migration.domain.enums.BankAccount;
import com.migration.domain.enums.BankAccountType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "credi_persona_account")
public class PersonaAccounts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_id")
    private Persona persona;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_account_id")
    private BankAccount account;

    @Column(name = "type")
    private BankAccountType type;

    @Column(name = "principal")
    private Boolean principal;

    @Column(name = "created_at")
    private Date createdAt;

    @PrePersist
    public void persist() {
        this.createdAt = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonaAccounts that = (PersonaAccounts) o;
        return Objects.equals(persona, that.persona) && Objects.equals(account, that.account);
    }

}