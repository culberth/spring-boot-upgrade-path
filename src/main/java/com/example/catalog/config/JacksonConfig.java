package com.example.catalog.config;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class JacksonConfig {

    /**
     * Serialize monetary amounts as strings so clients never round them through a
     * floating point type.
     */
    @Bean
    Jackson2ObjectMapperBuilderCustomizer decimalsAsStrings() {
        return builder -> builder.serializerByType(BigDecimal.class, ToStringSerializer.instance);
    }

}
