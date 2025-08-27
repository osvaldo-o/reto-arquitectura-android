package io.devexpert.splitbill.data

import android.graphics.Bitmap
import io.devexpert.splitbill.TicketData

class TicketRepository(
    private val ticketDataSource: TicketDataSource
) {
    suspend fun getTicket(image: Bitmap): TicketData {
        return ticketDataSource.processTicket(image)
    }
}