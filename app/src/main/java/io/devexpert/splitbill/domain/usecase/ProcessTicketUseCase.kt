package io.devexpert.splitbill.domain.usecase

import io.devexpert.splitbill.TicketData
import io.devexpert.splitbill.data.repository.ScanCounterRepository
import io.devexpert.splitbill.data.repository.TicketRepository

class ProcessTicketUseCase(
    private val ticketRepository: TicketRepository,
    private val scanCounterRepository: ScanCounterRepository
) {

    suspend operator fun invoke(image: ByteArray): TicketData {
        val result = ticketRepository.getTicket(image)
        scanCounterRepository.decrementScan()
        return result
    }
}