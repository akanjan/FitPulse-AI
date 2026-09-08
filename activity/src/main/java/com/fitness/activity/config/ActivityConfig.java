package com.fitness.activity.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActivityConfig {
    @Bean
    public ModelMapper modelMapper()
    {
        return new ModelMapper();
    }
}
