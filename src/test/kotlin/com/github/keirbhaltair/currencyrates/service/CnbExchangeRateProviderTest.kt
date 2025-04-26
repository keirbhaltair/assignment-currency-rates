package com.github.keirbhaltair.currencyrates.service

import com.github.keirbhaltair.currencyrates.model.CnbRateRow
import com.github.keirbhaltair.currencyrates.model.CnbRateTable
import com.github.keirbhaltair.currencyrates.model.CnbRates
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import kotlin.test.assertEquals

@SpringBootTest
class CnbExchangeRateProviderTest {

    @MockitoBean
    lateinit var restTemplate: RestTemplate

    @Autowired
    lateinit var rateProvider: CnbExchangeRateProvider

    @BeforeEach
    fun setUp() {
        val rates = CnbRates(
            CnbRateTable(
                listOf(
                    CnbRateRow("EUR", "euro", 1, BigDecimal("24.920"), "EMU"),
                    CnbRateRow("USD", "dolar", 1, BigDecimal("21.950"), "USA"),
                    CnbRateRow("JPY", "jen", 100, BigDecimal("15.315"), "Japonsko"),
                )
            )
        )

        Mockito.`when`(restTemplate.getForObject(CNB_RATES_URL, CnbRates::class.java))
            .thenReturn(rates)
    }

    @Test
    fun getSupportedCurrencies() {
        val list = rateProvider.getSupportedCurrencies().toList()
        assertEquals(listOf("EUR", "USD", "JPY"), list)
    }

    @Test
    fun getSupportedCurrencyPairs() {
        val list = rateProvider.getSupportedCurrencyPairs().toList()
        assertEquals(
            listOf(
                "EUR" to "CZK",
                "CZK" to "EUR",
                "USD" to "CZK",
                "CZK" to "USD",
                "JPY" to "CZK",
                "CZK" to "JPY"
            ), list
        )
    }

    @Test
    fun getExchangeRate_toCzk() {
        assertEquals(BigDecimal("24.920"), rateProvider.getExchangeRate("eur", "czk"))
        assertEquals(BigDecimal("0.15315"), rateProvider.getExchangeRate("JPY", "CZK"))
    }

    @Test
    fun getExchangeRate_fromCzk() {
        assertEquals(BigDecimal("0.04012841"), rateProvider.getExchangeRate("czk", "eur"))
        assertEquals(BigDecimal("6.529546"), rateProvider.getExchangeRate("CZK", "JPY"))
    }

    @Test
    fun getExchangeRate_errorInvalidCurrency() {
        assertEquals(null, rateProvider.getExchangeRate("ASD", "CZK"))
        assertEquals(null, rateProvider.getExchangeRate("CZK", "ZXC"))
        assertEquals(null, rateProvider.getExchangeRate("QWE", "RTY"))
    }
}
