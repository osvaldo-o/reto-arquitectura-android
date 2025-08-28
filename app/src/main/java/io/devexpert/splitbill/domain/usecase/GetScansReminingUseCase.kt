package io.devexpert.splitbill.domain.usecase

import io.devexpert.splitbill.data.repository.ScanCounterRepository
import kotlinx.coroutines.flow.Flow

class GetScansReminingUseCase(
    private val scanCounterRepository: ScanCounterRepository
) {
    operator fun invoke(): Flow<Int> = scanCounterRepository.scansRemaining
}