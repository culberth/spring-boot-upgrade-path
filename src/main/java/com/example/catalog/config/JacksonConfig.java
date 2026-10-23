package com.example.catalog.config;

import java.math.BigDecimal;

import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class JacksonConfig {

    /**
     * Serialize monetary amounts as strings so clients never round them through a
     * floating point type.
     */
    @Bean
    JsonMapperBuilderCustomizer decimalsAsStrings() {
        return builder -> builder.addModule(
                new SimpleModule("decimals-as-strings").addSerializer(BigDecimal.class, ToStringSerializer.instance));
    }

}
