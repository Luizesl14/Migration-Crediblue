package com.migration.domain;

import com.migration.domain.persona.Persona;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.management.relation.Role;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "credi_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "persona_id")
    private Persona persona;

    private String name;
    private String cpf;
    private String telephone;
    private String login;
    private String email;
    private String password;
    private Boolean active = true;
    private Boolean blocked = false;
    private LocalDateTime createdAt;


    @OneToOne
    @JoinColumn(name = "partner_id")
    private Partner partner;

    @OneToOne
    @JoinColumn(name = "investor_id")
    private Investor investor;

}
