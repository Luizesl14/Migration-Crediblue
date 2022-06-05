package com.migration.application.core.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class GenericObjectMapper {

    @Autowired
    ModelMapper modelMapper;

    /**
     * Maps the source to destination class.
     *
     * @param source    Source.
     * @param destClass Destination class.
     * @return Instance of destination class.
     */
    public <S, D> D mapTo(S source, Class<D> destClass) {
        return this.modelMapper.map(source, destClass);
    }

    /**
     * Maps the list source to destination class.
     *
     * @param list      List source.
     * @param destClass Destination class.
     * @return Instance of destination class.
     */
    public <S, D> List<D> mapListTo(List<S> list, Class<D> destClass) {
        return list.stream()
                .map(s -> modelMapper.map(s, destClass))
                .collect(Collectors.toList());
    }
}
