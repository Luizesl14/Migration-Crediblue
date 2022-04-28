package com.migration.domain.persona.aggregation;

import com.migration.domain.persona.Persona;
import com.migration.domain.enums.EmailType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ContactEmail {

    private Integer id;
    private String email;
    private Persona persona;
    private EmailType type;
    private Boolean principal;
    private Date createdAt;
}
