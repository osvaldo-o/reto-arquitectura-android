package io.devexpert.splitbill.domain.usecase

import io.devexpert.splitbill.domain.model.TicketData
import io.devexpert.splitbill.data.repository.TicketRepository

class ProcessTicketUseCase(
    private val ticketRepository: TicketRepository,
) {

    suspend operator fun invoke(image: ByteArray): TicketData {
        val result = ticketRepository.processTicket(image)
        return result
    }
}