package com.github.keirbhaltair.currencyrates.model

import java.math.BigDecimal
import java.util.*

data class ExchangeRateDetails(
    val sourceCurrency: String,
    val targetCurrency: String,
    val date: Date,
    val cnbRate: BigDecimal,
    val exchangeApiRate: BigDecimal,
    val rateDifference: BigDecimal
)
