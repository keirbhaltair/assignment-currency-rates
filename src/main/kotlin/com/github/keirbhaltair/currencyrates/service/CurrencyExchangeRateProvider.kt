package com.github.keirbhaltair.currencyrates.service

import java.math.BigDecimal

interface CurrencyExchangeRateProvider {
    fun getSupportedCurrencies(): Iterable<String>
    fun getSupportedCurrencyPairs(): Iterable<Pair<String, String>>
    fun getExchangeRate(sourceCurrency: String, targetCurrency: String): BigDecimal?
}
