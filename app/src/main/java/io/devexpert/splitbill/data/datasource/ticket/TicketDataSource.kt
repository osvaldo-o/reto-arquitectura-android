package io.devexpert.splitbill.data.datasource.ticket

import io.devexpert.splitbill.domain.model.TicketData

interface TicketDataSource {

    suspend fun processTicket(image: ByteArray): TicketData

}