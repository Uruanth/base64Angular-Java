package com.file.base;

import com.file.base.usecase.DemoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseClaseConfig {

    @Bean
    public DemoUseCase useCase() {
        return new DemoUseCase();
    }

}
