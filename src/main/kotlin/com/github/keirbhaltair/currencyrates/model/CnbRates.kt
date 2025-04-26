package com.github.keirbhaltair.currencyrates.model

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import java.math.BigDecimal

@JacksonXmlRootElement(localName = "kurzy")
data class CnbRates(
    @JacksonXmlProperty(localName = "tabulka")
    val table: CnbRateTable
)

data class CnbRateTable(
    @JacksonXmlProperty(localName = "radek")
    @JacksonXmlElementWrapper(useWrapping = false)
    val rows: List<CnbRateRow>
)

data class CnbRateRow(
    @JacksonXmlProperty(isAttribute = true, localName = "kod")
    val code: String,

    @JacksonXmlProperty(isAttribute = true, localName = "mena")
    val currency: String,

    @JacksonXmlProperty(isAttribute = true, localName = "mnozstvi")
    val amount: Int,

    @JacksonXmlProperty(isAttribute = true, localName = "kurz")
    @JsonDeserialize(using = CnbRateDecimalDeserializer::class)
    val rate: BigDecimal,

    @JacksonXmlProperty(isAttribute = true, localName = "zeme")
    val country: String
)

private class CnbRateDecimalDeserializer : JsonDeserializer<BigDecimal>() {
    override fun deserialize(parser: JsonParser?, context: DeserializationContext?): BigDecimal? {
        return parser?.text?.replace(",", ".")?.toBigDecimal()
    }
}
