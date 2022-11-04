package com.example.cargas.archivo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Configuracion {
    @Bean
    public UseCase useCase() {return new UseCase();}
}
