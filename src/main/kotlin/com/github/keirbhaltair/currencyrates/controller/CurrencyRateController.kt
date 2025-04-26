package com.github.keirbhaltair.currencyrates.controller

import com.github.keirbhaltair.currencyrates.model.ExchangeCurrencies
import com.github.keirbhaltair.currencyrates.model.ExchangeRateDetails
import com.github.keirbhaltair.currencyrates.service.CurrencyExchangeRatesService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/currencies", produces = [MediaType.APPLICATION_JSON_VALUE])
class CurrencyRateController(
    val ratesService: CurrencyExchangeRatesService
) {
    @GetMapping("/pairs")
    fun getCurrencyPairs(): List<ExchangeCurrencies> {
        return ratesService.getSupportedCurrencyPairs()
    }

    @GetMapping("/rates/{sourceCurrency}/{targetCurrency}")
    fun getCurrencyRate(
        @PathVariable sourceCurrency: String,
        @PathVariable targetCurrency: String
    ): ResponseEntity<ExchangeRateDetails> {
        val rate = ratesService.getCurrencyRate(sourceCurrency, targetCurrency)
        return ResponseEntity.ofNullable(rate)
    }
}
