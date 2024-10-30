package com.ivy.exchangeRates

import com.ivy.core.persistence.algorithm.calc.Rate
import com.ivy.core.persistence.algorithm.calc.RatesDao
import com.ivy.data.CurrencyCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class RatesDaoFake: RatesDao {
    val rates = MutableStateFlow(
        listOf(
            Rate(currency = "EUR", rate = 20.0),
            Rate(currency = "USD", rate = 50.0),
            Rate(currency = "CAD", rate = 70.0),
        )
    )

    val overrides = MutableStateFlow(
        listOf(
            Rate(currency = "EUR", rate = 15.0),
            Rate(currency = "USD", rate = 17.0),
        )
    )

    override fun findAll(baseCurrency: CurrencyCode): Flow<List<Rate>> {
        return rates.map { rate ->
            rate.filter { it.currency == baseCurrency }
        }
    }

    override fun findAllOverrides(baseCurrency: CurrencyCode): Flow<List<Rate>> {
        return overrides.map { rate ->
            rate.filter { it.currency == baseCurrency }
        }
    }

}