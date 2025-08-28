package io.devexpert.splitbill.data.datasource.ticket

import io.devexpert.splitbill.TicketData

interface TicketDataSource {

    suspend fun processTicket(image: ByteArray): TicketData

}