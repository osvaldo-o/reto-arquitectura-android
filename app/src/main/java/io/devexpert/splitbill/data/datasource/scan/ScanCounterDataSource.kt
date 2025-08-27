package io.devexpert.splitbill.data.datasource.scan

import kotlinx.coroutines.flow.Flow

interface ScanCounterDataSource {

    val scansRemaining: Flow<Int>

    suspend fun initializeOrResetIfNeeded()

    suspend fun decrementScan()

}