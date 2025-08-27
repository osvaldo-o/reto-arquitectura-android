package io.devexpert.splitbill.data.repository

import io.devexpert.splitbill.data.datasource.scan.ScanCounterDataSource
import kotlinx.coroutines.flow.Flow

class ScanCounterRepository(
    private val scanCounterDataSource: ScanCounterDataSource
) {

    val scansRemaining: Flow<Int> = scanCounterDataSource.scansRemaining

    suspend fun initializeOrResetIfNeeded() {
        scanCounterDataSource.initializeOrResetIfNeeded()
    }

    suspend fun decrementScan() {
        scanCounterDataSource.decrementScan()
    }

}