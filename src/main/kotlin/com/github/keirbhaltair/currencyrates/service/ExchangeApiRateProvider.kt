package com.github.keirbhaltair.currencyrates.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.math.BigDecimal

const val API_BASE_URL_MAIN = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1"
const val API_BASE_URL_FALLBACK = "https://latest.currency-api.pages.dev/v1"

/**
 * Service providing currency exchange rate data from public API (currency-api / exchange-api) at:
 *   https://github.com/fawazahmed0/exchange-api
 */
@Service
class ExchangeApiRateProvider(
    @Qualifier("currencyExchangeObjectMapper") val objectMapper: ObjectMapper,
    val restTemplate: RestTemplate
) : CurrencyExchangeRateProvider {

    override fun getSupportedCurrencies(): Iterable<String> {
        val currencyMap = getObject<LinkedHashMap<String, String>>("/currencies.json")
        return currencyMap?.keys ?: emptySet()
    }

    override fun getSupportedCurrencyPairs(): Iterable<Pair<String, String>> {
        val currencies = getSupportedCurrencies()
        return currencies.flatMap { source -> currencies.map { source to it } }
    }

    override fun getExchangeRate(sourceCurrency: String, targetCurrency: String): BigDecimal? {
        val rates = getExchangeRates(sourceCurrency)
        return rates[targetCurrency.lowercase()]
    }

    private fun getExchangeRates(sourceCurrency: String): Map<String, BigDecimal> {
        val currencyLowerCase = sourceCurrency.lowercase()
        val responseData = getObject<String>("/currencies/$currencyLowerCase.json")

        if (responseData == null)
            return emptyMap()

        val currencyNode = objectMapper.readTree(responseData).get(currencyLowerCase)
        return try {
            objectMapper.convertValue<Map<String, BigDecimal>>(currencyNode)
        } catch (_: JsonProcessingException) {
            emptyMap()
        }
    }

    private inline fun <reified T> getObject(path: String): T? {
        return try {
            restTemplate.getForObject<T>("$API_BASE_URL_MAIN$path")
        } catch (_: RestClientException) {
            try {
                restTemplate.getForObject<T>("$API_BASE_URL_FALLBACK$path")
            } catch (_: RestClientException) {
                null
            }
        }
    }
}
