package com.github.keirbhaltair.currencyrates.model

data class HealthResponse(
    val overallStatus: String,

    val thisAppStatus: String,
    val cnbApiStatus: String,
    val exchangeApiStatus: String
)
