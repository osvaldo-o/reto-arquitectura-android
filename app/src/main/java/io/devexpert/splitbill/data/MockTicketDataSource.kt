package io.devexpert.splitbill.data

import android.graphics.Bitmap
import io.devexpert.splitbill.TicketData

class MockTicketDataSource : TicketDataSource {
    override suspend fun processTicket(image: Bitmap): TicketData {
        return MockTicketData.getMockTicket()
    }
}