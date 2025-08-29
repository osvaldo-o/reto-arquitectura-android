package io.devexpert.splitbill.domain.usecase

import io.devexpert.splitbill.data.repository.TicketRepository

class GetTicketUseCase(
    private val ticketRepository: TicketRepository
) {
    operator fun invoke() = ticketRepository.getTicketData()
}