package com.migration.domain;

import com.migration.domain.enums.MaritalStatus;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String name;
    private LocalDate birthDate;
    private String telephone;
    private String email;
    private String cpfCnpj;
    private MaritalStatus maritalStatus;
    private BigDecimal familyIncome;
    private String spouseName;

    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;

    private boolean active = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isChecked = false;

    @OneToOne
    @JoinColumn(name = "persona_id")
    private Persona persona;

    @OneToOne
    @JoinColumn(name = "pertner_id")
    private Partner partner;
}
