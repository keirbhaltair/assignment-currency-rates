package com.github.keirbhaltair.currencyrates.service

import com.github.keirbhaltair.currencyrates.model.ExchangeCurrencies
import com.github.keirbhaltair.currencyrates.model.ExchangeRateDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class CurrencyExchangeRatesService(
    val cnbExchangeRateProvider: CnbExchangeRateProvider,
    val exchangeApiRateProvider: ExchangeApiRateProvider
) {
    fun getSupportedCurrencyPairs(): List<ExchangeCurrencies> {
        val exchangeApiCurrencies = exchangeApiRateProvider.getSupportedCurrencies()
            .asSequence()
            .map { it.uppercase() }
            .toSet()

        val cnbPairs = cnbExchangeRateProvider.getSupportedCurrencyPairs()

        return cnbPairs.asSequence()
            .filter { exchangeApiCurrencies.contains(it.first) && exchangeApiCurrencies.contains(it.second) }
            .map { ExchangeCurrencies(it.first, it.second) }
            .toList()
    }

    fun getCurrencyRate(sourceCurrency: String, targetCurrency: String): ExchangeRateDetails? {
        val exchangeApiRate = exchangeApiRateProvider.getExchangeRate(sourceCurrency, targetCurrency)
        val cnbRate = cnbExchangeRateProvider.getExchangeRate(sourceCurrency, targetCurrency)

        if (exchangeApiRate == null || cnbRate == null)
            return null

        return ExchangeRateDetails(
            sourceCurrency,
            targetCurrency,
            Date(),
            cnbRate,
            exchangeApiRate,
            cnbRate - exchangeApiRate
        )
    }
}
