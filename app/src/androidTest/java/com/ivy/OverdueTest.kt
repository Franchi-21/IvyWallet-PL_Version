package com.ivy

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.ivy.common.androidtest.IvyAndroidTest
import com.ivy.common.androidtest.test_data.saveAccountWithTransactions
import com.ivy.common.androidtest.test_data.transactionWithTime
import com.ivy.wallet.ui.RootActivity
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import java.time.Instant

@HiltAndroidTest
class OverdueTest : IvyAndroidTest() {
    @get:Rule
    val composeRule = createAndroidComposeRule<RootActivity>()

    @Test
    fun overdueTransactionsAreShownCorrectly() = runBlocking<Unit> {
        val trn1 = transactionWithTime(Instant.parse("2023-11-01T09:00:00Z")).copy(
            title = "OverdueTransaction1"
        )

        db.saveAccountWithTransactions(transactions = listOf(trn1))

        composeRule.awaitIdle()
        composeRule.onNodeWithText("Overdue", useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithText("OverdueTransaction1", useUnmergedTree = true).assertIsDisplayed()
    }
}