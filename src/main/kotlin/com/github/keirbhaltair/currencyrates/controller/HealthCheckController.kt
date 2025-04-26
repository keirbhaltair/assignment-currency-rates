package com.github.keirbhaltair.currencyrates.controller

import com.github.keirbhaltair.currencyrates.model.HealthResponse
import com.github.keirbhaltair.currencyrates.service.CnbExchangeRateProvider
import com.github.keirbhaltair.currencyrates.service.CurrencyExchangeRateProvider
import com.github.keirbhaltair.currencyrates.service.ExchangeApiRateProvider
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class HealthCheckController(
    val cnbExchangeRateProvider: CnbExchangeRateProvider,
    val exchangeApiRateProvider: ExchangeApiRateProvider
) {

    @GetMapping("/health")
    fun healthCheck(): HealthResponse {
        val cnbApiStatus = checkStatus(cnbExchangeRateProvider)
        val exchangeApiStatus = checkStatus(exchangeApiRateProvider)
        val overallStatus = cnbApiStatus && exchangeApiStatus

        return HealthResponse(
            translateStatus(overallStatus),
            translateStatus(true),
            translateStatus(cnbApiStatus),
            translateStatus(exchangeApiStatus)
        )
    }

    private fun checkStatus(provider: CurrencyExchangeRateProvider): Boolean {
        return try {
            provider.getSupportedCurrencies().any()
        } catch (_: Exception) {
            false
        }
    }

    private fun translateStatus(active: Boolean) = if (active) "OK" else "Error"
}
