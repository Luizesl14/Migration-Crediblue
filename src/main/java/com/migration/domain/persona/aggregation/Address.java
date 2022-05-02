package com.migration.domain.persona.aggregation;

import com.migration.domain.enums.UF;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String street;
    private String neighborhood;
    private String number;
    private String complement;
    private String cep;
    private String city;
    private UF uf;
    private String country;
    private String ibge;

    @OneToOne
    @JoinColumn(name = "location_id")
    private Location location;
    private LocalDateTime createdAt;
}
