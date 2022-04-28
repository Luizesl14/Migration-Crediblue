package com.migration.domain;

import com.migration.domain.enums.ParameterCategoryType;
import com.migration.domain.enums.ParameterType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Parameter {

    private Integer id;
    private String key;
    private String value;
    private String description;
    private ParameterType type;
    private LocalDateTime createdAt;
    private ParameterCategoryType categoryType;
}
