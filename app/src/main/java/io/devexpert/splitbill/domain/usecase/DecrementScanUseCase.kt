package io.devexpert.splitbill.domain.usecase

import io.devexpert.splitbill.data.repository.ScanCounterRepository

class DecrementScanUseCase(
    private val scanCounterRepository: ScanCounterRepository
) {
    suspend operator fun invoke() {
        scanCounterRepository.decrementScan()
    }
}