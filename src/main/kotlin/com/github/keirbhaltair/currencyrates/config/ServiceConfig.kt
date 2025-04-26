package com.github.keirbhaltair.currencyrates.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.StdDateFormat
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class ServiceConfig {

    @Bean
    fun currencyExchangeObjectMapper(): ObjectMapper {
        return ObjectMapper()
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setDateFormat(StdDateFormat().withColonInTimeZone(true))
    }

    @Bean
    fun restTemplate() = RestTemplate()

}
