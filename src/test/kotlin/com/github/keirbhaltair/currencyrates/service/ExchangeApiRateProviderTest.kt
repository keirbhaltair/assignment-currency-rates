package com.github.keirbhaltair.currencyrates.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import kotlin.test.assertEquals

@SpringBootTest
class ExchangeApiRateProviderTest {

    @MockitoBean
    lateinit var restTemplate: RestTemplate

    @Autowired
    lateinit var rateProvider: ExchangeApiRateProvider

    @BeforeEach
    fun setUp() {
        Mockito.`when`(restTemplate.getForObject("$API_BASE_URL_MAIN/currencies.json", LinkedHashMap::class.java))
            .thenReturn(
                linkedMapOf(
                    "eur" to "Euro",
                    "czk" to "Czech Koruna",
                    "jpy" to "Japanese Yen",
                )
            )

        Mockito.`when`(restTemplate.getForObject("$API_BASE_URL_MAIN/currencies/eur.json", String::class.java))
            .thenReturn(
                """
                    {
                        "date": "2025-04-24",
                        "eur": {
                            "eur": 1,
                            "czk": 25.00459065,
                            "jpy": 162.10496228
                        }
                    }
                """.trimIndent()
            )

        Mockito.`when`(
            restTemplate.getForObject(
                Mockito.argThat<String> { it != "$API_BASE_URL_MAIN/currencies/eur.json" },
                Mockito.eq(String::class.java)
            )
        ).thenThrow(HttpClientErrorException(HttpStatus.NOT_FOUND))
    }

    @Test
    fun getSupportedCurrencies() {
        val list = rateProvider.getSupportedCurrencies().toList()
        assertEquals(listOf("eur", "czk", "jpy"), list)
    }

    @Test
    fun getSupportedCurrencyPairs() {
        val list = rateProvider.getSupportedCurrencyPairs().toList()
        assertEquals(
            listOf(
                "eur" to "eur",
                "eur" to "czk",
                "eur" to "jpy",
                "czk" to "eur",
                "czk" to "czk",
                "czk" to "jpy",
                "jpy" to "eur",
                "jpy" to "czk",
                "jpy" to "jpy",
            ), list
        )
    }

    @Test
    fun getExchangeRate() {
        assertEquals(BigDecimal("25.00459065"), rateProvider.getExchangeRate("eur", "czk"))
        assertEquals(BigDecimal("162.10496228"), rateProvider.getExchangeRate("EUR", "JPY"))
    }

    @Test
    fun getExchangeRate_errorInvalidCurrency() {
        assertEquals(null, rateProvider.getExchangeRate("ASD", "FGH"))
    }

}
