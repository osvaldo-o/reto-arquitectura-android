package io.devexpert.splitbill.data.datasource.ticket

import android.graphics.Bitmap
import io.devexpert.splitbill.TicketData

interface TicketDataSource {

    suspend fun processTicket(image: Bitmap): TicketData

}