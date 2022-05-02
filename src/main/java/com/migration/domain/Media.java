package com.migration.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Media {

    private Integer id;
    private String type;
    private String uri;
    private String filename;
    private Long size;
    private LocalDateTime createdAt;

}
