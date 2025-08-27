package io.devexpert.splitbill.data.datasource.ticket

import android.graphics.Bitmap
import io.devexpert.splitbill.TicketData
import io.devexpert.splitbill.data.datasource.ticket.MockTicketData
import io.devexpert.splitbill.data.datasource.ticket.TicketDataSource

class MockTicketDataSource : TicketDataSource {
    override suspend fun processTicket(image: Bitmap): TicketData {
        return MockTicketData.getMockTicket()
    }
}