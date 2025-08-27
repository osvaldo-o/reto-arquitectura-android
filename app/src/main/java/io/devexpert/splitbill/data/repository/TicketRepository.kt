package io.devexpert.splitbill.data.repository

import android.graphics.Bitmap
import io.devexpert.splitbill.TicketData
import io.devexpert.splitbill.data.datasource.ticket.TicketDataSource

class TicketRepository(
    private val ticketDataSource: TicketDataSource
) {
    suspend fun getTicket(image: Bitmap): TicketData {
        return ticketDataSource.processTicket(image)
    }
}