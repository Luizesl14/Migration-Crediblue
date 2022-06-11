package com.migration.domain.persona.aggregation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "credi_phone")
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "number")
    private String number;

    @Column(name = "is_whatsapp")
    private Boolean isWhatsApp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phone phone = (Phone) o;
        return Objects.equals(number, phone.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
