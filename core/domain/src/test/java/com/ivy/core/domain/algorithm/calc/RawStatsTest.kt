package com.ivy.core.domain.algorithm.calc

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ivy.core.persistence.algorithm.calc.CalcTrn
import com.ivy.data.transaction.TransactionType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

internal class RawStatsTest {
    private lateinit var transactions: List<CalcTrn>

    @BeforeEach
    fun setUp() {
        transactions = listOf(
            CalcTrn(
                amount = 20.0,
                type = TransactionType.Income,
                currency = "USD",
                time = Instant.ofEpochMilli(1_000_000)
            ),
            CalcTrn(
                amount = 30.0,
                type = TransactionType.Income,
                currency = "USD",
                time = Instant.MIN
            ),
            CalcTrn(
                amount = -20.0,
                type = TransactionType.Expense,
                currency = "USD",
                time = Instant.MIN
            ),
            CalcTrn(
                amount = -50.0,
                type = TransactionType.Expense,
                currency = "USD",
                time = Instant.MIN
            )
        )
    }

    @Test
    fun `When sending a list of transactions, check if every field is correct`() {
        val result = rawStats(transactions)

        assertThat(result.incomes).isEqualTo(mapOf("USD" to 50.0))
        assertThat(result.expenses).isEqualTo(mapOf("USD" to -70.0))
        assertThat(result.incomesCount).isEqualTo(2)
        assertThat(result.expensesCount).isEqualTo(2)
        assertThat(result.newestTrnTime).isEqualTo(Instant.ofEpochMilli(1_000_000))
    }
}