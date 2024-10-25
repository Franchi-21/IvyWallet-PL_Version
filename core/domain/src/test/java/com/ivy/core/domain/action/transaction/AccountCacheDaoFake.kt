package com.ivy.core.domain.action.transaction

import com.ivy.core.persistence.algorithm.accountcache.AccountCacheDao
import com.ivy.core.persistence.algorithm.accountcache.AccountCacheEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.time.Instant

private fun accountCacheEntityDefault() = AccountCacheEntity(
    accountId = "-1",
    incomesCount = Int.MIN_VALUE,
    expensesCount = Int.MIN_VALUE,
    timestamp = Instant.MIN,
    expensesJson = "",
    incomesJson = ""
)

class AccountCacheDaoFake : AccountCacheDao {
    private val account = MutableStateFlow(accountCacheEntityDefault())

    override fun findAccountCache(accountId: String): Flow<AccountCacheEntity?> {
       return account.filter { it.accountId == accountId }
    }

    override suspend fun findTimestampById(accountId: String): Instant? {
        val acc = account.value
        return if (acc.accountId == accountId) acc.timestamp else null
    }

    override suspend fun save(cache: AccountCacheEntity) {
        account.value = cache
    }

    override suspend fun delete(accountId: String) {
        account.value = accountCacheEntityDefault()
    }

    override suspend fun deleteAll() {
        account.value = accountCacheEntityDefault()
    }
}