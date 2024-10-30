package com.ivy.exchangeRates

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.doesNotContain
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.ivy.MainCoroutineExtension
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.persistence.algorithm.calc.Rate
import com.ivy.exchangeRates.data.RateUi
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineExtension::class)
internal class RatesStateFlowTest {
    private lateinit var ratesStateFlow: RatesStateFlow
    private lateinit var ratesDaoFake: RatesDaoFake

    @BeforeEach
    fun setUp() {
        ratesDaoFake = RatesDaoFake()
        val baseCurrencyFlow = mockk<BaseCurrencyFlow>()
        every { baseCurrencyFlow() } returns flowOf("", "EUR")
        ratesStateFlow = RatesStateFlow(
            baseCurrencyFlow = baseCurrencyFlow,
            ratesDao = ratesDaoFake
        )
    }

    @Test
    fun `When collecting, check if the rates are overridden`() = runTest {
        ratesStateFlow().test {
            awaitItem() // Since the first default value is a empty string, only awaits for it.

            val emit1 = awaitItem()
            assertThat(emit1.baseCurrency).isEqualTo("EUR")
            assertThat(emit1.manual).contains(RateUi(from = "EUR", to = "EUR", rate = 15.0))
            assertThat(emit1.automatic).doesNotContain(RateUi(from = "CAD", to = "CAD", rate = 70.0))

            ratesDaoFake.overrides.value = emptyList()

            val emit2 = awaitItem()
            assertThat(emit2.baseCurrency).isEqualTo("EUR")
            assertThat(emit2.manual).isEmpty()
            assertThat(emit2.automatic).contains(RateUi(from = "EUR", to = "EUR", rate = 20.0))
        }
    }
}