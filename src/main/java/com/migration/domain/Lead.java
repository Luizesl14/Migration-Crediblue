package com.migration.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.migration.domain.enums.MaritalStatus;
import com.migration.domain.persona.Persona;
import com.migration.domain.persona.aggregation.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "credi_lead")
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String telephone;
    private String email;

    @Column(name = "cpf_cnpj")
    private String cpfCnpj;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status")
    private MaritalStatus maritalStatus;

    @Column(name = "family_income")
    private BigDecimal familyIncome;

    @Column(name = "spouse_name")
    private String spouseName;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "home_address_id")
    private Address address;

    @JsonIgnore
    private boolean active = true;

    @JsonIgnore
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JsonIgnore
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private boolean isChecked = false;

    @ManyToOne
    @JoinColumn(name = "partner_id")
    private Partner partner;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_id")
    private Persona persona;


}
