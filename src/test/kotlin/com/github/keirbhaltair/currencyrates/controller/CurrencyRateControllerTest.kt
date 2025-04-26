package com.github.keirbhaltair.currencyrates.controller

import com.github.keirbhaltair.currencyrates.model.CnbRateRow
import com.github.keirbhaltair.currencyrates.model.CnbRateTable
import com.github.keirbhaltair.currencyrates.model.CnbRates
import com.github.keirbhaltair.currencyrates.model.ExchangeCurrencies
import com.github.keirbhaltair.currencyrates.service.API_BASE_URL_MAIN
import com.github.keirbhaltair.currencyrates.service.CNB_RATES_URL
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest
class CurrencyRateControllerTest {

    @MockitoBean
    lateinit var restTemplate: RestTemplate

    @Autowired
    lateinit var controller: CurrencyRateController

    @BeforeEach
    fun setUp() {
        Mockito.`when`(restTemplate.getForObject(CNB_RATES_URL, CnbRates::class.java))
            .thenReturn(
                CnbRates(
                    CnbRateTable(
                        listOf(
                            CnbRateRow("EUR", "euro", 1, BigDecimal("24.920"), "EMU"),
                        )
                    )
                )
            )

        Mockito.`when`(restTemplate.getForObject("$API_BASE_URL_MAIN/currencies.json", LinkedHashMap::class.java))
            .thenReturn(
                linkedMapOf(
                    "eur" to "Euro",
                    "czk" to "Czech Koruna",
                )
            )

        Mockito.`when`(restTemplate.getForObject("$API_BASE_URL_MAIN/currencies/eur.json", String::class.java))
            .thenReturn(
                """
                    {
                        "date": "2025-04-24",
                        "eur": {
                            "czk": 25.00459065
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
    fun getCurrencyPairs() {
        val pairs = controller.getCurrencyPairs()
        assertEquals(
            listOf(
                ExchangeCurrencies("EUR", "CZK"),
                ExchangeCurrencies("CZK", "EUR")
            ), pairs
        )
    }

    @Test
    fun getCurrencyRate() {
        val response = controller.getCurrencyRate("EUR", "CZK")
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertAll(
            { assertEquals("EUR", response.body?.sourceCurrency) },
            { assertEquals("CZK", response.body?.targetCurrency) },
            { assertNotNull(response.body?.date) },
            { assertEquals(BigDecimal("24.920"), response.body?.cnbRate) },
            { assertEquals(BigDecimal("25.00459065"), response.body?.exchangeApiRate) },
            { assertEquals(BigDecimal("-0.08459065"), response.body?.rateDifference) },
        )
    }

    @Test
    fun getCurrencyRate_errorInvalidCurrency() {
        val response = controller.getCurrencyRate("ASD", "FGH")
        assertNotNull(response)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }
}
