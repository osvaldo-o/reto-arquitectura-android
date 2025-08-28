package io.devexpert.splitbill.data.datasource.ticket

import io.devexpert.splitbill.TicketData

class MockTicketDataSource : TicketDataSource {
    override suspend fun processTicket(image: ByteArray): TicketData {
        return MockTicketData.getMockTicket()
    }
}