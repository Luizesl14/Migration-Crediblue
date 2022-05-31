package com.migration.domain.persona.aggregation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.migration.domain.enums.UF;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "credi_address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String street;
    private String neighborhood;
    private String number;
    private String complement;
    private String cep;
    private String city;

    @Enumerated(EnumType.STRING)
    private UF uf;
    private String country;

    private String ibge;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;

    @JsonIgnore
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
