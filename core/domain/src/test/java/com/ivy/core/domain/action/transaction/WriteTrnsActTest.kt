package com.ivy.core.domain.action.transaction

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.ivy.core.domain.algorithm.accountcache.InvalidateAccCacheAct
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.Value
import com.ivy.data.account.Account
import com.ivy.data.account.AccountState
import com.ivy.data.category.Category
import com.ivy.data.category.CategoryState
import com.ivy.data.category.CategoryType
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnMetadata
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnState
import com.ivy.data.transaction.TrnTime
import com.ivy.tag
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID


internal class WriteTrnsActTest {
    private lateinit var writeTransactionAction: WriteTrnsAct
    private lateinit var transactionDaoFake: TransactionDaoFake
    private lateinit var timeProviderFake: TimeProviderFake
    private lateinit var invalidateAccCacheAct: InvalidateAccCacheAct
    private lateinit var accountCacheDaoFake: AccountCacheDaoFake

    @BeforeEach
    fun setUp() {
        transactionDaoFake = TransactionDaoFake()
        timeProviderFake = TimeProviderFake()
        accountCacheDaoFake = AccountCacheDaoFake()
        invalidateAccCacheAct = InvalidateAccCacheAct(
            accountCacheDao = accountCacheDaoFake,
            timeProvider = timeProviderFake
        )
        writeTransactionAction = WriteTrnsAct(
            transactionDao = transactionDaoFake,
            trnsSignal = TrnsSignal(),
            timeProvider = timeProviderFake,
            invalidateAccCacheAct = invalidateAccCacheAct,
            accountCacheDao = accountCacheDaoFake
        )
    }

    @Test
    fun `Create new income, check if it was saved correctly`() = runBlocking {
        val uuid = UUID.randomUUID()
        val transaction = WriteTrnsAct.Input.CreateNew(
            trn = Transaction(
                id = uuid,
                account = Account(
                    id = UUID.randomUUID(),
                    name = "Marcos",
                    currency = "USD",
                    color = Color.Blue.toArgb(),
                    icon = "1231",
                    excluded = false,
                    folderId = UUID.randomUUID(),
                    orderNum = 21.5,
                    state = AccountState.Default,
                    sync = Sync(SyncState.Syncing, LocalDateTime.now())
                ),
                type = TransactionType.Income,
                value = Value(324.5, "USD"),
                category = Category(
                    id = UUID.randomUUID(),
                    name = "Salary",
                    color = Color.Blue.toArgb(),
                    icon = "1231",
                    orderNum = 21.5,
                    sync = Sync(SyncState.Syncing, LocalDateTime.now()),
                    type = CategoryType.Income,
                    parentCategoryId = UUID.randomUUID(),
                    state = CategoryState.Archived
                ),
                time = TrnTime.Actual(LocalDateTime.now()),
                title = null,
                description = null,
                state = TrnState.Default,
                purpose = TrnPurpose.Fee,
                tags = listOf(),
                attachments = listOf(),
                metadata = TrnMetadata(
                    recurringRuleId = UUID.randomUUID(),
                    loanId = UUID.randomUUID(),
                    loanRecordId = UUID.randomUUID()
                ),
                sync = Sync(SyncState.Syncing, LocalDateTime.now())
            )
        )
        writeTransactionAction(transaction)
        assertThat(transaction.trn.tags).isEmpty()
        assertThat(transaction.trn.sync.state).isEqualTo(SyncState.Syncing)
    }
}