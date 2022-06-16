package com.migration.domain.persona;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.migration.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "credi_user_role")
public class UserRole  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Transient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User user;

    @Transient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "role_id", updatable = false, insertable = false)
    private Role role;
}
