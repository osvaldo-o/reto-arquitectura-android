package io.devexpert.splitbill.data.repository

import io.devexpert.splitbill.domain.model.TicketData
import io.devexpert.splitbill.data.datasource.ticket.TicketDataSource

class TicketRepository(
    private val ticketDataSource: TicketDataSource
) {

    private var _ticketData: TicketData? = null

    suspend fun processTicket(image: ByteArray): TicketData {
        val ticket = ticketDataSource.processTicket(image)
        _ticketData = ticket
        return ticket
    }

    fun getTicketData(): TicketData? {
        return _ticketData
    }

}