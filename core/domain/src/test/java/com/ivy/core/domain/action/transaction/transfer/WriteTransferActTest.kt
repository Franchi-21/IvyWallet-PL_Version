package com.ivy.core.domain.action.transaction.transfer

import com.ivy.account
import com.ivy.core.domain.action.transaction.WriteTrnsAct
import com.ivy.core.domain.action.transaction.WriteTrnsBatchAct
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.Value
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnTime
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime


internal class WriteTransferActTest {
    private lateinit var writeTransactionAct: WriteTransferAct
    private lateinit var writeTrnsAct: WriteTrnsAct
    private lateinit var writeTrnsBatchAct: WriteTrnsBatchAct
    private lateinit var writeTransferByBatchIdAct: TransferByBatchIdAct

    @BeforeEach
    fun setUp() {
        writeTrnsAct = mockk(relaxed = true)
        writeTrnsBatchAct = mockk(relaxed = true)
        writeTransferByBatchIdAct = mockk(relaxed = true)
        writeTransactionAct = WriteTransferAct(
            writeTrnsAct = writeTrnsAct,
            writeTrnsBatchAct = writeTrnsBatchAct,
            transferByBatchIdAct = writeTransferByBatchIdAct
        )
    }

    @Test
    fun `Add transfer, fees are considered`() = runBlocking {
        writeTransactionAct(
            ModifyTransfer.Add(
                batchId = null,
                data = TransferData(
                    amountFrom = Value(amount = 50.0, currency = "EUR"),
                    amountTo = Value(amount = 55.5, currency = "AUD"),
                    accountTo = account().copy(name = "Test acc 1"),
                    accountFrom = account().copy(name = "Test acc 2"),
                    category = null,
                    time = TrnTime.Actual(LocalDateTime.now()),
                    title = null,
                    description = null,
                    fee = Value(20.0, "EUR"),
                    sync = Sync(SyncState.Syncing, LocalDateTime.now())
                )
            )
        )

        coVerify {
            writeTrnsBatchAct(
                match {
                    it as WriteTrnsBatchAct.ModifyBatch.Save

                    val from = it.batch.trns[0]
                    val to = it.batch.trns[1]
                    val fee = it.batch.trns[2]

                    from.value.amount == 50.0 && to.value.amount == 55.5 &&
                            fee.value.amount == 20.0 && fee.type == TransactionType.Expense
                }
            )
        }
    }
}