package com.migration.application.shared;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class ConvertLocalDataTime {


    public LocalDateTime covertDate(Date date){
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public Date covertLocalDataTimeToDate(LocalDateTime localDateTime){
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


    public Date convertToLocalDate(LocalDate localDate){
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}

