package com.ivy.core.domain.action.transaction

import androidx.sqlite.db.SupportSQLiteQuery
import com.ivy.core.persistence.dao.trn.AccountIdAndTrnTime
import com.ivy.core.persistence.dao.trn.TransactionDao
import com.ivy.core.persistence.entity.attachment.AttachmentEntity
import com.ivy.core.persistence.entity.trn.TransactionEntity
import com.ivy.core.persistence.entity.trn.TrnMetadataEntity
import com.ivy.core.persistence.entity.trn.TrnTagEntity
import com.ivy.core.persistence.entity.trn.data.TrnTimeType
import com.ivy.data.SyncState
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnState
import com.ivy.tag
import java.time.Instant


private fun transactionEntityDefault() = TransactionEntity(
    id = "",
    accountId = "",
    type = TransactionType.Income,
    amount = 0.0,
    currency = "",
    time = Instant.MIN,
    timeType = TrnTimeType.Actual,
    title = "",
    description = "",
    categoryId = "",
    state = TrnState.Default,
    purpose = TrnPurpose.TransferTo,
    sync = SyncState.Syncing,
    lastUpdated = Instant.MIN
)

class TransactionDaoFake : TransactionDao() {
    private var transactions = mutableListOf<TransactionEntity>()
    private var tags = mutableListOf<TrnTagEntity>()
    private var attachments = mutableListOf<AttachmentEntity>()
    private var metadata = mutableListOf<TrnMetadataEntity>()

    override suspend fun saveTrnEntity(entity: TransactionEntity) {
        transactions.add(entity)
    }

    override suspend fun updateTrnTagsSyncByTrnId(trnId: String, sync: SyncState) {
        val transaction = tags.find { trnId == it.trnId } ?: return
        val index = tags.indexOf(transaction)
        tags[index] = transaction.copy(sync = sync)
    }

    override suspend fun saveTags(entity: List<TrnTagEntity>) {
        tags.addAll(entity)
    }

    override suspend fun updateAttachmentsSyncByAssociatedId(
        associatedId: String,
        sync: SyncState
    ) {
        val attachment = attachments.find { it.associatedId == associatedId } ?: return
        val index = attachments.indexOf(attachment)
        attachments[index] = attachment.copy(sync = sync)
    }

    override suspend fun saveAttachments(entity: List<AttachmentEntity>) {
        attachments.addAll(entity)
    }

    override suspend fun updateMetadataSyncByTrnId(trnId: String, sync: SyncState) {
        val metadata = metadata.find { it.trnId == trnId } ?: return
        val index = this.metadata.indexOf(metadata)
        this.metadata[index] = metadata.copy(sync = sync)
    }

    override suspend fun saveMetadata(entity: List<TrnMetadataEntity>) {
        metadata.addAll(entity)
    }

    override suspend fun findAllBlocking(): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findBySQL(query: SupportSQLiteQuery): List<TransactionEntity> {
        return transactions
    }

    override suspend fun findAccountIdAndTimeById(trnId: String): AccountIdAndTrnTime? {
        val acc = transactions.find { it.id == trnId } ?: return null
        return AccountIdAndTrnTime(
            accountId = acc.accountId,
            time = acc.time,
            timeType = acc.timeType
        )
    }

    override suspend fun updateTrnEntitySyncById(trnId: String, sync: SyncState) {
        val transaction = transactions.find { trnId == it.id } ?: return
        val index = transactions.indexOf(transaction)
        transactions[index] = transaction.copy(sync = sync)
    }
}