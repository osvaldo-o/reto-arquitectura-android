package io.devexpert.splitbill.domain.usecase

import io.devexpert.splitbill.data.repository.ScanCounterRepository

class InitializeOrResetScanCounterUseCase(
    private val scanCounterRepository: ScanCounterRepository
) {
    suspend operator fun invoke() {
        scanCounterRepository.initializeOrResetIfNeeded()
    }
}