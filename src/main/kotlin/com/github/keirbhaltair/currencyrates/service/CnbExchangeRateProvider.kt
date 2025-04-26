package com.github.keirbhaltair.currencyrates.service

import com.github.keirbhaltair.currencyrates.model.CnbRateRow
import com.github.keirbhaltair.currencyrates.model.CnbRates
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.math.BigDecimal
import java.math.MathContext

const val CNB_RATES_URL = "https://www.cnb.cz/cs/financni_trhy/devizovy_trh/kurzy_devizoveho_trhu/denni_kurz.xml"
const val CZK_CURRENCY = "CZK"

/**
 * Service providing currency exchange rate data from the public API of Czech National Bank (CNB).
 */
@Service
class CnbExchangeRateProvider(val restTemplate: RestTemplate) : CurrencyExchangeRateProvider {

    override fun getSupportedCurrencies(): Iterable<String> {
        val rates = loadRates()
        return rates.map { it.code }
    }

    override fun getSupportedCurrencyPairs(): Iterable<Pair<String, String>> {
        val rates = loadRates()
        return rates.flatMap { listOf(it.code to CZK_CURRENCY, CZK_CURRENCY to it.code) }
    }

    override fun getExchangeRate(sourceCurrency: String, targetCurrency: String): BigDecimal? {
        val sourceCurrencyUpperCase = sourceCurrency.uppercase()
        val targetCurrencyUpperCase = targetCurrency.uppercase()

        return when {
            sourceCurrencyUpperCase == CZK_CURRENCY -> getExchangeRateFromCzk(targetCurrencyUpperCase)
            targetCurrencyUpperCase == CZK_CURRENCY -> getExchangeRateToCzk(sourceCurrencyUpperCase)
            else -> null
        }
    }

    private fun getExchangeRateToCzk(sourceCurrency: String): BigDecimal? {
        val rates = loadRates()
        return rates
            .find { it.code == sourceCurrency }
            ?.let { it.rate.divide(BigDecimal(it.amount)) }
    }

    private fun getExchangeRateFromCzk(targetCurrency: String): BigDecimal? {
        val toRate = getExchangeRateToCzk(targetCurrency)

        return when {
            toRate == null || toRate == BigDecimal.ZERO -> null
            else -> BigDecimal.ONE.divide(toRate, MathContext.DECIMAL32)
        }
    }

    private fun loadRates(): List<CnbRateRow> = restTemplate.getForObject<CnbRates>(CNB_RATES_URL).table.rows
}
