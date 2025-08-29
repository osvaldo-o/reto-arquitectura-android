package io.devexpert.splitbill.domain.usecase

import io.devexpert.splitbill.domain.model.TicketData
import io.devexpert.splitbill.data.repository.ScanCounterRepository
import io.devexpert.splitbill.data.repository.TicketRepository

class ProcessTicketUseCase(
    private val ticketRepository: TicketRepository,
    private val scanCounterRepository: ScanCounterRepository
) {

    suspend operator fun invoke(image: ByteArray): TicketData {
        val result = ticketRepository.processTicket(image)
        scanCounterRepository.decrementScan()
        return result
    }
}