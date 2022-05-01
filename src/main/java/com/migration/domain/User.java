package com.migration.domain;

import com.migration.domain.persona.Persona;
import lombok.Getter;
import lombok.Setter;

import javax.management.relation.Role;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class User {

    private Integer id;
    private Persona persona;
    private String login;
    private String email;
    private String password;
    private Boolean active = true;
    private Boolean blocked = false;
    private LocalDateTime createdAt;
    private List<Role> roles;
    private Partner partner;
    private Investor investor;
    private Media photo;
}
