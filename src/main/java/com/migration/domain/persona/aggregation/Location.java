package com.migration.domain.persona.aggregation;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Location {

    private Integer id;
    private String lat;
    private String lng;
    private LocalDateTime createdAt;
}
