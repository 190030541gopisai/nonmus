package com.nonmus.nonmus.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

//        mapper.createTypeMap(User.class, UserResponse.class)
//                .addMappings(m -> {
//                    m.map(User::getFirstName, UserResponse::setName);
//                });

        return mapper;
    }
}